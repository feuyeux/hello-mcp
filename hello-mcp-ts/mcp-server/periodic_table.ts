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
  { atomicNumber: 36, symbol: "Kr", name: "氪", pronunciation: "kè", englishName: "Krypton", atomicWeight: 83.798, period: 4, group: "0族" },

  // 周期5（18种）
  { atomicNumber: 37, symbol: "Rb", name: "铷", pronunciation: "rú", englishName: "Rubidium", atomicWeight: 85.468, period: 5, group: "IA" },
  { atomicNumber: 38, symbol: "Sr", name: "锶", pronunciation: "sī", englishName: "Strontium", atomicWeight: 87.62, period: 5, group: "IIA" },
  { atomicNumber: 39, symbol: "Y", name: "钇", pronunciation: "yǐ", englishName: "Yttrium", atomicWeight: 88.906, period: 5, group: "IIIB" },
  { atomicNumber: 40, symbol: "Zr", name: "锆", pronunciation: "gào", englishName: "Zirconium", atomicWeight: 91.224, period: 5, group: "IVB" },
  { atomicNumber: 41, symbol: "Nb", name: "铌", pronunciation: "ní", englishName: "Niobium", atomicWeight: 92.906, period: 5, group: "VB" },
  { atomicNumber: 42, symbol: "Mo", name: "钼", pronunciation: "mù", englishName: "Molybdenum", atomicWeight: 95.95, period: 5, group: "VIB" },
  { atomicNumber: 43, symbol: "Tc", name: "锝", pronunciation: "dé", englishName: "Technetium", atomicWeight: 98.0, period: 5, group: "VIIB" },
  { atomicNumber: 44, symbol: "Ru", name: "钌", pronunciation: "liǎo", englishName: "Ruthenium", atomicWeight: 101.07, period: 5, group: "VIIIB" },
  { atomicNumber: 45, symbol: "Rh", name: "铑", pronunciation: "láo", englishName: "Rhodium", atomicWeight: 102.91, period: 5, group: "VIIIB" },
  { atomicNumber: 46, symbol: "Pd", name: "钯", pronunciation: "bǎ", englishName: "Palladium", atomicWeight: 106.42, period: 5, group: "VIIIB" },
  { atomicNumber: 47, symbol: "Ag", name: "银", pronunciation: "yín", englishName: "Silver", atomicWeight: 107.87, period: 5, group: "IB" },
  { atomicNumber: 48, symbol: "Cd", name: "镉", pronunciation: "gé", englishName: "Cadmium", atomicWeight: 112.41, period: 5, group: "IIB" },
  { atomicNumber: 49, symbol: "In", name: "铟", pronunciation: "yīn", englishName: "Indium", atomicWeight: 114.82, period: 5, group: "IIIA" },
  { atomicNumber: 50, symbol: "Sn", name: "锡", pronunciation: "xī", englishName: "Tin", atomicWeight: 118.71, period: 5, group: "IVA" },
  { atomicNumber: 51, symbol: "Sb", name: "锑", pronunciation: "tī", englishName: "Antimony", atomicWeight: 121.76, period: 5, group: "VA" },
  { atomicNumber: 52, symbol: "Te", name: "碲", pronunciation: "dì", englishName: "Tellurium", atomicWeight: 127.6, period: 5, group: "VIA" },
  { atomicNumber: 53, symbol: "I", name: "碘", pronunciation: "diǎn", englishName: "Iodine", atomicWeight: 126.9, period: 5, group: "VIIA" },
  { atomicNumber: 54, symbol: "Xe", name: "氙", pronunciation: "xiān", englishName: "Xenon", atomicWeight: 131.29, period: 5, group: "0族" },

  // 周期6（32种）
  { atomicNumber: 55, symbol: "Cs", name: "铯", pronunciation: "sè", englishName: "Cesium", atomicWeight: 132.91, period: 6, group: "IA" },
  { atomicNumber: 56, symbol: "Ba", name: "钡", pronunciation: "bèi", englishName: "Barium", atomicWeight: 137.33, period: 6, group: "IIA" },
  { atomicNumber: 57, symbol: "La", name: "镧", pronunciation: "lán", englishName: "Lanthanum", atomicWeight: 138.91, period: 6, group: "IIIB" },
  { atomicNumber: 58, symbol: "Ce", name: "铈", pronunciation: "shì", englishName: "Cerium", atomicWeight: 140.12, period: 6, group: "镧系" },
  { atomicNumber: 59, symbol: "Pr", name: "镨", pronunciation: "pǔ", englishName: "Praseodymium", atomicWeight: 140.91, period: 6, group: "镧系" },
  { atomicNumber: 60, symbol: "Nd", name: "钕", pronunciation: "nǚ", englishName: "Neodymium", atomicWeight: 144.24, period: 6, group: "镧系" },
  { atomicNumber: 61, symbol: "Pm", name: "钷", pronunciation: "pǒ", englishName: "Promethium", atomicWeight: 145.0, period: 6, group: "镧系" },
  { atomicNumber: 62, symbol: "Sm", name: "钐", pronunciation: "shān", englishName: "Samarium", atomicWeight: 150.36, period: 6, group: "镧系" },
  { atomicNumber: 63, symbol: "Eu", name: "铕", pronunciation: "yǒu", englishName: "Europium", atomicWeight: 151.96, period: 6, group: "镧系" },
  { atomicNumber: 64, symbol: "Gd", name: "钆", pronunciation: "gá", englishName: "Gadolinium", atomicWeight: 157.25, period: 6, group: "镧系" },
  { atomicNumber: 65, symbol: "Tb", name: "铽", pronunciation: "tè", englishName: "Terbium", atomicWeight: 158.93, period: 6, group: "镧系" },
  { atomicNumber: 66, symbol: "Dy", name: "镝", pronunciation: "dī", englishName: "Dysprosium", atomicWeight: 162.50, period: 6, group: "镧系" },
  { atomicNumber: 67, symbol: "Ho", name: "钬", pronunciation: "huǒ", englishName: "Holmium", atomicWeight: 164.93, period: 6, group: "镧系" },
  { atomicNumber: 68, symbol: "Er", name: "铒", pronunciation: "ěr", englishName: "Erbium", atomicWeight: 167.26, period: 6, group: "镧系" },
  { atomicNumber: 69, symbol: "Tm", name: "铥", pronunciation: "diū", englishName: "Thulium", atomicWeight: 168.93, period: 6, group: "镧系" },
  { atomicNumber: 70, symbol: "Yb", name: "镱", pronunciation: "yì", englishName: "Ytterbium", atomicWeight: 173.05, period: 6, group: "镧系" },
  { atomicNumber: 71, symbol: "Lu", name: "镥", pronunciation: "lǔ", englishName: "Lutetium", atomicWeight: 174.97, period: 6, group: "镧系" },
  { atomicNumber: 72, symbol: "Hf", name: "铪", pronunciation: "hā", englishName: "Hafnium", atomicWeight: 178.49, period: 6, group: "IVB" },
  { atomicNumber: 73, symbol: "Ta", name: "钽", pronunciation: "tǎn", englishName: "Tantalum", atomicWeight: 180.95, period: 6, group: "VB" },
  { atomicNumber: 74, symbol: "W", name: "钨", pronunciation: "wū", englishName: "Tungsten", atomicWeight: 183.84, period: 6, group: "VIB" },
  { atomicNumber: 75, symbol: "Re", name: "铼", pronunciation: "lái", englishName: "Rhenium", atomicWeight: 186.21, period: 6, group: "VIIB" },
  { atomicNumber: 76, symbol: "Os", name: "锇", pronunciation: "é", englishName: "Osmium", atomicWeight: 190.23, period: 6, group: "VIIIB" },
  { atomicNumber: 77, symbol: "Ir", name: "铱", pronunciation: "yī", englishName: "Iridium", atomicWeight: 192.22, period: 6, group: "VIIIB" },
  { atomicNumber: 78, symbol: "Pt", name: "铂", pronunciation: "bó", englishName: "Platinum", atomicWeight: 195.08, period: 6, group: "VIIIB" },
  { atomicNumber: 79, symbol: "Au", name: "金", pronunciation: "jīn", englishName: "Gold", atomicWeight: 196.97, period: 6, group: "IB" },
  { atomicNumber: 80, symbol: "Hg", name: "汞", pronunciation: "gǒng", englishName: "Mercury", atomicWeight: 200.59, period: 6, group: "IIB" },
  { atomicNumber: 81, symbol: "Tl", name: "铊", pronunciation: "tā", englishName: "Thallium", atomicWeight: 204.38, period: 6, group: "IIIA" },
  { atomicNumber: 82, symbol: "Pb", name: "铅", pronunciation: "qiān", englishName: "Lead", atomicWeight: 207.2, period: 6, group: "IVA" },
  { atomicNumber: 83, symbol: "Bi", name: "铋", pronunciation: "bì", englishName: "Bismuth", atomicWeight: 208.98, period: 6, group: "VA" },
  { atomicNumber: 84, symbol: "Po", name: "钋", pronunciation: "pō", englishName: "Polonium", atomicWeight: 209.0, period: 6, group: "VIA" },
  { atomicNumber: 85, symbol: "At", name: "砹", pronunciation: "ài", englishName: "Astatine", atomicWeight: 210.0, period: 6, group: "VIIA" },
  { atomicNumber: 86, symbol: "Rn", name: "氡", pronunciation: "dōng", englishName: "Radon", atomicWeight: 222.0, period: 6, group: "0族" },

  // 周期7（32种）
  { atomicNumber: 87, symbol: "Fr", name: "钫", pronunciation: "fāng", englishName: "Francium", atomicWeight: 223.0, period: 7, group: "IA" },
  { atomicNumber: 88, symbol: "Ra", name: "镭", pronunciation: "léi", englishName: "Radium", atomicWeight: 226.0, period: 7, group: "IIA" },
  { atomicNumber: 89, symbol: "Ac", name: "锕", pronunciation: "ā", englishName: "Actinium", atomicWeight: 227.0, period: 7, group: "IIIB" },
  { atomicNumber: 90, symbol: "Th", name: "钍", pronunciation: "tǔ", englishName: "Thorium", atomicWeight: 232.04, period: 7, group: "锕系" },
  { atomicNumber: 91, symbol: "Pa", name: "镤", pronunciation: "pú", englishName: "Protactinium", atomicWeight: 231.04, period: 7, group: "锕系" },
  { atomicNumber: 92, symbol: "U", name: "铀", pronunciation: "yóu", englishName: "Uranium", atomicWeight: 238.03, period: 7, group: "锕系" },
  { atomicNumber: 93, symbol: "Np", name: "镎", pronunciation: "ná", englishName: "Neptunium", atomicWeight: 237.0, period: 7, group: "锕系" },
  { atomicNumber: 94, symbol: "Pu", name: "钚", pronunciation: "bù", englishName: "Plutonium", atomicWeight: 244.0, period: 7, group: "锕系" },
  { atomicNumber: 95, symbol: "Am", name: "镅", pronunciation: "méi", englishName: "Americium", atomicWeight: 243.0, period: 7, group: "锕系" },
  { atomicNumber: 96, symbol: "Cm", name: "锔", pronunciation: "jú", englishName: "Curium", atomicWeight: 247.0, period: 7, group: "锕系" },
  { atomicNumber: 97, symbol: "Bk", name: "锫", pronunciation: "péi", englishName: "Berkelium", atomicWeight: 247.0, period: 7, group: "锕系" },
  { atomicNumber: 98, symbol: "Cf", name: "锎", pronunciation: "kāi", englishName: "Californium", atomicWeight: 251.0, period: 7, group: "锕系" },
  { atomicNumber: 99, symbol: "Es", name: "锿", pronunciation: "āi", englishName: "Einsteinium", atomicWeight: 252.0, period: 7, group: "锕系" },
  { atomicNumber: 100, symbol: "Fm", name: "镄", pronunciation: "fèi", englishName: "Fermium", atomicWeight: 257.0, period: 7, group: "锕系" },
  { atomicNumber: 101, symbol: "Md", name: "钔", pronunciation: "mén", englishName: "Mendelevium", atomicWeight: 258.0, period: 7, group: "锕系" },
  { atomicNumber: 102, symbol: "No", name: "锘", pronunciation: "nuò", englishName: "Nobelium", atomicWeight: 259.0, period: 7, group: "锕系" },
  { atomicNumber: 103, symbol: "Lr", name: "铹", pronunciation: "láo", englishName: "Lawrencium", atomicWeight: 262.0, period: 7, group: "锕系" },
  { atomicNumber: 104, symbol: "Rf", name: "𬬻", pronunciation: "lú", englishName: "Rutherfordium", atomicWeight: 267.0, period: 7, group: "IVB" },
  { atomicNumber: 105, symbol: "Db", name: "𬭊", pronunciation: "dù", englishName: "Dubnium", atomicWeight: 268.0, period: 7, group: "VB" },
  { atomicNumber: 106, symbol: "Sg", name: "𬭳", pronunciation: "xǐ", englishName: "Seaborgium", atomicWeight: 271.0, period: 7, group: "VIB" },
  { atomicNumber: 107, symbol: "Bh", name: "𬭛", pronunciation: "bō", englishName: "Bohrium", atomicWeight: 272.0, period: 7, group: "VIIB" },
  { atomicNumber: 108, symbol: "Hs", name: "𬭶", pronunciation: "hēi", englishName: "Hassium", atomicWeight: 270.0, period: 7, group: "VIIIB" },
  { atomicNumber: 109, symbol: "Mt", name: "鿏", pronunciation: "mài", englishName: "Meitnerium", atomicWeight: 276.0, period: 7, group: "VIIIB" },
  { atomicNumber: 110, symbol: "Ds", name: "𫟼", pronunciation: "dá", englishName: "Darmstadtium", atomicWeight: 281.0, period: 7, group: "VIIIB" },
  { atomicNumber: 111, symbol: "Rg", name: "𬬭", pronunciation: "lún", englishName: "Roentgenium", atomicWeight: 280.0, period: 7, group: "IB" },
  { atomicNumber: 112, symbol: "Cn", name: "鿔", pronunciation: "gē", englishName: "Copernicium", atomicWeight: 285.0, period: 7, group: "IIB" },
  { atomicNumber: 113, symbol: "Nh", name: "鿭", pronunciation: "nǐ", englishName: "Nihonium", atomicWeight: 284.0, period: 7, group: "IIIA" },
  { atomicNumber: 114, symbol: "Fl", name: "𫓧", pronunciation: "fū", englishName: "Flerovium", atomicWeight: 289.0, period: 7, group: "IVA" },
  { atomicNumber: 115, symbol: "Mc", name: "镆", pronunciation: "mò", englishName: "Moscovium", atomicWeight: 288.0, period: 7, group: "VA" },
  { atomicNumber: 116, symbol: "Lv", name: "𫟷", pronunciation: "lì", englishName: "Livermorium", atomicWeight: 293.0, period: 7, group: "VIA" },
  { atomicNumber: 117, symbol: "Ts", name: "鿬", pronunciation: "tián", englishName: "Tennessine", atomicWeight: 294.0, period: 7, group: "VIIA" },
  { atomicNumber: 118, symbol: "Og", name: "鿫", pronunciation: "ào", englishName: "Oganesson", atomicWeight: 294.0, period: 7, group: "0族" },
];

