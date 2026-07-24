package uz.fido_biznes.filter.xss;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class XssSecurityFilter implements Filter {
   public void init(FilterConfig filterConfig) throws ServletException {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
      HttpServletResponse httpServletResponse = (HttpServletResponse)response;
      XssRequestWrapper wrappedRequest = new XssRequestWrapper((HttpServletRequest)request);
      httpServletResponse.setHeader("X-Content-Type-Options", "nosniff");
      httpServletResponse.setHeader("X-Frame-Options", "SAMEORIGIN");
      httpServletResponse.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
      chain.doFilter(wrappedRequest, response);
   }

   public void destroy() {
   }
}
