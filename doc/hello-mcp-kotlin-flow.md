# Hello MCP Kotlin Server 详细流程图

## 完整的请求响应流程

```mermaid
sequenceDiagram
    autonumber
    participant Client as MCP客户端
    participant SSE as SSE端点<br/>/sse
    participant Transport as SseServerTransport
    participant Server as MCP Server
    participant Service as HelloMcpService
    participant Data as 元素周期表数据

    Note over Data: 静态初始化块<br/>加载118个元素数据

    rect rgb(240, 248, 255)
        Note over Client,Server: 阶段1: 服务器启动与连接建立
        Client->>SSE: GET /sse (建立SSE连接)
        activate SSE
        SSE->>Transport: 创建 SseServerTransport(sessionId)
        activate Transport
        SSE->>Server: 创建 Server 实例
        activate Server
        Note over Server: 配置服务器能力<br/>- tools: listChanged=true<br/>- logging: enabled
        Server->>Service: 创建 HelloMcpService
        activate Service
        
        Server->>Server: addTool("getElement")<br/>注册工具1
        Note over Server: 输入参数: name (string)<br/>描述: 根据元素名称获取信息
        
        Server->>Server: addTool("getElementByPosition")<br/>注册工具2
        Note over Server: 输入参数: position (integer)<br/>描述: 根据原子序数获取信息
        
        SSE->>Server: server.connect(transport)
        Server-->>Transport: 连接成功
        Transport-->>SSE: SSE流建立
        SSE-->>Client: 200 OK (SSE连接保持)
        deactivate SSE
    end

    rect rgb(255, 250, 240)
        Note over Client,Server: 阶段2: 初始化握手 (Initialize)
        Client->>SSE: POST /message?sessionId=xxx
        activate SSE
        Note over Client: JSON-RPC 2.0 请求<br/>method: "initialize"<br/>params: {<br/>  protocolVersion: "2024-11-05",<br/>  capabilities: {...},<br/>  clientInfo: {...}<br/>}
        
        SSE->>Transport: handlePostMessage(call)
        activate Transport
        Transport->>Server: 处理 initialize 请求
        activate Server
        
        Server->>Server: 验证协议版本
        Server->>Server: 协商能力
        
        Server-->>Transport: InitializeResult {<br/>  protocolVersion,<br/>  capabilities: {<br/>    tools: {listChanged: true},<br/>    logging: {...}<br/>  },<br/>  serverInfo: {<br/>    name: "mcp kotlin server",<br/>    version: "0.1.0"<br/>  }<br/>}
        deactivate Server
        Transport-->>SSE: JSON-RPC Response
        deactivate Transport
        SSE-->>Client: 200 OK (初始化结果)
        deactivate SSE
    end

    rect rgb(240, 255, 240)
        Note over Client,Server: 阶段3: 工具列表查询 (List Tools)
        Client->>SSE: POST /message?sessionId=xxx
        activate SSE
        Note over Client: JSON-RPC 2.0 请求<br/>method: "tools/list"
        
        SSE->>Transport: handlePostMessage(call)
        activate Transport
        Transport->>Server: 处理 tools/list 请求
        activate Server
        
        Server->>Server: 获取已注册工具列表
        
        Server-->>Transport: ListToolsResult {<br/>  tools: [<br/>    {<br/>      name: "getElement",<br/>      description: "根据元素名称获取...",<br/>      inputSchema: {<br/>        properties: {name: {...}},<br/>        required: ["name"]<br/>      }<br/>    },<br/>    {<br/>      name: "getElementByPosition",<br/>      description: "根据原子序数...",<br/>      inputSchema: {<br/>        properties: {position: {...}},<br/>        required: ["position"]<br/>      }<br/>    }<br/>  ]<br/>}
        deactivate Server
        Transport-->>SSE: JSON-RPC Response
        deactivate Transport
        SSE-->>Client: 200 OK (工具列表)
        deactivate SSE
    end

    rect rgb(255, 240, 245)
        Note over Client,Data: 阶段4: 工具调用 - getElement (按名称查询)
        Client->>SSE: POST /message?sessionId=xxx
        activate SSE
        Note over Client: JSON-RPC 2.0 请求<br/>method: "tools/call"<br/>params: {<br/>  name: "getElement",<br/>  arguments: {<br/>    name: "氢" 或 "H" 或 "Hydrogen"<br/>  }<br/>}
        
        SSE->>Transport: handlePostMessage(call)
        activate Transport
        Transport->>Server: 处理 tools/call 请求
        activate Server
        
        Server->>Server: 解析参数 name
        Note over Server: 从 arguments 中提取<br/>name = "氢"
        
        Server->>Service: getElement("氢")
        activate Service
        
        Service->>Service: 验证参数非空
        Service->>Data: 查询元素列表
        activate Data
        Note over Data: 遍历 elements 列表<br/>匹配条件:<br/>- element.name == "氢"<br/>- element.englishName == "Hydrogen"<br/>- element.symbol == "H"
        
        Data-->>Service: 找到匹配元素
        deactivate Data
        
        Service->>Service: 格式化返回结果
        Note over Service: 构建字符串:<br/>"元素名称: 氢 (qīng, Hydrogen),<br/>原子序数: 1, 符号: H,<br/>相对原子质量: 1.008,<br/>周期: 1, 族: IA"
        
        Service-->>Server: 返回格式化字符串
        deactivate Service
        
        Server->>Server: 构建 CallToolResult
        Note over Server: CallToolResult {<br/>  content: [<br/>    TextContent(text)<br/>  ],<br/>  structuredContent: {<br/>    content: text<br/>  }<br/>}
        
        Server-->>Transport: CallToolResult
        deactivate Server
        Transport-->>SSE: JSON-RPC Response
        deactivate Transport
        SSE-->>Client: 200 OK (元素信息)
        deactivate SSE
    end

    rect rgb(255, 255, 240)
        Note over Client,Data: 阶段5: 工具调用 - getElementByPosition (按序号查询)
        Client->>SSE: POST /message?sessionId=xxx
        activate SSE
        Note over Client: JSON-RPC 2.0 请求<br/>method: "tools/call"<br/>params: {<br/>  name: "getElementByPosition",<br/>  arguments: {<br/>    position: 6<br/>  }<br/>}
        
        SSE->>Transport: handlePostMessage(call)
        activate Transport
        Transport->>Server: 处理 tools/call 请求
        activate Server
        
        Server->>Server: 解析参数 position
        Note over Server: 从 arguments 中提取<br/>position = 6
        
        Server->>Service: getElementByPosition(6)
        activate Service
        
        Service->>Service: 验证参数范围 (1-118)
        Service->>Data: 查询元素列表
        activate Data
        Note over Data: 遍历 elements 列表<br/>匹配条件:<br/>element.atomicNumber == 6
        
        Data-->>Service: 找到元素 (碳)
        deactivate Data
        
        Service->>Service: 格式化返回结果
        Note over Service: 构建字符串:<br/>"元素名称: 碳 (tàn, Carbon),<br/>原子序数: 6, 符号: C,<br/>相对原子质量: 12.011,<br/>周期: 2, 族: IVA"
        
        Service-->>Server: 返回格式化字符串
        deactivate Service
        
        Server->>Server: 构建 CallToolResult
        Server-->>Transport: CallToolResult
        deactivate Server
        Transport-->>SSE: JSON-RPC Response
        deactivate Transport
        SSE-->>Client: 200 OK (元素信息)
        deactivate SSE
    end

    rect rgb(255, 240, 240)
        Note over Client,Server: 阶段6: 连接关闭
        Client->>SSE: 关闭 SSE 连接
        activate SSE
        SSE->>Server: server.onClose()
        activate Server
        Server->>Transport: 清理资源
        deactivate Transport
        Server-->>SSE: 关闭完成
        deactivate Server
        SSE->>SSE: servers.remove(sessionId)
        SSE-->>Client: 连接已关闭
        deactivate SSE
        deactivate Service
    end
```

