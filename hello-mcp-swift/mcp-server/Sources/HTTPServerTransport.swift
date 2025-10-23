import Foundation
import MCP
import Logging
import Network

/// HTTP 服务器传输层，支持 SSE 流式传输
public actor HTTPServerTransport: Transport {
    public nonisolated let logger: Logger
    private var isConnected = false
    private let messageStream: AsyncThrowingStream<Data, any Swift.Error>
    private let messageContinuation: AsyncThrowingStream<Data, any Swift.Error>.Continuation
    private let port: Int
    private var listener: NWListener?
    private var connections: [NWConnection] = []
    private var currentConnection: NWConnection?
    
    public init(port: Int, logger: Logger? = nil) {
        self.port = port
        self.logger = logger ?? Logger(label: "mcp.transport.http")
        
        var continuation: AsyncThrowingStream<Data, any Swift.Error>.Continuation!
        self.messageStream = AsyncThrowingStream { continuation = $0 }
        self.messageContinuation = continuation
    }
    
    public func connect() async throws {
        guard !isConnected else { return }
        
        logger.info("Starting HTTP server on port \(port)")
        
        let parameters = NWParameters.tcp
        parameters.allowLocalEndpointReuse = true
        
        listener = try NWListener(using: parameters, on: NWEndpoint.Port(integerLiteral: UInt16(port)))
        
        listener?.stateUpdateHandler = { [weak self] state in
            Task { [weak self] in
                await self?.handleListenerState(state)
            }
        }
        
        listener?.newConnectionHandler = { [weak self] connection in
            Task { [weak self] in
                await self?.handleNewConnection(connection)
            }
        }
        
        listener?.start(queue: .main)
        isConnected = true
        
        logger.info("HTTP server started on port \(port)")
    }
    
    public func disconnect() async {
        guard isConnected else { return }
        
        logger.info("Stopping HTTP server")
        listener?.cancel()
        connections.forEach { $0.cancel() }
        connections.removeAll()
        messageContinuation.finish()
        isConnected = false
    }
    
    public func send(_ data: Data) async throws {
        guard isConnected else {
            throw MCPError.internalError("Transport not connected")
        }
        
        // 发送响应给当前连接
        if let connection = currentConnection {
            logger.debug("Sending response: \(String(data: data, encoding: .utf8)?.prefix(100) ?? "binary data")")
            sendSSEResponse(connection, data: data)
            currentConnection = nil
        } else {
            logger.warning("No current connection to send response to")
        }
    }
    
    public func receive() -> AsyncThrowingStream<Data, any Swift.Error> {
        return messageStream
    }
    
    // MARK: - Private Methods
    
    private func handleListenerState(_ state: NWListener.State) {
        switch state {
        case .ready:
            logger.info("HTTP listener ready")
        case .failed(let error):
            logger.error("HTTP listener failed: \(error)")
        case .cancelled:
            logger.info("HTTP listener cancelled")
        default:
            break
        }
    }
    
    private func handleNewConnection(_ connection: NWConnection) {
        connections.append(connection)
        
        connection.stateUpdateHandler = { [weak self] state in
            Task { [weak self] in
                await self?.handleConnectionState(connection, state: state)
            }
        }
        
        connection.start(queue: .main)
        receiveHTTPRequest(on: connection)
    }
    
    private func handleConnectionState(_ connection: NWConnection, state: NWConnection.State) {
        switch state {
        case .ready:
            logger.debug("Connection ready")
        case .failed(let error):
            logger.error("Connection failed: \(error)")
            removeConnection(connection)
        case .cancelled:
            logger.debug("Connection cancelled")
            removeConnection(connection)
        default:
            break
        }
    }
    
    private func removeConnection(_ connection: NWConnection) {
        connections.removeAll { $0 === connection }
    }
    
    private func receiveHTTPRequest(on connection: NWConnection) {
        var accumulatedData = Data()
        
        func receiveMore() {
            connection.receive(minimumIncompleteLength: 1, maximumLength: 65536) { [weak self] data, _, isComplete, error in
                Task { [weak self] in
                    guard let self = self else { return }
                    
                    if let error = error {
                        await self.logger.error("Receive error: \(error)")
                        connection.cancel()
                        return
                    }
                    
                    if let data = data, !data.isEmpty {
                        accumulatedData.append(data)
                        await self.logger.debug("Received \(data.count) bytes, total: \(accumulatedData.count)")
                    }
                    
                    // 检查是否收到完整的 HTTP 请求（包含 \r\n\r\n）
                    if let separatorData = "\r\n\r\n".data(using: .utf8),
                       let _ = accumulatedData.range(of: separatorData) {
                        await self.logger.debug("Found complete HTTP request")
                        // 已收到完整请求头，现在检查是否有 Content-Length
                        if let request = await self.parseHTTPRequest(accumulatedData) {
                            await self.handleHTTPRequest(connection, request: request)
                        } else {
                            await self.sendHTTPResponse(connection, status: "400 Bad Request", body: "Invalid request")
                        }
                    } else if !isComplete {
                        // 继续接收
                        receiveMore()
                    } else {
                        // 连接关闭但没有收到完整请求
                        connection.cancel()
                    }
                }
            }
        }
        
        receiveMore()
    }
    
    private func parseHTTPRequest(_ data: Data) -> HTTPRequest? {
        guard let requestString = String(data: data, encoding: .utf8) else {
            logger.error("Failed to decode request as UTF-8")
            return nil
        }
        
        logger.debug("Parsing request: \(requestString.prefix(200))...")
        
        let lines = requestString.components(separatedBy: "\r\n")
        guard let firstLine = lines.first else { 
            logger.error("No first line in request")
            return nil 
        }
        
        let parts = firstLine.components(separatedBy: " ")
        guard parts.count >= 2 else { 
            logger.error("Invalid first line: \(firstLine)")
            return nil 
        }
        
        let method = parts[0]
        let path = parts[1]
        
        // 查找请求体 - 使用原始 Data 而不是转换后的字符串
        var body: Data?
        if let separatorData = "\r\n\r\n".data(using: .utf8),
           let separatorRange = data.range(of: separatorData) {
            let bodyData = data.subdata(in: separatorRange.upperBound..<data.count)
            if !bodyData.isEmpty {
                body = bodyData
                logger.debug("Found body with \(bodyData.count) bytes")
            }
        }
        
        return HTTPRequest(method: method, path: path, body: body)
    }
    
    private func handleHTTPRequest(_ connection: NWConnection, request: HTTPRequest) {
        logger.info("Received \(request.method) \(request.path)")
        
        switch request.path {
        case "/health":
            sendHTTPResponse(connection, status: "200 OK", contentType: "application/json", body: "{\"status\":\"UP\"}")
            
        case "/mcp":
            if request.method == "POST" {
                if let body = request.body, !body.isEmpty {
                    // 调试：打印收到的数据
                    if let bodyString = String(data: body, encoding: .utf8) {
                        logger.debug("Received body: \(bodyString)")
                    }
                    
                    // 保存当前连接以便稍后发送响应
                    currentConnection = connection
                    
                    // 将请求传递给 MCP 服务器
                    messageContinuation.yield(body)
                } else {
                    // 空body请求 - 可能是SSE探测请求
                    logger.debug("Received empty body POST - sending SSE headers")
                    sendSSEHeaders(connection)
                }
            } else {
                sendHTTPResponse(connection, status: "405 Method Not Allowed", body: "Only POST is allowed")
            }
            
        default:
            sendHTTPResponse(connection, status: "404 Not Found", body: "Not Found")
        }
    }
    
    private func sendHTTPResponse(_ connection: NWConnection, status: String, contentType: String = "text/plain", body: String) {
        let bodyData = body.data(using: .utf8) ?? Data()
        let response = """
        HTTP/1.1 \(status)\r
        Content-Type: \(contentType)\r
        Content-Length: \(bodyData.count)\r
        Connection: close\r
        \r
        \(body)
        """
        
        guard let responseData = response.data(using: .utf8) else { return }
        
        connection.send(content: responseData, completion: .contentProcessed { error in
            if let error = error {
                print("Send error: \(error)")
            }
            connection.cancel()
        })
    }
    
    private func sendSSEHeaders(_ connection: NWConnection) {
        // 发送 SSE headers 和一个空事件，然后关闭
        let response = """
        HTTP/1.1 200 OK\r
        Content-Type: text/event-stream\r
        Cache-Control: no-cache\r
        Connection: close\r
        \r
        : SSE supported\n\n
        """
        
        guard let responseData = response.data(using: .utf8) else { return }
        
        connection.send(content: responseData, completion: .contentProcessed { _ in
            connection.cancel()
        })
    }
    
    private func sendSSEResponse(_ connection: NWConnection, data: Data) {
        // 发送 SSE 格式的响应
        let headers = """
        HTTP/1.1 200 OK\r
        Content-Type: text/event-stream\r
        Cache-Control: no-cache\r
        Connection: keep-alive\r
        \r
        
        """
        
        guard let headerData = headers.data(using: .utf8) else { return }
        
        connection.send(content: headerData, completion: .contentProcessed { _ in
            // 发送 SSE 数据
            if let jsonString = String(data: data, encoding: .utf8) {
                let sseData = "data: \(jsonString)\n\n"
                if let sseBytes = sseData.data(using: .utf8) {
                    connection.send(content: sseBytes, completion: .contentProcessed { _ in
                        // 发送完成后关闭连接
                        connection.cancel()
                    })
                }
            }
        })
    }
}

struct HTTPRequest {
    let method: String
    let path: String
    let body: Data?
}
