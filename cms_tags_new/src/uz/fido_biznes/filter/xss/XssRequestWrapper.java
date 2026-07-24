package uz.fido_biznes.filter.xss;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class XssRequestWrapper extends HttpServletRequestWrapper {
   private Map<String, String[]> sanitizedQueryString;

   public XssRequestWrapper(HttpServletRequest servletRequest) {
      super(servletRequest);
   }

   public String[] getParameterValues(String parameter) {
      String[] values = super.getParameterValues(parameter);
      if (values == null) {
         return null;
      } else {
         int count = values.length;
         String[] encodedValues = new String[count];

         for(int i = 0; i < count; ++i) {
            encodedValues[i] = XssSanitizerUtil.stripXSS(values[i]);
         }

         return encodedValues;
      }
   }

   public String getParameter(String parameter) {
      String value = super.getParameter(parameter);
      if (value != null) {
         value = XssSanitizerUtil.stripXSS(value);
      }

      return value;
   }

   public String getHeader(String name) {
      String value = super.getHeader(name);
      if (value != null) {
         value = XssSanitizerUtil.stripXSS(value);
      }

      return value;
   }

   public Enumeration<String> getParameterNames() {
      return Collections.enumeration(this.getParameterMap().keySet());
   }

   public Map<String, String[]> getParameterMap() {
      if (this.sanitizedQueryString == null) {
         Map<String, String[]> res = new HashMap();
         Map<String, String[]> originalQueryString = super.getParameterMap();
         if (originalQueryString != null) {
            for(String key : originalQueryString.keySet()) {
               String[] rawVals = originalQueryString.get(key);
               String[] snzVals = new String[rawVals.length];

               for(int i = 0; i < rawVals.length; ++i) {
                  snzVals[i] = XssSanitizerUtil.stripXSS(rawVals[i]);
                  System.out.println("Sanitized: " + rawVals[i] + " to " + snzVals[i]);
               }

               res.put(XssSanitizerUtil.stripXSS(key), snzVals);
            }
         }

         this.sanitizedQueryString = res;
      }

      return this.sanitizedQueryString;
   }

   public Cookie[] getCookies() {
      Cookie[] cookies = super.getCookies();

      for(Cookie c : cookies) {
         String name = c.getName();
         String value = c.getValue();
         XssSanitizerUtil.stripXSS(value);
      }

      return cookies;
   }
}
