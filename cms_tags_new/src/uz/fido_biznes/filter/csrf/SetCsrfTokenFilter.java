package uz.fido_biznes.filter.csrf;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uz.fido_biznes.cms.Util;

public class SetCsrfTokenFilter implements Filter {
   private static SecureRandom random = new SecureRandom();
   private static Pattern excludeUrls = Pattern.compile("^.*.(css|js|images|svg|png|jpg|jpeg|ico|woff2)$", 2);

   public void init(FilterConfig filterConfig) throws ServletException {
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {
      HttpServletRequest httpReq = (HttpServletRequest)request;
      HttpServletResponse httpRes = (HttpServletResponse)response;
      if (this.isWorthyRequest(httpReq)) {
         next.doFilter(httpReq, httpRes);
      } else {
         String uuid = (String)httpReq.getSession().getAttribute("uuid");

         String csrfToken;
         try {
            csrfToken = this.generateCsrfToken(uuid, random.nextLong());
         } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
         }

         httpRes.addHeader("Set-Cookie", String.format("x-csrf-token=%s; SameSite=Strict", csrfToken));
         next.doFilter(httpReq, httpRes);
      }
   }

   public void destroy() {
   }

   private boolean isWorthyRequest(HttpServletRequest request) {
      String url = request.getServletPath();
      System.out.println("Request URI: " + url);
      Matcher m = excludeUrls.matcher(url);
      return m.matches() || url.toLowerCase().matches("/index.jsp|/ibs/index.jsp|/ibs/login_after.jsp|/ibs/login.jsp|/ibs/login_before.jsp");
   }

   private String generateCsrfToken(String sessionId, long randomValue) throws NoSuchAlgorithmException, InvalidKeyException {
      String message = sessionId + "!" + randomValue;
      String hmac = Util.hmacWithJava(message);
      return hmac + "." + message;
   }
}
