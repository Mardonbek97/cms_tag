package uz.fido_biznes.cms.tags;

import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

public class ReferencesTag extends ContainerTag {
   public boolean isItRequestedContainer() {
      ServletRequest request = this.pageContext.getRequest();
      return request.getParameter("reference") != null;
   }

   public int doStartTag() throws JspException {
      this.pageContext.getResponse().setContentType("text/html;charset=WINDOWS-1251");
      return super.doStartTag();
   }
}
