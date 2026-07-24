package uz.fido_biznes.filter.csrf;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class MyHttpRequestWrapper extends HttpServletRequestWrapper {
   private Map<String, String[]> escapedParametersValuesMap = new HashMap();

   public MyHttpRequestWrapper(HttpServletRequest req) {
      super(req);
   }

   public String getParameter(String name) {
      String[] escapedParameterValues = (String[])this.escapedParametersValuesMap.get(name);
      String escapedParameterValue = null;
      if (escapedParameterValues != null) {
         escapedParameterValue = escapedParameterValues[0];
      } else {
         super.getParameter(name);
         this.escapedParametersValuesMap.put(name, new String[]{escapedParameterValue});
      }

      return escapedParameterValue;
   }

   public String[] getParameterValues(String name) {
      String[] escapedParameterValues = (String[])this.escapedParametersValuesMap.get(name);
      if (escapedParameterValues == null) {
         String[] parametersValues = super.getParameterValues(name);
         escapedParameterValues = new String[parametersValues.length];

         for(int i = 0; i < parametersValues.length; ++i) {
            String parameterValue = parametersValues[i];
            escapedParameterValues[i] = parameterValue;
         }

         this.escapedParametersValuesMap.put(name, escapedParameterValues);
      }

      return escapedParameterValues;
   }
}
