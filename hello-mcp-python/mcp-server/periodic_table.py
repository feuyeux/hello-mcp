from dataclasses import dataclass
from typing import List, Optional, Dict, Any

@dataclass
class Element:
    atomic_number: int
    symbol: str
    name: str
    pronunciation: str
    english_name: str
    atomic_weight: float
    period: int
    group: str

# 元素周期表数据
periodic_table: List[Element] = [
    # 周期1（2种）
    Element(1, "H", "氢", "qīng", "Hydrogen", 1.008, 1, "IA"),
    Element(2, "He", "氦", "hài", "Helium", 4.0026, 1, "0族"),

    # 周期2（8种）
    Element(3, "Li", "锂", "lǐ", "Lithium", 6.941, 2, "IA"),
    Element(4, "Be", "铍", "pí", "Beryllium", 9.0122, 2, "IIA"),
    Element(5, "B", "硼", "péng", "Boron", 10.811, 2, "IIIA"),
    Element(6, "C", "碳", "tàn", "Carbon", 12.011, 2, "IVA"),
    Element(7, "N", "氮", "dàn", "Nitrogen", 14.007, 2, "VA"),
    Element(8, "O", "氧", "yǎng", "Oxygen", 15.999, 2, "VIA"),
    Element(9, "F", "氟", "fú", "Fluorine", 18.998, 2, "VIIA"),
    Element(10, "Ne", "氖", "nǎi", "Neon", 20.180, 2, "0族"),

    # 周期3（8种）
    Element(11, "Na", "钠", "nà", "Sodium", 22.990, 3, "IA"),
    Element(12, "Mg", "镁", "měi", "Magnesium", 24.305, 3, "IIA"),
    Element(13, "Al", "铝", "lǚ", "Aluminum", 26.982, 3, "IIIA"),
    Element(14, "Si", "硅", "guī", "Silicon", 28.085, 3, "IVA"),
    Element(15, "P", "磷", "lín", "Phosphorus", 30.974, 3, "VA"),
    Element(16, "S", "硫", "liú", "Sulfur", 32.06, 3, "VIA"),
    Element(17, "Cl", "氯", "lǜ", "Chlorine", 35.45, 3, "VIIA"),
    Element(18, "Ar", "氩", "yà", "Argon", 39.948, 3, "0族"),

    # 周期4（18种）
    Element(19, "K", "钾", "jiǎ", "Potassium", 39.098, 4, "IA"),
    Element(20, "Ca", "钙", "gài", "Calcium", 40.078, 4, "IIA"),
    Element(21, "Sc", "钪", "kàng", "Scandium", 44.956, 4, "IIIB"),
    Element(22, "Ti", "钛", "tài", "Titanium", 47.867, 4, "IVB"),
    Element(23, "V", "钒", "fán", "Vanadium", 50.942, 4, "VB"),
    Element(24, "Cr", "铬", "gè", "Chromium", 51.996, 4, "VIB"),
    Element(25, "Mn", "锰", "měng", "Manganese", 54.938, 4, "VIIB"),
    Element(26, "Fe", "铁", "tiě", "Iron", 55.845, 4, "VIIIB"),
    Element(27, "Co", "钴", "gǔ", "Cobalt", 58.933, 4, "VIIIB"),
    Element(28, "Ni", "镍", "niè", "Nickel", 58.693, 4, "VIIIB"),
    Element(29, "Cu", "铜", "tóng", "Copper", 63.546, 4, "IB"),
    Element(30, "Zn", "锌", "xīn", "Zinc", 65.38, 4, "IIB"),
    Element(31, "Ga", "镓", "jiā", "Gallium", 69.723, 4, "IIIA"),
    Element(32, "Ge", "锗", "zhě", "Germanium", 72.63, 4, "IVA"),
    Element(33, "As", "砷", "shēn", "Arsenic", 74.922, 4, "VA"),
    Element(34, "Se", "硒", "xī", "Selenium", 78.971, 4, "VIA"),
    Element(35, "Br", "溴", "xiù", "Bromine", 79.904, 4, "VIIA"),
    Element(36, "Kr", "氪", "kè", "Krypton", 83.798, 4, "0族"),

    # 周期5（18种）
    Element(37, "Rb", "铷", "rú", "Rubidium", 85.468, 5, "IA"),
    Element(38, "Sr", "锶", "sī", "Strontium", 87.62, 5, "IIA"),
    Element(39, "Y", "钇", "yǐ", "Yttrium", 88.906, 5, "IIIB"),
    Element(40, "Zr", "锆", "gào", "Zirconium", 91.224, 5, "IVB"),
    Element(41, "Nb", "铌", "ní", "Niobium", 92.906, 5, "VB"),
    Element(42, "Mo", "钼", "mù", "Molybdenum", 95.95, 5, "VIB"),
    Element(43, "Tc", "锝", "dé", "Technetium", 98.0, 5, "VIIB"),
    Element(44, "Ru", "钌", "liǎo", "Ruthenium", 101.07, 5, "VIIIB"),
    Element(45, "Rh", "铑", "láo", "Rhodium", 102.91, 5, "VIIIB"),
    Element(46, "Pd", "钯", "bǎ", "Palladium", 106.42, 5, "VIIIB"),
    Element(47, "Ag", "银", "yín", "Silver", 107.87, 5, "IB"),
    Element(48, "Cd", "镉", "gé", "Cadmium", 112.41, 5, "IIB"),
    Element(49, "In", "铟", "yīn", "Indium", 114.82, 5, "IIIA"),
    Element(50, "Sn", "锡", "xī", "Tin", 118.71, 5, "IVA"),
    Element(51, "Sb", "锑", "tī", "Antimony", 121.76, 5, "VA"),
    Element(52, "Te", "碲", "dì", "Tellurium", 127.6, 5, "VIA"),
    Element(53, "I", "碘", "diǎn", "Iodine", 126.9, 5, "VIIA"),
    Element(54, "Xe", "氙", "xiān", "Xenon", 131.29, 5, "0族"),
    
     # 周期6（32种）
    Element(55, "Cs", "铯", "sè", "Cesium", 132.91, 6, "IA"),
    Element(56, "Ba", "钡", "bèi", "Barium", 137.33, 6, "IIA"),
    Element(57, "La", "镧", "lán", "Lanthanum", 138.91, 6, "IIIB"),
    Element(58, "Ce", "铈", "shì", "Cerium", 140.12, 6, "镧系"),
    Element(59, "Pr", "镨", "pǔ", "Praseodymium", 140.91, 6, "镧系"),
    Element(60, "Nd", "钕", "nǚ", "Neodymium", 144.24, 6, "镧系"),
    Element(61, "Pm", "钷", "pǒ", "Promethium", 145.0, 6, "镧系"),
    Element(62, "Sm", "钐", "shān", "Samarium", 150.36, 6, "镧系"),
    Element(63, "Eu", "铕", "yǒu", "Europium", 151.96, 6, "镧系"),
    Element(64, "Gd", "钆", "gá", "Gadolinium", 157.25, 6, "镧系"),
    Element(65, "Tb", "铽", "tè", "Terbium", 158.93, 6, "镧系"),
    Element(66, "Dy", "镝", "dī", "Dysprosium", 162.50, 6, "镧系"),
    Element(67, "Ho", "钬", "huǒ", "Holmium", 164.93, 6, "镧系"),
    Element(68, "Er", "铒", "ěr", "Erbium", 167.26, 6, "镧系"),
    Element(69, "Tm", "铥", "diū", "Thulium", 168.93, 6, "镧系"),
    Element(70, "Yb", "镱", "yì", "Ytterbium", 173.05, 6, "镧系"),
    Element(71, "Lu", "镥", "lǔ", "Lutetium", 174.97, 6, "镧系"),
    Element(72, "Hf", "铪", "hā", "Hafnium", 178.49, 6, "IVB"),
    Element(73, "Ta", "钽", "tǎn", "Tantalum", 180.95, 6, "VB"),
    Element(74, "W", "钨", "wū", "Tungsten", 183.84, 6, "VIB"),
    Element(75, "Re", "铼", "lái", "Rhenium", 186.21, 6, "VIIB"),
    Element(76, "Os", "锇", "é", "Osmium", 190.23, 6, "VIIIB"),
    Element(77, "Ir", "铱", "yī", "Iridium", 192.22, 6, "VIIIB"),
    Element(78, "Pt", "铂", "bó", "Platinum", 195.08, 6, "VIIIB"),
    Element(79, "Au", "金", "jīn", "Gold", 196.97, 6, "IB"),
    Element(80, "Hg", "汞", "gǒng", "Mercury", 200.59, 6, "IIB"),
    Element(81, "Tl", "铊", "tā", "Thallium", 204.38, 6, "IIIA"),
    Element(82, "Pb", "铅", "qiān", "Lead", 207.2, 6, "IVA"),
    Element(83, "Bi", "铋", "bì", "Bismuth", 208.98, 6, "VA"),
    Element(84, "Po", "钋", "pō", "Polonium", 209.0, 6, "VIA"),
    Element(85, "At", "砹", "ài", "Astatine", 210.0, 6, "VIIA"),
    Element(86, "Rn", "氡", "dōng", "Radon", 222.0, 6, "0族"),

    # 周期7（32种）
    Element(87, "Fr", "钫", "fāng", "Francium", 223.0, 7, "IA"),
    Element(88, "Ra", "镭", "léi", "Radium", 226.0, 7, "IIA"),
    Element(89, "Ac", "锕", "ā", "Actinium", 227.0, 7, "IIIB"),
    Element(90, "Th", "钍", "tǔ", "Thorium", 232.04, 7, "锕系"),
    Element(91, "Pa", "镤", "pú", "Protactinium", 231.04, 7, "锕系"),
    Element(92, "U", "铀", "yóu", "Uranium", 238.03, 7, "锕系"),
    Element(93, "Np", "镎", "ná", "Neptunium", 237.0, 7, "锕系"),
    Element(94, "Pu", "钚", "bù", "Plutonium", 244.0, 7, "锕系"),
    Element(95, "Am", "镅", "méi", "Americium", 243.0, 7, "锕系"),
    Element(96, "Cm", "锔", "jú", "Curium", 247.0, 7, "锕系"),
    Element(97, "Bk", "锫", "péi", "Berkelium", 247.0, 7, "锕系"),
    Element(98, "Cf", "锎", "kāi", "Californium", 251.0, 7, "锕系"),
    Element(99, "Es", "锿", "āi", "Einsteinium", 252.0, 7, "锕系"),
    Element(100, "Fm", "镄", "fèi", "Fermium", 257.0, 7, "锕系"),
    Element(101, "Md", "钔", "mén", "Mendelevium", 258.0, 7, "锕系"),
    Element(102, "No", "锘", "nuò", "Nobelium", 259.0, 7, "锕系"),
    Element(103, "Lr", "铹", "láo", "Lawrencium", 262.0, 7, "锕系"),
    Element(104, "Rf", "𬬻", "lú", "Rutherfordium", 267.0, 7, "IVB"),
    Element(105, "Db", "𬭊", "dù", "Dubnium", 268.0, 7, "VB"),
    Element(106, "Sg", "𬭳", "xǐ", "Seaborgium", 271.0, 7, "VIB"),
    Element(107, "Bh", "𬭛", "bō", "Bohrium", 272.0, 7, "VIIB"),
    Element(108, "Hs", "𬭶", "hēi", "Hassium", 270.0, 7, "VIIIB"),
    Element(109, "Mt", "鿏", "mài", "Meitnerium", 276.0, 7, "VIIIB"),
    Element(110, "Ds", "𫟼", "dá", "Darmstadtium", 281.0, 7, "VIIIB"),
    Element(111, "Rg", "𬬭", "lún", "Roentgenium", 280.0, 7, "IB"),
    Element(112, "Cn", "鿔", "gē", "Copernicium", 285.0, 7, "IIB"),
    Element(113, "Nh", "鿭", "nǐ", "Nihonium", 284.0, 7, "IIIA"),
    Element(114, "Fl", "𫓧", "fū", "Flerovium", 289.0, 7, "IVA"),
    Element(115, "Mc", "镆", "mò", "Moscovium", 288.0, 7, "VA"),
    Element(116, "Lv", "𫟷", "lì", "Livermorium", 293.0, 7, "VIA"),
    Element(117, "Ts", "鿬", "tián", "Tennessine", 294.0, 7, "VIIA"),
    Element(118, "Og", "鿫", "ào", "Oganesson", 294.0, 7, "0族"),
]