## 架构组件说明

```mermaid
graph TB
    subgraph "服务器启动层"
        A[main函数] --> B[embeddedServer Netty]
        B --> C[监听 0.0.0.0:3001]
    end
    
    subgraph "路由层"
        C --> D[GET /sse<br/>SSE连接端点]
        C --> E[POST /message<br/>消息处理端点]
    end
    
    subgraph "传输层"
        D --> F[SseServerTransport]
        E --> F
        F --> G[sessionId管理]
        F --> H[handlePostMessage]
    end
    
    subgraph "MCP协议层"
        H --> I[Server实例]
        I --> J[JSON-RPC 2.0处理]
        J --> K{请求类型}
        K -->|initialize| L[初始化握手]
        K -->|tools/list| M[返回工具列表]
        K -->|tools/call| N[执行工具调用]
    end
    
    subgraph "业务逻辑层"
        N --> O[HelloMcpService]
        O --> P[getElement<br/>按名称查询]
        O --> Q[getElementByPosition<br/>按序号查询]
    end
    
    subgraph "数据层"
        P --> R[elements列表<br/>118个元素]
        Q --> R
        R --> S[Element数据类<br/>原子序数/符号/名称等]
    end
    
    style A fill:#e1f5ff
    style I fill:#fff4e1
    style O fill:#e8f5e9
    style R fill:#fce4ec
```

## 数据流转详解

