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
        // Removing external dependency due to network issues
    ],
    targets: [
        .target(
            name: "HelloMCPCore",
            dependencies: []
        ),
        .executableTarget(
            name: "HelloMCPServer",
            dependencies: [
                "HelloMCPCore"
            ]
        ),
        .executableTarget(
            name: "HelloMCPClient",
            dependencies: [
                "HelloMCPCore"
            ]
        ),
        .testTarget(
            name: "HelloMCPTests",
            dependencies: ["HelloMCPCore"]
        )
    ]
)
