import Foundation

public struct Element: Codable, Equatable {
    public let atomicNumber: Int
    public let symbol: String
    public let name: String
    public let pronunciation: String
    public let englishName: String
    public let atomicWeight: Double
    public let period: Int
    public let group: String
    
    public init(
        atomicNumber: Int,
        symbol: String,
        name: String,
        pronunciation: String,
        englishName: String,
        atomicWeight: Double,
        period: Int,
        group: String
    ) {
        self.atomicNumber = atomicNumber
        self.symbol = symbol
        self.name = name
        self.pronunciation = pronunciation
        self.englishName = englishName
        self.atomicWeight = atomicWeight
        self.period = period
        self.group = group
    }
}

public let periodicTable: [Element] = [
    // 周期1（2种）
    Element(atomicNumber: 1, symbol: "H", name: "氢", pronunciation: "qīng", englishName: "Hydrogen", atomicWeight: 1.008, period: 1, group: "IA"),
    Element(atomicNumber: 2, symbol: "He", name: "氦", pronunciation: "hài", englishName: "Helium", atomicWeight: 4.0026, period: 1, group: "0族"),
    
    // 周期2（8种）
    Element(atomicNumber: 3, symbol: "Li", name: "锂", pronunciation: "lǐ", englishName: "Lithium", atomicWeight: 6.941, period: 2, group: "IA"),
    Element(atomicNumber: 4, symbol: "Be", name: "铍", pronunciation: "pí", englishName: "Beryllium", atomicWeight: 9.0122, period: 2, group: "IIA"),
    Element(atomicNumber: 5, symbol: "B", name: "硼", pronunciation: "péng", englishName: "Boron", atomicWeight: 10.811, period: 2, group: "IIIA"),
    Element(atomicNumber: 6, symbol: "C", name: "碳", pronunciation: "tàn", englishName: "Carbon", atomicWeight: 12.011, period: 2, group: "IVA"),
    Element(atomicNumber: 7, symbol: "N", name: "氮", pronunciation: "dàn", englishName: "Nitrogen", atomicWeight: 14.007, period: 2, group: "VA"),
    Element(atomicNumber: 8, symbol: "O", name: "氧", pronunciation: "yǎng", englishName: "Oxygen", atomicWeight: 15.999, period: 2, group: "VIA"),
    Element(atomicNumber: 9, symbol: "F", name: "氟", pronunciation: "fú", englishName: "Fluorine", atomicWeight: 18.998, period: 2, group: "VIIA"),
    Element(atomicNumber: 10, symbol: "Ne", name: "氖", pronunciation: "nǎi", englishName: "Neon", atomicWeight: 20.180, period: 2, group: "0族"),
    
    // 周期3（8种）
    Element(atomicNumber: 11, symbol: "Na", name: "钠", pronunciation: "nà", englishName: "Sodium", atomicWeight: 22.990, period: 3, group: "IA"),
    Element(atomicNumber: 12, symbol: "Mg", name: "镁", pronunciation: "měi", englishName: "Magnesium", atomicWeight: 24.305, period: 3, group: "IIA"),
    Element(atomicNumber: 13, symbol: "Al", name: "铝", pronunciation: "lǚ", englishName: "Aluminum", atomicWeight: 26.982, period: 3, group: "IIIA"),
    Element(atomicNumber: 14, symbol: "Si", name: "硅", pronunciation: "guī", englishName: "Silicon", atomicWeight: 28.085, period: 3, group: "IVA"),
    Element(atomicNumber: 15, symbol: "P", name: "磷", pronunciation: "lín", englishName: "Phosphorus", atomicWeight: 30.974, period: 3, group: "VA"),
    Element(atomicNumber: 16, symbol: "S", name: "硫", pronunciation: "liú", englishName: "Sulfur", atomicWeight: 32.06, period: 3, group: "VIA"),
    Element(atomicNumber: 17, symbol: "Cl", name: "氯", pronunciation: "lǜ", englishName: "Chlorine", atomicWeight: 35.45, period: 3, group: "VIIA"),
    Element(atomicNumber: 18, symbol: "Ar", name: "氩", pronunciation: "yà", englishName: "Argon", atomicWeight: 39.948, period: 3, group: "0族"),
    
    // 周期4（部分元素）
    Element(atomicNumber: 19, symbol: "K", name: "钾", pronunciation: "jiǎ", englishName: "Potassium", atomicWeight: 39.098, period: 4, group: "IA"),
    Element(atomicNumber: 20, symbol: "Ca", name: "钙", pronunciation: "gài", englishName: "Calcium", atomicWeight: 40.078, period: 4, group: "IIA"),
    Element(atomicNumber: 26, symbol: "Fe", name: "铁", pronunciation: "tiě", englishName: "Iron", atomicWeight: 55.845, period: 4, group: "VIIIB"),
    Element(atomicNumber: 29, symbol: "Cu", name: "铜", pronunciation: "tóng", englishName: "Copper", atomicWeight: 63.546, period: 4, group: "IB"),
    Element(atomicNumber: 30, symbol: "Zn", name: "锌", pronunciation: "xīn", englishName: "Zinc", atomicWeight: 65.38, period: 4, group: "IIB"),
    
    // 周期5（部分元素）
    Element(atomicNumber: 47, symbol: "Ag", name: "银", pronunciation: "yín", englishName: "Silver", atomicWeight: 107.87, period: 5, group: "IB"),
    Element(atomicNumber: 50, symbol: "Sn", name: "锡", pronunciation: "xī", englishName: "Tin", atomicWeight: 118.71, period: 5, group: "IVA"),
    
    // 周期6（部分元素）
    Element(atomicNumber: 79, symbol: "Au", name: "金", pronunciation: "jīn", englishName: "Gold", atomicWeight: 196.97, period: 6, group: "IB"),
    Element(atomicNumber: 80, symbol: "Hg", name: "汞", pronunciation: "gǒng", englishName: "Mercury", atomicWeight: 200.59, period: 6, group: "IIB"),
    Element(atomicNumber: 82, symbol: "Pb", name: "铅", pronunciation: "qiān", englishName: "Lead", atomicWeight: 207.2, period: 6, group: "IVA"),
]

public func findElement(byName name: String) -> Element? {
    return periodicTable.first { $0.name == name }
}

public func findElement(byAtomicNumber atomicNumber: Int) -> Element? {
    return periodicTable.first { $0.atomicNumber == atomicNumber }
}
