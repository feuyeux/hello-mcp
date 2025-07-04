use serde::{Deserialize, Serialize};
use std::sync::LazyLock;

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Element {
    pub atomic_number: u8,
    pub symbol: String,
    pub name: String,
    pub pronunciation: String,
    pub english_name: String,
    pub atomic_weight: f64,
    pub period: u8,
    pub group: String,
}

pub static PERIODIC_TABLE: LazyLock<Vec<Element>> = LazyLock::new(|| vec![
    // 周期1（2种）
    Element {
        atomic_number: 1,
        symbol: "H".to_string(),
        name: "氢".to_string(),
        pronunciation: "qīng".to_string(),
        english_name: "Hydrogen".to_string(),
        atomic_weight: 1.008,
        period: 1,
        group: "IA".to_string(),
    },
    Element {
        atomic_number: 2,
        symbol: "He".to_string(),
        name: "氦".to_string(),
        pronunciation: "hài".to_string(),
        english_name: "Helium".to_string(),
        atomic_weight: 4.0026,
        period: 1,
        group: "0族".to_string(),
    },
    // 周期2（8种）
    Element {
        atomic_number: 3,
        symbol: "Li".to_string(),
        name: "锂".to_string(),
        pronunciation: "lǐ".to_string(),
        english_name: "Lithium".to_string(),
        atomic_weight: 6.941,
        period: 2,
        group: "IA".to_string(),
    },
    Element {
        atomic_number: 4,
        symbol: "Be".to_string(),
        name: "铍".to_string(),
        pronunciation: "pí".to_string(),
        english_name: "Beryllium".to_string(),
        atomic_weight: 9.0122,
        period: 2,
        group: "IIA".to_string(),
    },
    Element {
        atomic_number: 5,
        symbol: "B".to_string(),
        name: "硼".to_string(),
        pronunciation: "péng".to_string(),
        english_name: "Boron".to_string(),
        atomic_weight: 10.811,
        period: 2,
        group: "IIIA".to_string(),
    },
    Element {
        atomic_number: 6,
        symbol: "C".to_string(),
        name: "碳".to_string(),
        pronunciation: "tàn".to_string(),
        english_name: "Carbon".to_string(),
        atomic_weight: 12.011,
        period: 2,
        group: "IVA".to_string(),
    },
    Element {
        atomic_number: 7,
        symbol: "N".to_string(),
        name: "氮".to_string(),
        pronunciation: "dàn".to_string(),
        english_name: "Nitrogen".to_string(),
        atomic_weight: 14.007,
        period: 2,
        group: "VA".to_string(),
    },
    Element {
        atomic_number: 8,
        symbol: "O".to_string(),
        name: "氧".to_string(),
        pronunciation: "yǎng".to_string(),
        english_name: "Oxygen".to_string(),
        atomic_weight: 15.999,
        period: 2,
        group: "VIA".to_string(),
    },
    Element {
        atomic_number: 9,
        symbol: "F".to_string(),
        name: "氟".to_string(),
        pronunciation: "fú".to_string(),
        english_name: "Fluorine".to_string(),
        atomic_weight: 18.998,
        period: 2,
        group: "VIIA".to_string(),
    },
    Element {
        atomic_number: 10,
        symbol: "Ne".to_string(),
        name: "氖".to_string(),
        pronunciation: "nǎi".to_string(),
        english_name: "Neon".to_string(),
        atomic_weight: 20.180,
        period: 2,
        group: "0族".to_string(),
    },
    // 周期3（8种）
    Element {
        atomic_number: 11,
        symbol: "Na".to_string(),
        name: "钠".to_string(),
        pronunciation: "nà".to_string(),
        english_name: "Sodium".to_string(),
        atomic_weight: 22.990,
        period: 3,
        group: "IA".to_string(),
    },
    Element {
        atomic_number: 12,
        symbol: "Mg".to_string(),
        name: "镁".to_string(),
        pronunciation: "měi".to_string(),
        english_name: "Magnesium".to_string(),
        atomic_weight: 24.305,
        period: 3,
        group: "IIA".to_string(),
    },
    Element {
        atomic_number: 13,
        symbol: "Al".to_string(),
        name: "铝".to_string(),
        pronunciation: "lǚ".to_string(),
        english_name: "Aluminum".to_string(),
        atomic_weight: 26.982,
        period: 3,
        group: "IIIA".to_string(),
    },
    Element {
        atomic_number: 14,
        symbol: "Si".to_string(),
        name: "硅".to_string(),
        pronunciation: "guī".to_string(),
        english_name: "Silicon".to_string(),
        atomic_weight: 28.085,
        period: 3,
        group: "IVA".to_string(),
    },
    Element {
        atomic_number: 15,
        symbol: "P".to_string(),
        name: "磷".to_string(),
        pronunciation: "lín".to_string(),
        english_name: "Phosphorus".to_string(),
        atomic_weight: 30.974,
        period: 3,
        group: "VA".to_string(),
    },
    Element {
        atomic_number: 16,
        symbol: "S".to_string(),
        name: "硫".to_string(),
        pronunciation: "liú".to_string(),
        english_name: "Sulfur".to_string(),
        atomic_weight: 32.06,
        period: 3,
        group: "VIA".to_string(),
    },
    Element {
        atomic_number: 17,
        symbol: "Cl".to_string(),
        name: "氯".to_string(),
        pronunciation: "lǜ".to_string(),
        english_name: "Chlorine".to_string(),
        atomic_weight: 35.45,
        period: 3,
        group: "VIIA".to_string(),
    },
    Element {
        atomic_number: 18,
        symbol: "Ar".to_string(),
        name: "氩".to_string(),
        pronunciation: "yà".to_string(),
        english_name: "Argon".to_string(),
        atomic_weight: 39.948,
        period: 3,
        group: "0族".to_string(),
    },
]);