/**
 * 根据中文名称查找元素
 */
export function getElementByName(name: string): Element | undefined {
  return periodicTable.find(element => element.name === name);
}

/**
 * 根据英文名称查找元素（不区分大小写）
 */
export function getElementByEnglishName(englishName: string): Element | undefined {
  const lowerName = englishName.toLowerCase();
  return periodicTable.find(element => element.englishName.toLowerCase() === lowerName);
}

/**
 * 根据元素符号查找元素（不区分大小写）
 */
export function getElementBySymbol(symbol: string): Element | undefined {
  const upperSymbol = symbol.toUpperCase();
  return periodicTable.find(element => element.symbol.toUpperCase() === upperSymbol);
}

/**
 * 根据原子序数查找元素
 */
export function getElementByPosition(atomicNumber: number): Element | undefined {
  return periodicTable.find(element => element.atomicNumber === atomicNumber);
}

/**
 * 统一的查询接口，支持中文名、英文名和符号查询
 */
export function findElement(query: string): Element | undefined {
  // 首先尝试按中文名称查找
  let result = getElementByName(query);
  if (result) return result;

  // 尝试按英文名称查找
  result = getElementByEnglishName(query);
  if (result) return result;

  // 尝试按元素符号查找
  result = getElementBySymbol(query);
  if (result) return result;

  return undefined;
}
