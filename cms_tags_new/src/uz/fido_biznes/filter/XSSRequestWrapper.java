package uz.fido_biznes.filter;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import uz.fido_biznes.cms.Util;

public class XSSRequestWrapper extends HttpServletRequestWrapper {
   public XSSRequestWrapper(ServletRequest request) {
      super((HttpServletRequest)request);
   }

   public String getParameter(String name) {
      String value = super.getParameter(name);
      return Util.sanitize(value);
   }

   public String[] getParameterValues(String name) {
      String[] values = super.getParameterValues(name);
      return Util.sanitize(values);
   }
}
