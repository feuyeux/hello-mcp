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
