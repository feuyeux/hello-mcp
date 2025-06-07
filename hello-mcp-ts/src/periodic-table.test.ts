import { getElementByName, getElementByPosition } from './periodic-table';

describe('Periodic Table', () => {
  test('should get element by name', () => {
    const element = getElementByName('硅');
    expect(element).toBeDefined();
    expect(element?.symbol).toBe('Si');
    expect(element?.atomicNumber).toBe(14);
    expect(element?.englishName).toBe('Silicon');
  });

  test('should get element by position', () => {
    const element = getElementByPosition(14);
    expect(element).toBeDefined();
    expect(element?.symbol).toBe('Si');
    expect(element?.name).toBe('硅');
    expect(element?.englishName).toBe('Silicon');
  });

  test('should return undefined for unknown element name', () => {
    const element = getElementByName('unknown');
    expect(element).toBeUndefined();
  });

  test('should return undefined for invalid position', () => {
    const element = getElementByPosition(999);
    expect(element).toBeUndefined();
  });

  test('should get hydrogen by name', () => {
    const element = getElementByName('氢');
    expect(element).toBeDefined();
    expect(element?.symbol).toBe('H');
    expect(element?.atomicNumber).toBe(1);
  });

  test('should get carbon by position', () => {
    const element = getElementByPosition(6);
    expect(element).toBeDefined();
    expect(element?.symbol).toBe('C');
    expect(element?.name).toBe('碳');
  });
});