def _element_to_dict(element: Element) -> Dict[str, Any]:
    """将 Element 对象转换为字典"""
    return {
        'atomic_number': element.atomic_number,
        'symbol': element.symbol,
        'name': element.name,
        'pronunciation': element.pronunciation,
        'english_name': element.english_name,
        'atomic_weight': element.atomic_weight,
        'period': element.period,
        'group': element.group
    }

def get_element_by_name(name: str) -> Optional[Dict[str, Any]]:
    """根据中文名称查找元素"""
    for element in periodic_table:
        if element.name == name:
            return _element_to_dict(element)
    return None

def get_element_by_english_name(english_name: str) -> Optional[Dict[str, Any]]:
    """根据英文名称查找元素（不区分大小写）"""
    english_name_lower = english_name.lower()
    for element in periodic_table:
        if element.english_name.lower() == english_name_lower:
            return _element_to_dict(element)
    return None

def get_element_by_symbol(symbol: str) -> Optional[Dict[str, Any]]:
    """根据元素符号查找元素（不区分大小写）"""
    symbol_upper = symbol.upper()
    for element in periodic_table:
        if element.symbol.upper() == symbol_upper:
            return _element_to_dict(element)
    return None

def get_element_by_position(atomic_number: int) -> Optional[Dict[str, Any]]:
    """根据原子序数查找元素"""
    for element in periodic_table:
        if element.atomic_number == atomic_number:
            return _element_to_dict(element)
    return None

def find_element(query: str) -> Optional[Dict[str, Any]]:
    """
    统一的查询接口，支持中文名、英文名和符号查询
    
    Args:
        query: 查询字符串，可以是中文名称、英文名称或元素符号
        
    Returns:
        元素信息字典，如果未找到则返回 None
    """
    # 首先尝试按中文名称查找
    result = get_element_by_name(query)
    if result:
        return result
    
    # 尝试按英文名称查找
    result = get_element_by_english_name(query)
    if result:
        return result
    
    # 尝试按元素符号查找
    result = get_element_by_symbol(query)
    if result:
        return result
    
    return None
