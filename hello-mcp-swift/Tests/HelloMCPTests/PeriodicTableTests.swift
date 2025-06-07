import XCTest
@testable import HelloMCPCore

final class PeriodicTableTests: XCTestCase {
    
    func testFindElementByName_Hydrogen() {
        // Act
        let element = findElement(byName: "氢")
        
        // Assert
        XCTAssertNotNil(element)
        XCTAssertEqual(element?.symbol, "H")
        XCTAssertEqual(element?.atomicNumber, 1)
        XCTAssertEqual(element?.englishName, "Hydrogen")
    }
    
    func testFindElementByName_Silicon() {
        // Act
        let element = findElement(byName: "硅")
        
        // Assert
        XCTAssertNotNil(element)
        XCTAssertEqual(element?.symbol, "Si")
        XCTAssertEqual(element?.atomicNumber, 14)
        XCTAssertEqual(element?.englishName, "Silicon")
    }
    
    func testFindElementByName_Carbon() {
        // Act
        let element = findElement(byName: "碳")
        
        // Assert
        XCTAssertNotNil(element)
        XCTAssertEqual(element?.symbol, "C")
        XCTAssertEqual(element?.atomicNumber, 6)
        XCTAssertEqual(element?.englishName, "Carbon")
    }
    
    func testFindElementByAtomicNumber_Hydrogen() {
        // Act
        let element = findElement(byAtomicNumber: 1)
        
        // Assert
        XCTAssertNotNil(element)
        XCTAssertEqual(element?.symbol, "H")
        XCTAssertEqual(element?.name, "氢")
        XCTAssertEqual(element?.englishName, "Hydrogen")
    }
    
    func testFindElementByAtomicNumber_Silicon() {
        // Act
        let element = findElement(byAtomicNumber: 14)
        
        // Assert
        XCTAssertNotNil(element)
        XCTAssertEqual(element?.symbol, "Si")
        XCTAssertEqual(element?.name, "硅")
        XCTAssertEqual(element?.englishName, "Silicon")
    }
    
    func testFindElementByAtomicNumber_Carbon() {
        // Act
        let element = findElement(byAtomicNumber: 6)
        
        // Assert
        XCTAssertNotNil(element)
        XCTAssertEqual(element?.symbol, "C")
        XCTAssertEqual(element?.name, "碳")
        XCTAssertEqual(element?.englishName, "Carbon")
    }
    
    func testFindElementByName_NonExistent() {
        // Act
        let element = findElement(byName: "NonExistent")
        
        // Assert
        XCTAssertNil(element)
    }
    
    func testFindElementByAtomicNumber_NonExistent() {
        // Act
        let element = findElement(byAtomicNumber: 999)
        
        // Assert
        XCTAssertNil(element)
    }
    
    func testPeriodicTableSize() {
        // Assert
        XCTAssertTrue(periodicTable.count > 0)
        XCTAssertTrue(periodicTable.count >= 18) // At least 18 elements defined
    }
}
