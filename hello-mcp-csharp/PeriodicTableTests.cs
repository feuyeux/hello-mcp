using Xunit;
using Feuyeux.AI.Hello;

namespace Feuyeux.AI.Hello.Tests
{
    public class PeriodicTableTests
    {
        [Fact]
        public void GetElementByName_ShouldReturnSilicon_WhenSearchingFor硅()
        {
            // Act
            var element = PeriodicTable.GetElementByName("硅");
            
            // Assert
            Assert.NotNull(element);
            Assert.Equal("Si", element.Symbol);
            Assert.Equal(14, element.AtomicNumber);
            Assert.Equal("Silicon", element.EnglishName);
        }

        [Fact]
        public void GetElementByPosition_ShouldReturnSilicon_WhenSearchingForPosition14()
        {
            // Act
            var element = PeriodicTable.GetElementByPosition(14);
            
            // Assert
            Assert.NotNull(element);
            Assert.Equal("Si", element.Symbol);
            Assert.Equal("硅", element.Name);
            Assert.Equal("Silicon", element.EnglishName);
        }

        [Fact]
        public void GetElementByName_ShouldReturnNull_WhenElementNotFound()
        {
            // Act
            var element = PeriodicTable.GetElementByName("unknown");
            
            // Assert
            Assert.Null(element);
        }

        [Fact]
        public void GetElementByPosition_ShouldReturnNull_WhenPositionInvalid()
        {
            // Act
            var element = PeriodicTable.GetElementByPosition(999);
            
            // Assert
            Assert.Null(element);
        }

        [Fact]
        public void GetElementByName_ShouldReturnHydrogen_WhenSearchingFor氢()
        {
            // Act
            var element = PeriodicTable.GetElementByName("氢");
            
            // Assert
            Assert.NotNull(element);
            Assert.Equal("H", element.Symbol);
            Assert.Equal(1, element.AtomicNumber);
        }

        [Fact]
        public void GetElementByPosition_ShouldReturnCarbon_WhenSearchingForPosition6()
        {
            // Act
            var element = PeriodicTable.GetElementByPosition(6);
            
            // Assert
            Assert.NotNull(element);
            Assert.Equal("C", element.Symbol);
            Assert.Equal("碳", element.Name);
        }
    }
}
