package uz.fido_biznes.cms.tags;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import uz.fido_biznes.cms.Resource;
import uz.fido_biznes.cms.User;
import uz.fido_biznes.sql.StoredObject;

public class PageTag extends ContainerTag {
   private String contentType = "text/html";
   private String charset = "WINDOWS-1251";

   public void setContentType(String contentType) {
      this.contentType = contentType;
   }

   public void setCharset(String charset) {
      this.charset = charset;
   }

   public boolean isItRequestedContainer() {
      ServletRequest request = this.pageContext.getRequest();
      return request.getParameter("request") == null && request.getParameter("reference") == null;
   }

   protected void printHeader() throws IOException, SQLException, JspException {
      String userAgent = ((HttpServletRequest)this.pageContext.getRequest()).getHeader("User-Agent").toLowerCase();
      JspWriter out = this.pageContext.getOut();
      out.print(Resource.HTML_OPEN);
      User user = (User)this.pageContext.getAttribute("user", 3);
      String headerMfo = (String)this.pageContext.getAttribute("headerMFO", 4);
      StoredObject stored = (StoredObject)this.pageContext.getSession().getValue("stored");
      out.print(Resource.getFormCss(user, stored, userAgent));
      if (user != null && user.getContextPath().length() > 0) {
         out.print("<script>document._contextPath='" + user.getContextPath() + "'</script>");
      }

      out.print(Resource.getFormJS(user, userAgent, stored));
      out.print("</head>");
   }

   public int doStartTag() throws JspException {
      return super.doStartTag();
   }
}
