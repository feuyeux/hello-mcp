package org.feuyeux.ai.hello.protocol

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * MCP JSON-RPC 2.0 协议定义
 */

@Serializable
data class JsonRpcRequest(
    val jsonrpc: String = "2.0",
    val id: JsonElement? = null,
    val method: String,
    val params: JsonObject? = null
)

@Serializable
data class JsonRpcResponse(
    val jsonrpc: String = "2.0",
    val id: JsonElement? = null,
    val result: JsonElement? = null,
    val error: JsonRpcError? = null
)

@Serializable
data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: JsonElement? = null
)

@Serializable
data class InitializeRequest(
    val protocolVersion: String,
    val capabilities: ClientCapabilities,
    val clientInfo: Implementation
)

@Serializable
data class ClientCapabilities(
    val roots: RootsCapability? = null,
    val sampling: JsonObject? = null
)

@Serializable
data class RootsCapability(
    val listChanged: Boolean? = null
)

@Serializable
data class Implementation(
    val name: String,
    val version: String
)

@Serializable
data class InitializeResult(
    val protocolVersion: String,
    val capabilities: ServerCapabilities,
    val serverInfo: Implementation
)

@Serializable
data class ServerCapabilities(
    val tools: ToolsCapability? = null,
    val logging: JsonObject? = null
)

@Serializable
data class ToolsCapability(
    val listChanged: Boolean? = null
)

@Serializable
data class Tool(
    val name: String,
    val description: String,
    val inputSchema: JsonObject
)

@Serializable
data class ListToolsResult(
    val tools: List<Tool>
)

@Serializable
data class CallToolRequest(
    val name: String,
    val arguments: JsonObject? = null
)

@Serializable
data class CallToolResult(
    val content: List<Content>
)

@Serializable
data class Content(
    val type: String,
    val text: String
)
