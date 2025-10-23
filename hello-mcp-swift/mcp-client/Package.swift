// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "MCPClient",
    platforms: [
        .macOS(.v13),
        .iOS(.v16)
    ],
    products: [
        .executable(name: "test-client", targets: ["TestClient"]),
        .executable(name: "test-ollama", targets: ["TestOllama"]),
        .library(name: "Client", targets: ["Client"])
    ],
    dependencies: [
        .package(url: "https://github.com/modelcontextprotocol/swift-sdk.git", branch: "main"),
        .package(url: "https://github.com/apple/swift-argument-parser.git", from: "1.2.0")
    ],
    targets: [
        .target(
            name: "Client",
            dependencies: [
                .product(name: "MCP", package: "swift-sdk")
            ],
            path: "Sources/Client"
        ),
        .executableTarget(
            name: "TestClient",
            dependencies: [
                "Client",
                .product(name: "ArgumentParser", package: "swift-argument-parser")
            ],
            path: "Sources/TestClient"
        ),
        .executableTarget(
            name: "TestOllama",
            dependencies: [
                "Client",
                .product(name: "ArgumentParser", package: "swift-argument-parser")
            ],
            path: "Sources/TestOllama"
        )
    ]
)
