package uz.fido_biznes.cms.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class MyTagClass extends TagSupport {
   public int doStartTag() throws JspException {
      try {
         this.pageContext.getOut().write("YOU ARE AWESOME MAN BRO!");
      } catch (IOException e) {
         e.printStackTrace();
      }

      return super.doStartTag();
   }
}
