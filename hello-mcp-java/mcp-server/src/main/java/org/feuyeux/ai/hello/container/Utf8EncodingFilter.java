package org.feuyeux.ai.hello.container;

import jakarta.servlet.*;
import java.io.IOException;

/** UTF-8 编码过滤器 确保所有请求和响应都使用 UTF-8 编码 */
public class Utf8EncodingFilter implements Filter {

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    // 设置请求和响应的字符编码为 UTF-8
    servletRequest.setCharacterEncoding("UTF-8");
    servletResponse.setCharacterEncoding("UTF-8");
    // 不要在这里设置 Content-Type，让 MCP transport 自己设置

    filterChain.doFilter(servletRequest, servletResponse);
  }
}
