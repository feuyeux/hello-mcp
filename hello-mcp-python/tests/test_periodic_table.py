import pytest
import sys
import os

# Add src to path
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..', 'src'))

from periodic_table import get_element_by_name, get_element_by_position

def test_get_element_by_name():
    """Test getting element by Chinese name"""
    element = get_element_by_name('硅')
    assert element is not None
    assert element['symbol'] == 'Si'
    assert element['atomic_number'] == 14
    assert element['english_name'] == 'Silicon'
    
def test_get_element_by_position():
    """Test getting element by atomic number"""
    element = get_element_by_position(14)
    assert element is not None
    assert element['symbol'] == 'Si'
    assert element['name'] == '硅'
    assert element['english_name'] == 'Silicon'
    
def test_get_unknown_element_by_name():
    """Test getting unknown element by name returns None"""
    element = get_element_by_name('unknown')
    assert element is None
    
def test_get_invalid_position():
    """Test getting element by invalid position returns None"""
    element = get_element_by_position(999)
    assert element is None
    
def test_get_hydrogen():
    """Test getting hydrogen element"""
    element = get_element_by_name('氢')
    assert element is not None
    assert element['symbol'] == 'H'
    assert element['atomic_number'] == 1
    
def test_get_carbon():
    """Test getting carbon element"""
    element = get_element_by_position(6)
    assert element is not None
    assert element['symbol'] == 'C'
    assert element['name'] == '碳'
