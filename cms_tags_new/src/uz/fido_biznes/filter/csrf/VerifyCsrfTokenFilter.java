package uz.fido_biznes.filter.csrf;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uz.fido_biznes.cms.Util;

public class VerifyCsrfTokenFilter implements Filter {
   public static final String X_CSRF_TOKEN = "x-csrf-token";
   public static final String CSRF_METHODS = "POST";
   private static Pattern excludeUrls = Pattern.compile("^.*.(css|js|images|svg|png|jpg|jpeg|ico|woff2)$", 2);

   public void init(FilterConfig filterConfig) throws ServletException {
   }

   private String getCsrfTokenFromCookie(HttpServletRequest httpReq) {
      Cookie[] cookies = httpReq.getCookies();
      String tokenFromCookie = null;
      if (cookies != null) {
         for(Cookie cookie : cookies) {
            if (X_CSRF_TOKEN.equals(cookie.getName())) {
               tokenFromCookie = cookie.getValue();
            }
         }
      }

      return tokenFromCookie;
   }

   private List<Cookie> getCsrfTokenCookies(HttpServletRequest httpReq) {
      Cookie[] cookies = httpReq.getCookies();
      if (cookies == null) {
         return null;
      } else {
         List<Cookie> tokenCookies = new ArrayList();

         for(Cookie cookie : cookies) {
            if (X_CSRF_TOKEN.equals(cookie.getName())) {
               tokenCookies.add(cookie);
            }
         }

         return tokenCookies;
      }
   }

   public void doFilter(ServletRequest request, ServletResponse response, FilterChain next) throws IOException, ServletException {
      HttpServletRequest httpReq = (HttpServletRequest)request;
      HttpServletResponse httpRes = (HttpServletResponse)response;
      String path = httpReq.getServletPath();
      System.out.println("VerifyCsrfTokenFilter:" + path);
      if (this.isWorthyRequest(httpReq)) {
         next.doFilter(request, response);
      } else if (httpReq.getMethod().matches(CSRF_METHODS)) {
         String csrfToken = httpReq.getHeader(X_CSRF_TOKEN);
         if (csrfToken == null) {
            csrfToken = httpReq.getParameter(X_CSRF_TOKEN);
         }

         if (csrfToken == null) {
            httpRes.sendError(401);
            return;
         }

         List<Cookie> csrfTokenCookies = this.getCsrfTokenCookies(httpReq);
         if (csrfTokenCookies == null) {
            httpRes.sendError(401);
            return;
         }

         boolean hasCsrfTokenInCookie = false;

         for(Cookie cookie : csrfTokenCookies) {
            if (csrfToken.equals(cookie.getValue())) {
               hasCsrfTokenInCookie = true;
            }
         }

         String uuid = (String)httpReq.getSession().getAttribute("uuid");

         try {
            if (hasCsrfTokenInCookie && this.isValidToken(csrfToken, uuid)) {
               next.doFilter(request, response);
            } else {
               httpRes.sendError(401);
            }
         } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
         }
      } else {
         next.doFilter(request, response);
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

   private boolean isAjaxRequest(HttpServletRequest request) {
      Enumeration<String> headers = request.getHeaders("X-Requested-With");
      if (headers == null) {
         return false;
      } else {
         while(headers.hasMoreElements()) {
            String[] split = ((String)headers.nextElement()).split(",");
            int len$ = split.length;
            int i$ = 0;
            if (i$ < len$) {
               String s = split[i$];
               return "XMLHttpRequest".equals(s.trim());
            }
         }

         return false;
      }
   }

   private boolean isValidToken(String csrfToken, String sessionId) throws NoSuchAlgorithmException, InvalidKeyException {
      String message = this.extractMessage(csrfToken);
      String sesId = this.extractSessionId(message);
      if (!sesId.equals(sessionId)) {
         return false;
      } else {
         String hmac = this.extractHmac(csrfToken);
         String hmacWithJava = Util.hmacWithJava(message);
         return hmac.equals(hmacWithJava);
      }
   }

   private String extractMessage(String csrfToken) {
      int i = csrfToken.lastIndexOf(".");
      return csrfToken.substring(i + 1);
   }

   private String extractHmac(String csrfToken) {
      int i = csrfToken.lastIndexOf(".");
      return csrfToken.substring(0, i);
   }

   private String extractSessionId(String message) {
      return message.substring(0, message.indexOf("!"));
   }
}
