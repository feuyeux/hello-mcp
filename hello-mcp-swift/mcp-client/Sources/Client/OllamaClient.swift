import Foundation

#if canImport(FoundationNetworking)
import FoundationNetworking
#endif

/// 消息类
public struct OllamaMessage: Codable {
    public let role: String
    public let content: String
    
    public init(role: String, content: String) {
        self.role = role
        self.content = content
    }
}

// 为了向后兼容，保留 Message 别名
public typealias Message = OllamaMessage

/// 工具调用类
public struct ToolCall: Codable {
    public let name: String
    public let arguments: [String: AnyCodable]
    
    public init(name: String, arguments: [String: AnyCodable]) {
        self.name = name
        self.arguments = arguments
    }
}

/// 聊天响应类
public struct ChatResponse {
    public let role: String
    public let content: String
    public let toolCalls: [ToolCall]
    
    public var hasToolCalls: Bool {
        return !toolCalls.isEmpty
    }
}

/// Ollama 客户端
/// 用于与 Ollama API 交互，支持工具调用
public class OllamaClient {
    private let baseURL: String
    private let model: String
    
    public init(baseURL: String = "http://localhost:11434", model: String = "qwen2.5:latest") {
        self.baseURL = baseURL
        self.model = model
    }
    
    /// 发送聊天请求
    public func chat(messages: [Message], tools: [[String: AnyCodable]]) async throws -> ChatResponse {
        print("发送聊天请求到 Ollama: model=\(model), messages=\(messages.count)")
        
        let url = URL(string: "\(baseURL)/api/chat")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.timeoutInterval = 300
        
        let requestBody: [String: Any] = [
            "model": model,
            "stream": false,
            "messages": messages.map { ["role": $0.role, "content": $0.content] },
            "tools": tools.map { tool in
                tool.mapValues { value -> Any in
                    switch value {
                    case .string(let s): return s
                    case .int(let i): return i
                    case .double(let d): return d
                    case .bool(let b): return b
                    case .dictionary(let dict): return dict
                    case .array(let arr): return arr
                    case .null: return NSNull()
                    }
                }
            }
        ]
        
        request.httpBody = try JSONSerialization.data(withJSONObject: requestBody)
        
        let (data, response) = try await URLSession.shared.data(for: request)
        
        guard let httpResponse = response as? HTTPURLResponse else {
            throw NSError(domain: "OllamaClient", code: -1, userInfo: [NSLocalizedDescriptionKey: "Invalid response"])
        }
        
        guard httpResponse.statusCode == 200 else {
            throw NSError(domain: "OllamaClient", code: httpResponse.statusCode, 
                         userInfo: [NSLocalizedDescriptionKey: "Ollama API 请求失败: \(httpResponse.statusCode)"])
        }
        
        let json = try JSONSerialization.jsonObject(with: data) as? [String: Any]
        let messageNode = json?["message"] as? [String: Any]
        
        let role = messageNode?["role"] as? String ?? "assistant"
        let content = messageNode?["content"] as? String ?? ""
        
        var toolCalls: [ToolCall] = []
        if let toolCallsArray = messageNode?["tool_calls"] as? [[String: Any]] {
            for toolCallNode in toolCallsArray {
                if let function = toolCallNode["function"] as? [String: Any],
                   let name = function["name"] as? String,
                   let arguments = function["arguments"] as? [String: Any] {
                    let args = arguments.mapValues { AnyCodable($0) }
                    toolCalls.append(ToolCall(name: name, arguments: args))
                }
            }
        }
        
        return ChatResponse(role: role, content: content, toolCalls: toolCalls)
    }
    
    /// 执行工具调用
    public func executeToolCall(toolCall: ToolCall, helloClient: HelloClient) async throws -> String {
        print("执行工具调用: \(toolCall.name), 参数: \(toolCall.arguments)")
        
        var result: String
        
        switch toolCall.name {
        case "get_element":
            if case .string(let name) = toolCall.arguments["name"] {
                result = try await helloClient.getElement(name: name)
            } else {
                result = "{\"error\": \"参数错误\"}"
            }
            
        case "get_element_by_position":
            if case .int(let position) = toolCall.arguments["position"] {
                result = try await helloClient.getElementByPosition(position: position)
            } else if case .double(let position) = toolCall.arguments["position"] {
                result = try await helloClient.getElementByPosition(position: Int(position))
            } else {
                result = "{\"error\": \"参数错误\"}"
            }
            
        default:
            result = "{\"error\": \"未知工具: \(toolCall.name)\"}"
        }
        
        print("工具调用结果: \(result)")
        return result
    }
}

/// AnyCodable 类型用于处理动态 JSON
public enum AnyCodable: Codable {
    case string(String)
    case int(Int)
    case double(Double)
    case bool(Bool)
    case dictionary([String: AnyCodable])
    case array([AnyCodable])
    case null
    
    public init(_ value: Any) {
        if let string = value as? String {
            self = .string(string)
        } else if let int = value as? Int {
            self = .int(int)
        } else if let double = value as? Double {
            self = .double(double)
        } else if let bool = value as? Bool {
            self = .bool(bool)
        } else if let dict = value as? [String: Any] {
            self = .dictionary(dict.mapValues { AnyCodable($0) })
        } else if let array = value as? [Any] {
            self = .array(array.map { AnyCodable($0) })
        } else {
            self = .null
        }
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.singleValueContainer()
        
        if let string = try? container.decode(String.self) {
            self = .string(string)
        } else if let int = try? container.decode(Int.self) {
            self = .int(int)
        } else if let double = try? container.decode(Double.self) {
            self = .double(double)
        } else if let bool = try? container.decode(Bool.self) {
            self = .bool(bool)
        } else if let dict = try? container.decode([String: AnyCodable].self) {
            self = .dictionary(dict)
        } else if let array = try? container.decode([AnyCodable].self) {
            self = .array(array)
        } else {
            self = .null
        }
    }
    
    public func encode(to encoder: Encoder) throws {
        var container = encoder.singleValueContainer()
        
        switch self {
        case .string(let value):
            try container.encode(value)
        case .int(let value):
            try container.encode(value)
        case .double(let value):
            try container.encode(value)
        case .bool(let value):
            try container.encode(value)
        case .dictionary(let value):
            try container.encode(value)
        case .array(let value):
            try container.encode(value)
        case .null:
            try container.encodeNil()
        }
    }
}
