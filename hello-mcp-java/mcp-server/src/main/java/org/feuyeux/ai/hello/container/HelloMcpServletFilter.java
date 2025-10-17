/*
 * Copyright 2025 - 2025 the original author or authors.
 */

package org.feuyeux.ai.hello.container;

import jakarta.servlet.*;
import java.io.IOException;

/**
 * Simple {@link Filter} which sets a value in a thread local. Used to verify whether MCP executions
 * happen on the thread processing the request or are offloaded.
 *
 * @author Daniel Garnier-Moiroux
 */
public class HelloMcpServletFilter implements Filter {

  public static final String THREAD_LOCAL_VALUE = HelloMcpServletFilter.class.getName();

  private static final ThreadLocal<String> holder = new ThreadLocal<>();

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    // 设置请求和响应的字符编码为 UTF-8
    servletRequest.setCharacterEncoding("UTF-8");
    servletResponse.setCharacterEncoding("UTF-8");
    servletResponse.setContentType("application/json; charset=UTF-8");

    holder.set(THREAD_LOCAL_VALUE);
    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      holder.remove();
    }
  }

  public static String getThreadLocalValue() {
    return holder.get();
  }
}
