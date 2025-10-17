package org.feuyeux.ai.hello.container;

import jakarta.servlet.Servlet;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.FilterDef;
import org.apache.tomcat.util.descriptor.web.FilterMap;

public class TomChat {
  TomChat() {}

  public static Tomcat createTomcatServer(String contextPath, int port, Servlet servlet) {
    // 配置 Tomcat 的日志编码
    configureTomcatLogging();

    var tomcat = new Tomcat();
    tomcat.setPort(port);

    String baseDir = System.getProperty("java.io.tmpdir");
    tomcat.setBaseDir(baseDir);

    Context context = tomcat.addContext(contextPath, baseDir);

    // Add transport servlet to Tomcat
    org.apache.catalina.Wrapper wrapper = context.createWrapper();
    wrapper.setName("mcpServlet");
    wrapper.setServlet(servlet);
    wrapper.setLoadOnStartup(1);
    wrapper.setAsyncSupported(true);
    context.addChild(wrapper);
    context.addServletMappingDecoded("/*", "mcpServlet");

    var filterDef = new FilterDef();
    filterDef.setFilterClass(HelloMcpServletFilter.class.getName());
    filterDef.setFilterName(HelloMcpServletFilter.class.getSimpleName());
    context.addFilterDef(filterDef);

    var filterMap = new FilterMap();
    filterMap.setFilterName(HelloMcpServletFilter.class.getSimpleName());
    filterMap.addURLPattern("/*");
    context.addFilterMap(filterMap);

    var connector = tomcat.getConnector();
    connector.setAsyncTimeout(3000);
    // 设置 URI 编码为 UTF-8
    connector.setURIEncoding("UTF-8");
    // 设置使用 body 编码作为 URI 编码
    connector.setUseBodyEncodingForURI(true);

    return tomcat;
  }

  /** 配置 Tomcat 的日志输出编码为 UTF-8 */
  private static void configureTomcatLogging() {
    try {
      // 设置系统属性以强制使用 UTF-8
      System.setProperty("java.util.logging.ConsoleHandler.encoding", "UTF-8");
      System.setProperty("sun.stdout.encoding", "UTF-8");
      System.setProperty("sun.stderr.encoding", "UTF-8");

      // 获取根日志记录器
      Logger rootLogger = Logger.getLogger("");

      // 移除所有现有的处理器
      for (Handler handler : rootLogger.getHandlers()) {
        rootLogger.removeHandler(handler);
      }

      // 创建新的控制台处理器并设置 UTF-8 编码
      ConsoleHandler consoleHandler = new ConsoleHandler();
      consoleHandler.setLevel(Level.INFO);
      try {
        consoleHandler.setEncoding("UTF-8");
      } catch (UnsupportedEncodingException e) {
        // 如果 UTF-8 不支持，使用默认编码
        System.err.println("Warning: UTF-8 encoding not supported, using default encoding");
      }

      // 使用简单格式化器
      System.setProperty(
          "java.util.logging.SimpleFormatter.format",
          "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %2$s %5$s%6$s%n");
      consoleHandler.setFormatter(new SimpleFormatter());

      // 添加处理器到根日志记录器
      rootLogger.addHandler(consoleHandler);
      rootLogger.setLevel(Level.INFO);

      // 配置 Tomcat 相关的日志记录器
      configureLogger("org.apache.catalina", consoleHandler);
      configureLogger("org.apache.coyote", consoleHandler);
      configureLogger("org.apache.tomcat", consoleHandler);

    } catch (Exception e) {
      System.err.println("Failed to configure Tomcat logging: " + e.getMessage());
      e.printStackTrace();
    }
  }

  /** 配置指定名称的日志记录器 */
  private static void configureLogger(String loggerName, Handler handler) {
    Logger logger = Logger.getLogger(loggerName);
    logger.setUseParentHandlers(false);
    logger.addHandler(handler);
    logger.setLevel(Level.INFO);
  }

  /**
   * Finds an available port on the local machine.
   *
   * @return an available port number
   * @throws IllegalStateException if no available port can be found
   */
  public static int findAvailablePort() {
    try (final ServerSocket socket = new ServerSocket()) {
      socket.bind(new InetSocketAddress(0));
      return socket.getLocalPort();
    } catch (final IOException e) {
      throw new IllegalStateException("Cannot bind to an available port!", e);
    }
  }
}
