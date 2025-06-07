import Foundation
import HelloMCPCore

@main
struct HelloMCPClient {
    static func main() async {
        print("=== Hello MCP Swift Client ===")
        print("Testing periodic table functions...")
        
        // Test finding element by name
        print("\n--- Testing findElement(byName:) ---")
        if let hydrogen = findElement(byName: "氢") {
            print("Found: \(hydrogen.name) (\(hydrogen.englishName)) - Symbol: \(hydrogen.symbol)")
        }
        
        if let silicon = findElement(byName: "硅") {
            print("Found: \(silicon.name) (\(silicon.englishName)) - Symbol: \(silicon.symbol)")
        }
        
        // Test finding element by atomic number
        print("\n--- Testing findElement(byAtomicNumber:) ---")
        if let carbon = findElement(byAtomicNumber: 6) {
            print("Found: \(carbon.name) (\(carbon.englishName)) - Atomic Number: \(carbon.atomicNumber)")
        }
        
        if let gold = findElement(byAtomicNumber: 79) {
            print("Found: \(gold.name) (\(gold.englishName)) - Atomic Number: \(gold.atomicNumber)")
        }
        
        // Test non-existent elements
        print("\n--- Testing non-existent elements ---")
        if findElement(byName: "不存在") == nil {
            print("Correctly returned nil for non-existent element name")
        }
        
        if findElement(byAtomicNumber: 999) == nil {
            print("Correctly returned nil for non-existent atomic number")
        }
        
        print("\nAll tests completed!")
    }
}
