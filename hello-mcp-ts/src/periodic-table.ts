export interface Element {
  atomicNumber: number;
  symbol: string;
  name: string;
  pronunciation: string;
  englishName: string;
  atomicWeight: number;
  period: number;
  group: string;
}

export const periodicTable: Element[] = [
  // 周期1（2种）
  { atomicNumber: 1, symbol: "H", name: "氢", pronunciation: "qīng", englishName: "Hydrogen", atomicWeight: 1.008, period: 1, group: "IA" },
  { atomicNumber: 2, symbol: "He", name: "氦", pronunciation: "hài", englishName: "Helium", atomicWeight: 4.0026, period: 1, group: "0族" },

  // 周期2（8种）
  { atomicNumber: 3, symbol: "Li", name: "锂", pronunciation: "lǐ", englishName: "Lithium", atomicWeight: 6.941, period: 2, group: "IA" },
  { atomicNumber: 4, symbol: "Be", name: "铍", pronunciation: "pí", englishName: "Beryllium", atomicWeight: 9.0122, period: 2, group: "IIA" },
  { atomicNumber: 5, symbol: "B", name: "硼", pronunciation: "péng", englishName: "Boron", atomicWeight: 10.811, period: 2, group: "IIIA" },
  { atomicNumber: 6, symbol: "C", name: "碳", pronunciation: "tàn", englishName: "Carbon", atomicWeight: 12.011, period: 2, group: "IVA" },
  { atomicNumber: 7, symbol: "N", name: "氮", pronunciation: "dàn", englishName: "Nitrogen", atomicWeight: 14.007, period: 2, group: "VA" },
  { atomicNumber: 8, symbol: "O", name: "氧", pronunciation: "yǎng", englishName: "Oxygen", atomicWeight: 15.999, period: 2, group: "VIA" },
  { atomicNumber: 9, symbol: "F", name: "氟", pronunciation: "fú", englishName: "Fluorine", atomicWeight: 18.998, period: 2, group: "VIIA" },
  { atomicNumber: 10, symbol: "Ne", name: "氖", pronunciation: "nǎi", englishName: "Neon", atomicWeight: 20.180, period: 2, group: "0族" },

  // 周期3（8种）
  { atomicNumber: 11, symbol: "Na", name: "钠", pronunciation: "nà", englishName: "Sodium", atomicWeight: 22.990, period: 3, group: "IA" },
  { atomicNumber: 12, symbol: "Mg", name: "镁", pronunciation: "měi", englishName: "Magnesium", atomicWeight: 24.305, period: 3, group: "IIA" },
  { atomicNumber: 13, symbol: "Al", name: "铝", pronunciation: "lǚ", englishName: "Aluminum", atomicWeight: 26.982, period: 3, group: "IIIA" },
  { atomicNumber: 14, symbol: "Si", name: "硅", pronunciation: "guī", englishName: "Silicon", atomicWeight: 28.085, period: 3, group: "IVA" },
  { atomicNumber: 15, symbol: "P", name: "磷", pronunciation: "lín", englishName: "Phosphorus", atomicWeight: 30.974, period: 3, group: "VA" },
  { atomicNumber: 16, symbol: "S", name: "硫", pronunciation: "liú", englishName: "Sulfur", atomicWeight: 32.06, period: 3, group: "VIA" },
  { atomicNumber: 17, symbol: "Cl", name: "氯", pronunciation: "lǜ", englishName: "Chlorine", atomicWeight: 35.45, period: 3, group: "VIIA" },
  { atomicNumber: 18, symbol: "Ar", name: "氩", pronunciation: "yà", englishName: "Argon", atomicWeight: 39.948, period: 3, group: "0族" },

  // 周期4（18种）
  { atomicNumber: 19, symbol: "K", name: "钾", pronunciation: "jiǎ", englishName: "Potassium", atomicWeight: 39.098, period: 4, group: "IA" },
  { atomicNumber: 20, symbol: "Ca", name: "钙", pronunciation: "gài", englishName: "Calcium", atomicWeight: 40.078, period: 4, group: "IIA" },
  { atomicNumber: 21, symbol: "Sc", name: "钪", pronunciation: "kàng", englishName: "Scandium", atomicWeight: 44.956, period: 4, group: "IIIB" },
  { atomicNumber: 22, symbol: "Ti", name: "钛", pronunciation: "tài", englishName: "Titanium", atomicWeight: 47.867, period: 4, group: "IVB" },
  { atomicNumber: 23, symbol: "V", name: "钒", pronunciation: "fán", englishName: "Vanadium", atomicWeight: 50.942, period: 4, group: "VB" },
  { atomicNumber: 24, symbol: "Cr", name: "铬", pronunciation: "gè", englishName: "Chromium", atomicWeight: 51.996, period: 4, group: "VIB" },
  { atomicNumber: 25, symbol: "Mn", name: "锰", pronunciation: "měng", englishName: "Manganese", atomicWeight: 54.938, period: 4, group: "VIIB" },
  { atomicNumber: 26, symbol: "Fe", name: "铁", pronunciation: "tiě", englishName: "Iron", atomicWeight: 55.845, period: 4, group: "VIIIB" },
  { atomicNumber: 27, symbol: "Co", name: "钴", pronunciation: "gǔ", englishName: "Cobalt", atomicWeight: 58.933, period: 4, group: "VIIIB" },
  { atomicNumber: 28, symbol: "Ni", name: "镍", pronunciation: "niè", englishName: "Nickel", atomicWeight: 58.693, period: 4, group: "VIIIB" },
  { atomicNumber: 29, symbol: "Cu", name: "铜", pronunciation: "tóng", englishName: "Copper", atomicWeight: 63.546, period: 4, group: "IB" },
  { atomicNumber: 30, symbol: "Zn", name: "锌", pronunciation: "xīn", englishName: "Zinc", atomicWeight: 65.38, period: 4, group: "IIB" },
  { atomicNumber: 31, symbol: "Ga", name: "镓", pronunciation: "jiā", englishName: "Gallium", atomicWeight: 69.723, period: 4, group: "IIIA" },
  { atomicNumber: 32, symbol: "Ge", name: "锗", pronunciation: "zhě", englishName: "Germanium", atomicWeight: 72.63, period: 4, group: "IVA" },
  { atomicNumber: 33, symbol: "As", name: "砷", pronunciation: "shēn", englishName: "Arsenic", atomicWeight: 74.922, period: 4, group: "VA" },
  { atomicNumber: 34, symbol: "Se", name: "硒", pronunciation: "xī", englishName: "Selenium", atomicWeight: 78.971, period: 4, group: "VIA" },
  { atomicNumber: 35, symbol: "Br", name: "溴", pronunciation: "xiù", englishName: "Bromine", atomicWeight: 79.904, period: 4, group: "VIIA" },
  { atomicNumber: 36, symbol: "Kr", name: "氪", pronunciation: "kè", englishName: "Krypton", atomicWeight: 83.798, period: 4, group: "0族" }
  // 可以继续添加更多元素...
];

// 根据中文名称查找元素
export function getElementByName(name: string): Element | undefined {
  return periodicTable.find(element => element.name === name);
}

// 根据原子序数查找元素
export function getElementByPosition(atomicNumber: number): Element | undefined {
  return periodicTable.find(element => element.atomicNumber === atomicNumber);
}
