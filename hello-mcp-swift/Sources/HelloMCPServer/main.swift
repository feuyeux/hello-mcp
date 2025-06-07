import Foundation
import HelloMCPCore

@main
struct HelloMCPServer {
    static func main() async {
        print("Hello MCP Swift Server!")
        print("Periodic table has \(periodicTable.count) elements")
        
        // Test some elements
        if let hydrogen = findElement(byName: "氢") {
            print("Found hydrogen: \(hydrogen.englishName)")
        }
        
        if let carbon = findElement(byAtomicNumber: 6) {
            print("Found carbon: \(carbon.name)")
        }
    }
}
