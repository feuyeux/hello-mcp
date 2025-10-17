package org.feuyeux.ai.hello;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.feuyeux.ai.hello.service.HelloMcpService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * MCP服务器API测试类
 *
 * <p>该类测试MCP服务端功能，包括： - 工具列表查询 - 元素信息获取功能 - REST API端点测试
 */
class ApiSseTests {

  private HelloMcpService service;

  @BeforeEach
  void setUp() {
    service = new HelloMcpService();
  }

  /** 测试HelloMcpService的基本功能 */
  @Test
  void testHelloMcpServiceBasics() {
    // 测试getElement方法 - 中文名称
    String hydrogenResult = service.getElement("氢");
    assertNotNull(hydrogenResult);
    assertTrue(hydrogenResult.contains("Hydrogen"));
    assertTrue(hydrogenResult.contains("H"));

    // 测试getElementByPosition方法
    String carbonResult = service.getElementByPosition(6);
    assertNotNull(carbonResult);
    assertTrue(carbonResult.contains("Carbon"));
    assertTrue(carbonResult.contains("C"));
  }

  /** 测试元素搜索功能 */
  @Test
  void testElementSearch() {
    // 测试按中文名称搜索
    String oxygenResult = service.getElement("氧");
    assertNotNull(oxygenResult);
    assertTrue(oxygenResult.contains("Oxygen"));
    assertTrue(oxygenResult.contains("O"));

    // 测试按位置搜索硅元素
    String siliconResult = service.getElementByPosition(14);
    assertNotNull(siliconResult);
    assertTrue(siliconResult.contains("Silicon") || siliconResult.contains("硅"));
    assertTrue(siliconResult.contains("Si"));
  }

  /** 测试边界情况和错误处理 */
  @Test
  void testErrorHandling() {
    // 测试不存在的元素名称
    String nonExistentResult = service.getElement("不存在的元素");
    assertTrue(nonExistentResult.contains("元素不存在"));

    // 测试无效的原子序数
    String invalidPositionResult = service.getElementByPosition(0);
    assertTrue(invalidPositionResult.contains("元素位置无效"));

    String largePositionResult = service.getElementByPosition(999);
    assertTrue(largePositionResult.contains("无效"));

    // 测试空名称
    String emptyNameResult = service.getElement("");
    assertTrue(emptyNameResult.contains("元素名称不能为空"));

    // 测试null名称
    String nullNameResult = service.getElement(null);
    assertTrue(nullNameResult.contains("元素名称不能为空"));
  }

  /** 测试关键元素 */
  @Test
  void testKeyElements() {
    // 测试关键元素是否存在
    Map<Integer, String> keyElements =
        Map.of(
            1, "H", // 氢
            6, "C", // 碳
            8, "O", // 氧
            14, "Si", // 硅
            26, "Fe", // 铁
            79, "Au", // 金
            118, "Og" // 鿫
            );

    for (Map.Entry<Integer, String> entry : keyElements.entrySet()) {
      int atomicNumber = entry.getKey();
      String expectedSymbol = entry.getValue();

      String result = service.getElementByPosition(atomicNumber);
      assertNotNull(result, "原子序数 " + atomicNumber + " 的元素不存在");
      assertTrue(
          result.contains(expectedSymbol), "原子序数 " + atomicNumber + " 的元素符号应包含 " + expectedSymbol);
      assertTrue(result.contains("原子序数: " + atomicNumber), "结果应包含正确的原子序数");
    }
  }

  /** 模拟MCP客户端调用测试 */
  @Test
  void testMcpClientSimulation() {
    // 模拟MCP工具调用序列
    Object[][] testCases = {
      {"getElement", "氢"},
      {"getElement", "碳"},
      {"getElement", "金"},
      {"getElementByPosition", 1},
      {"getElementByPosition", 6},
      {"getElementByPosition", 79}
    };

    for (Object[] testCase : testCases) {
      String toolName = (String) testCase[0];
      Object param = testCase[1];

      String result;
      if ("getElement".equals(toolName)) {
        result = service.getElement((String) param);
      } else {
        result = service.getElementByPosition((Integer) param);
      }

      System.out.println("工具调用: " + toolName + "(" + param + ")");
      System.out.println("结果: " + result);
      System.out.println("---");

      assertNotNull(result);
      assertFalse(result.isEmpty());
    }
  }

  /** 测试元素信息格式 */
  @Test
  void testElementFormatting() {
    String hydrogenResult = service.getElement("氢");
    assertNotNull(hydrogenResult);

    // 验证格式化结果包含关键信息
    assertTrue(hydrogenResult.contains("Hydrogen"), "格式化结果应包含英文名称");
    assertTrue(hydrogenResult.contains("氢"), "格式化结果应包含中文名称");
    assertTrue(hydrogenResult.contains("H"), "格式化结果应包含符号");
    assertTrue(hydrogenResult.contains("1"), "格式化结果应包含原子序数");
    assertTrue(hydrogenResult.contains("1.008"), "格式化结果应包含原子质量");

    System.out.println("氢元素格式化结果:");
    System.out.println(hydrogenResult);
  }

  /** 测试周期表范围 */
  @Test
  void testPeriodicTableRange() {
    // 测试第一个元素
    String firstElement = service.getElementByPosition(1);
    assertTrue(firstElement.contains("H"));
    assertTrue(firstElement.contains("氢"));

    // 测试最后一个元素
    String lastElement = service.getElementByPosition(118);
    assertTrue(lastElement.contains("Og"));
    assertTrue(lastElement.contains("鿫"));

    // 测试中间元素
    String middleElement = service.getElementByPosition(50);
    assertTrue(middleElement.contains("Sn"));
    assertTrue(middleElement.contains("锡"));
  }

  /** 测试特殊元素 */
  @Test
  void testSpecialElements() {
    // 测试贵金属
    String gold = service.getElement("金");
    assertTrue(gold.contains("Au"));
    assertTrue(gold.contains("Gold"));

    String silver = service.getElement("银");
    assertTrue(silver.contains("Ag"));
    assertTrue(silver.contains("Silver"));

    // 测试稀有气体
    String helium = service.getElement("氦");
    assertTrue(helium.contains("He"));
    assertTrue(helium.contains("Helium"));

    String neon = service.getElement("氖");
    assertTrue(neon.contains("Ne"));
    assertTrue(neon.contains("Neon"));
  }
}
