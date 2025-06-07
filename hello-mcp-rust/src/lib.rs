pub mod periodic_table;

#[cfg(test)]
mod tests {
    use crate::periodic_table::{PERIODIC_TABLE, Element};

    #[test]
    fn test_get_element_by_name() {
        let silicon = PERIODIC_TABLE.iter().find(|e| e.name == "硅");
        assert!(silicon.is_some());
        let silicon = silicon.unwrap();
        assert_eq!(silicon.symbol, "Si");
        assert_eq!(silicon.atomic_number, 14);
        assert_eq!(silicon.english_name, "Silicon");
    }

    #[test]
    fn test_get_element_by_position() {
        let element = PERIODIC_TABLE.iter().find(|e| e.atomic_number == 14);
        assert!(element.is_some());
        let element = element.unwrap();
        assert_eq!(element.symbol, "Si");
        assert_eq!(element.name, "硅");
        assert_eq!(element.english_name, "Silicon");
    }

    #[test]
    fn test_get_hydrogen() {
        let hydrogen = PERIODIC_TABLE.iter().find(|e| e.name == "氢");
        assert!(hydrogen.is_some());
        let hydrogen = hydrogen.unwrap();
        assert_eq!(hydrogen.symbol, "H");
        assert_eq!(hydrogen.atomic_number, 1);
    }

    #[test]
    fn test_get_carbon() {
        let carbon = PERIODIC_TABLE.iter().find(|e| e.atomic_number == 6);
        assert!(carbon.is_some());
        let carbon = carbon.unwrap();
        assert_eq!(carbon.symbol, "C");
        assert_eq!(carbon.name, "碳");
    }

    #[test]
    fn test_periodic_table_not_empty() {
        assert!(!PERIODIC_TABLE.is_empty());
        assert!(PERIODIC_TABLE.len() > 0);
    }
}