```mermaid
flowchart TD
    Start([客户端发起请求]) --> A{连接类型}
    
    A -->|首次连接| B[GET /sse]
    B --> C[创建 SseServerTransport]
    C --> D[生成唯一 sessionId]
    D --> E[创建 Server 实例]
    E --> F[注册工具:<br/>getElement<br/>getElementByPosition]
    F --> G[server.connect transport]
    G --> H[建立 SSE 长连接]
    H --> I[返回 sessionId 给客户端]
    
    A -->|后续请求| J[POST /message?sessionId=xxx]
    J --> K[根据 sessionId 查找 Server]
    K --> L{Server 存在?}
    L -->|否| M[返回 404 Not Found]
    L -->|是| N[transport.handlePostMessage]
    
    N --> O{解析 JSON-RPC 请求}
    
    O -->|method: initialize| P[处理初始化]
    P --> P1[验证协议版本]
    P1 --> P2[返回服务器能力]
    P2 --> Return1[JSON-RPC Response]
    
    O -->|method: tools/list| Q[处理工具列表]
    Q --> Q1[返回已注册的工具]
    Q1 --> Q2[包含工具名称/描述/参数schema]
    Q2 --> Return2[JSON-RPC Response]
    
    O -->|method: tools/call| R[处理工具调用]
    R --> R1{工具名称}
    
    R1 -->|getElement| S1[提取参数: name]
    S1 --> S2[调用 service.getElement name]
    S2 --> S3[在元素列表中查找]
    S3 --> S4{匹配条件}
    S4 -->|中文名| S5[找到元素]
    S4 -->|英文名| S5
    S4 -->|符号| S5
    S4 -->|无匹配| S6[返回 元素不存在]
    S5 --> S7[格式化元素信息]
    S7 --> Return3[CallToolResult]
    S6 --> Return3
    
    R1 -->|getElementByPosition| T1[提取参数: position]
    T1 --> T2[验证范围 1-118]
    T2 --> T3{范围有效?}
    T3 -->|否| T4[返回 位置无效]
    T3 -->|是| T5[按 atomicNumber 查找]
    T5 --> T6[找到元素]
    T6 --> T7[格式化元素信息]
    T7 --> Return4[CallToolResult]
    T4 --> Return4
    
    Return1 --> End([返回响应给客户端])
    Return2 --> End
    Return3 --> End
    Return4 --> End
    M --> End
    
    style Start fill:#e3f2fd
    style End fill:#e3f2fd
    style E fill:#fff3e0
    style S3 fill:#e8f5e9
    style T5 fill:#e8f5e9
    style M fill:#ffebee
```

## 核心类关系图

```mermaid
classDiagram
    class HelloMcpServer {
        +main(args: Array~String~)
        +runSseMcpServerWithPlainConfiguration(port: Int)
        +configureServer() Server
        -servers: ConcurrentMap~String, Server~
    }
    
    class Server {
        -implementation: Implementation
        -options: ServerOptions
        +addTool(name, description, inputSchema, handler)
        +connect(transport: SseServerTransport)
        +onClose(callback)
    }
    
    class SseServerTransport {
        +sessionId: String
        -endpoint: String
        -sseSession: ServerSSESession
        +handlePostMessage(call: ApplicationCall)
    }
    
    class HelloMcpService {
        -elements: List~Element~
        +getElement(name: String) String
        +getElementByPosition(position: Int) String
        -initializePeriodicTable()
    }
    
    class Element {
        +atomicNumber: Int
        +symbol: String
        +name: String
        +pronunciation: String
        +englishName: String
        +atomicWeight: Double
        +period: Int
        +group: String
    }
    
    class Implementation {
        +name: String
        +version: String
    }
    
    class ServerCapabilities {
        +tools: ToolsCapability
        +logging: JsonObject
    }
    
    class Tool {
        +name: String
        +description: String
        +inputSchema: JsonObject
    }
    
    class CallToolResult {
        +content: List~Content~
        +structuredContent: JsonObject
    }
    
    HelloMcpServer --> Server : creates
    HelloMcpServer --> SseServerTransport : creates
    HelloMcpServer --> HelloMcpService : uses
    Server --> Implementation : has
    Server --> ServerCapabilities : has
    Server --> Tool : registers
    Server --> SseServerTransport : connects
    HelloMcpService --> Element : manages
    Server --> CallToolResult : returns
```

## 关键技术点

### 1. SSE (Server-Sent Events) 通信机制
- 使用 Ktor SSE 插件建立长连接
- 客户端通过 GET /sse 建立连接
- 服务器通过 POST /message 接收消息
- 每个连接有唯一的 sessionId

### 2. JSON-RPC 2.0 协议
- 所有请求/响应遵循 JSON-RPC 2.0 规范
- 包含 jsonrpc、id、method、params 字段
- 支持错误处理机制

### 3. MCP 协议实现
- 实现了 initialize 握手
- 实现了 tools/list 工具发现
- 实现了 tools/call 工具执行

### 4. 工具注册机制
- 使用 server.addTool() 注册工具
- 定义工具名称、描述、输入 schema
- 提供 lambda 处理函数

### 5. 数据查询逻辑
- 静态初始化 118 个元素数据
- 支持多种查询方式（中文名/英文名/符号/序号）
- 使用 Kotlin 集合操作进行高效查询
