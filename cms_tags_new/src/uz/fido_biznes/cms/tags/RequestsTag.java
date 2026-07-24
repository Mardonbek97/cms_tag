package uz.fido_biznes.cms.tags;

import java.io.IOException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import uz.fido_biznes.cms.User;

public class RequestsTag extends ContainerTag {
   public boolean isItRequestedContainer() {
      ServletRequest request = this.pageContext.getRequest();
      return request.getParameter("request") != null;
   }

   protected void sessionOut() throws IOException {
      HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
      if ("ax".equals(request.getHeader("aj"))) {
         HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
         response.setHeader("RT", "script");
         JspWriter out = this.pageContext.getOut();
         User user = (User)this.pageContext.getAttribute("user", 3);
         out.print(user.getSessionOutScript());
      } else {
         super.sessionOut();
      }

   }

   private boolean isMSIE5() {
      HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
      String userAgent = request.getHeader("User-Agent");
      return userAgent != null && userAgent.indexOf("MSIE 5.") > -1;
   }

   protected String getCompressEncoding() {
      return this.isMSIE5() ? "UTF8" : super.getCompressEncoding();
   }

   public int doStartTag() throws JspException {
      if (this.isMSIE5()) {
         this.pageContext.getResponse().setContentType("text/html;charset=UTF8");
      }

      return super.doStartTag();
   }
}
