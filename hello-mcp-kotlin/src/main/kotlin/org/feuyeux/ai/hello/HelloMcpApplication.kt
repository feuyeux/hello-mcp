package org.feuyeux.ai.hello

import kotlinx.coroutines.runBlocking

/**
 * Main application entry point for Hello MCP Kotlin
 * Supports server, client, and test modes
 */
object HelloMcpApplication {
    @JvmStatic
    fun main(args: Array<String>) {
        when (args.getOrNull(0)) {
            "server" -> {
                println("Starting Periodic Table MCP Server...")
                runBlocking {
                    val server = PeriodicTableServer()
                    server.start()
                }
            }
            "client" -> {
                println("Starting Periodic Table MCP Client...")
                runBlocking {
                    val client = PeriodicTableClient()
                    client.testPeriodicTableOperations()
                }
            }
            "test" -> {
                println("Running periodic table tests...")
                runBlocking {
                    val client = PeriodicTableClient()
                    client.testPeriodicTableOperations()
                }
            }
            else -> {
                println("Hello MCP Kotlin - Periodic Table MCP Server/Client")
                println("Usage: gradle run --args=\"[server|client|test]\"")
                println("  server - Start the MCP server")
                println("  client - Run client operations")
                println("  test   - Run test operations")
                println("")
                println("Example:")
                println("  gradle run --args=\"server\"")
                println("  gradle run --args=\"client\"")
            }
        }
    }
}

/**
 * Alternative main function for compatibility
 */
fun main(args: Array<String>) {
    HelloMcpApplication.main(args)
}
