package uz.fido_biznes.cms.tags;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Util;

public class RequestTag extends TagSupport {
   private String name;
   private String responseType;

   public void setName(String name) {
      this.name = name;
   }

   public void setResponseType(String responseType) {
      this.responseType = responseType;
   }

   public int doStartTag() throws JspException {
      ServletRequest request = this.pageContext.getRequest();
      if (this.name.equals(request.getParameter("request"))) {
         this.responseType = Util.nvl(this.responseType, "json");
         HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
         response.setHeader("RT", this.responseType);

         try {
            JspWriter out = this.pageContext.getOut();
            out.clearBuffer();
            return 1;
         } catch (Exception ex) {
            throw new JspException(Util.getStackTrace(ex));
         }
      } else {
         return 0;
      }
   }
}
