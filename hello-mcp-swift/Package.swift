// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "HelloMCPSwift",
    platforms: [
        .macOS(.v13),
        .iOS(.v16)
    ],
    products: [
        .executable(name: "HelloMCPServer", targets: ["HelloMCPServer"]),
        .executable(name: "HelloMCPClient", targets: ["HelloMCPClient"]),
        .library(name: "HelloMCPCore", targets: ["HelloMCPCore"])
    ],
    dependencies: [
        .package(url: "https://github.com/modelcontextprotocol/swift-sdk.git", from: "1.0.0")
    ],
    targets: [
        .target(
            name: "HelloMCPCore",
            dependencies: [
                .product(name: "ModelContextProtocol", package: "swift-sdk")
            ]
        ),
        .executableTarget(
            name: "HelloMCPServer",
            dependencies: [
                "HelloMCPCore",
                .product(name: "ModelContextProtocol", package: "swift-sdk")
            ]
        ),
        .executableTarget(
            name: "HelloMCPClient",
            dependencies: [
                "HelloMCPCore",
                .product(name: "ModelContextProtocol", package: "swift-sdk")
            ]
        ),
        .testTarget(
            name: "HelloMCPTests",
            dependencies: ["HelloMCPCore"]
        )
    ]
)
