using System.Text.Json.Serialization;

namespace Feuyeux.AI.Hello;

public class Element
{
    [JsonPropertyName("symbol")]
    public string Symbol { get; set; } = string.Empty;

    [JsonPropertyName("name")]
    public string Name { get; set; } = string.Empty;

    [JsonPropertyName("englishName")]
    public string EnglishName { get; set; } = string.Empty;

    [JsonPropertyName("atomicNumber")]
    public int AtomicNumber { get; set; }

    [JsonPropertyName("atomicWeight")]
    public double AtomicWeight { get; set; }

    [JsonPropertyName("period")]
    public int Period { get; set; }

    [JsonPropertyName("group")]
    public int Group { get; set; }

    [JsonPropertyName("pronunciation")]
    public string Pronunciation { get; set; } = string.Empty;

    [JsonPropertyName("phase")]
    public string Phase { get; set; } = string.Empty;

    [JsonPropertyName("type")]
    public string Type { get; set; } = string.Empty;
}

public static class PeriodicTable
{
    private static readonly Dictionary<string, Element> Elements = new()
    {
        ["H"] = new() { Symbol = "H", Name = "氢", Pronunciation = "qīng", EnglishName = "Hydrogen", AtomicNumber = 1, AtomicWeight = 1.008, Period = 1, Group = 1, Phase = "gas", Type = "nonmetal" },
        ["He"] = new() { Symbol = "He", Name = "氦", Pronunciation = "hài", EnglishName = "Helium", AtomicNumber = 2, AtomicWeight = 4.003, Period = 1, Group = 18, Phase = "gas", Type = "noble gas" },
        ["Li"] = new() { Symbol = "Li", Name = "锂", Pronunciation = "lǐ", EnglishName = "Lithium", AtomicNumber = 3, AtomicWeight = 6.94, Period = 2, Group = 1, Phase = "solid", Type = "alkali metal" },
        ["Be"] = new() { Symbol = "Be", Name = "铍", Pronunciation = "pí", EnglishName = "Beryllium", AtomicNumber = 4, AtomicWeight = 9.012, Period = 2, Group = 2, Phase = "solid", Type = "alkaline earth metal" },
        ["B"] = new() { Symbol = "B", Name = "硼", Pronunciation = "péng", EnglishName = "Boron", AtomicNumber = 5, AtomicWeight = 10.81, Period = 2, Group = 13, Phase = "solid", Type = "metalloid" },
        ["C"] = new() { Symbol = "C", Name = "碳", Pronunciation = "tàn", EnglishName = "Carbon", AtomicNumber = 6, AtomicWeight = 12.011, Period = 2, Group = 14, Phase = "solid", Type = "nonmetal" },
        ["N"] = new() { Symbol = "N", Name = "氮", Pronunciation = "dàn", EnglishName = "Nitrogen", AtomicNumber = 7, AtomicWeight = 14.007, Period = 2, Group = 15, Phase = "gas", Type = "nonmetal" },
        ["O"] = new() { Symbol = "O", Name = "氧", Pronunciation = "yǎng", EnglishName = "Oxygen", AtomicNumber = 8, AtomicWeight = 15.999, Period = 2, Group = 16, Phase = "gas", Type = "nonmetal" },
        ["F"] = new() { Symbol = "F", Name = "氟", Pronunciation = "fú", EnglishName = "Fluorine", AtomicNumber = 9, AtomicWeight = 18.998, Period = 2, Group = 17, Phase = "gas", Type = "halogen" },
        ["Ne"] = new() { Symbol = "Ne", Name = "氖", Pronunciation = "nǎi", EnglishName = "Neon", AtomicNumber = 10, AtomicWeight = 20.180, Period = 2, Group = 18, Phase = "gas", Type = "noble gas" },
        ["Na"] = new() { Symbol = "Na", Name = "钠", Pronunciation = "nà", EnglishName = "Sodium", AtomicNumber = 11, AtomicWeight = 22.990, Period = 3, Group = 1, Phase = "solid", Type = "alkali metal" },
        ["Mg"] = new() { Symbol = "Mg", Name = "镁", Pronunciation = "měi", EnglishName = "Magnesium", AtomicNumber = 12, AtomicWeight = 24.305, Period = 3, Group = 2, Phase = "solid", Type = "alkaline earth metal" },
        ["Al"] = new() { Symbol = "Al", Name = "铝", Pronunciation = "lǚ", EnglishName = "Aluminum", AtomicNumber = 13, AtomicWeight = 26.982, Period = 3, Group = 13, Phase = "solid", Type = "post-transition metal" },
        ["Si"] = new() { Symbol = "Si", Name = "硅", Pronunciation = "guī", EnglishName = "Silicon", AtomicNumber = 14, AtomicWeight = 28.085, Period = 3, Group = 14, Phase = "solid", Type = "metalloid" },
        ["P"] = new() { Symbol = "P", Name = "磷", Pronunciation = "lín", EnglishName = "Phosphorus", AtomicNumber = 15, AtomicWeight = 30.974, Period = 3, Group = 15, Phase = "solid", Type = "nonmetal" },
        ["S"] = new() { Symbol = "S", Name = "硫", Pronunciation = "liú", EnglishName = "Sulfur", AtomicNumber = 16, AtomicWeight = 32.06, Period = 3, Group = 16, Phase = "solid", Type = "nonmetal" },
        ["Cl"] = new() { Symbol = "Cl", Name = "氯", Pronunciation = "lǜ", EnglishName = "Chlorine", AtomicNumber = 17, AtomicWeight = 35.45, Period = 3, Group = 17, Phase = "gas", Type = "halogen" },
        ["Ar"] = new() { Symbol = "Ar", Name = "氩", Pronunciation = "yà", EnglishName = "Argon", AtomicNumber = 18, AtomicWeight = 39.948, Period = 3, Group = 18, Phase = "gas", Type = "noble gas" },
        ["K"] = new() { Symbol = "K", Name = "钾", Pronunciation = "jiǎ", EnglishName = "Potassium", AtomicNumber = 19, AtomicWeight = 39.098, Period = 4, Group = 1, Phase = "solid", Type = "alkali metal" },
        ["Ca"] = new() { Symbol = "Ca", Name = "钙", Pronunciation = "gài", EnglishName = "Calcium", AtomicNumber = 20, AtomicWeight = 40.078, Period = 4, Group = 2, Phase = "solid", Type = "alkaline earth metal" }
    };

    private static readonly Dictionary<int, Element> ElementsByNumber = Elements.Values.ToDictionary(e => e.AtomicNumber);

    public static Element? GetElement(string symbol)
    {
        Elements.TryGetValue(symbol, out var element);
        return element;
    }

    public static Element? GetElementByPosition(int atomicNumber)
    {
        ElementsByNumber.TryGetValue(atomicNumber, out var element);
        return element;
    }

    public static Element? GetElementByName(string name)
    {
        return Elements.Values.FirstOrDefault(e => 
            string.Equals(e.Name, name, StringComparison.OrdinalIgnoreCase) ||
            string.Equals(e.EnglishName, name, StringComparison.OrdinalIgnoreCase));
    }

    public static IEnumerable<Element> GetAllElements()
    {
        return Elements.Values.OrderBy(e => e.AtomicNumber);
    }
}
