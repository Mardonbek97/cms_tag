package uz.fido_biznes.cms.tags;

import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import uz.fido_biznes.cms.Util;

public class ReferenceTag extends FormTag {
   private String name;

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public int doStartTag() throws JspException {
      JspWriter out = this.pageContext.getOut();
      ServletRequest request = this.pageContext.getRequest();
      this.base = null;
      if (this.name.equals(request.getParameter("reference"))) {
         try {
            out.clearBuffer();
            this.printHeader();
            this.base = "<body style='margin:0'><table id=base minWidth=fill minHeight=fill><tr><td id=referenceTitle><tr><td><table align=center class=formToolbar cellspacing=2><tr><td id=filterControls><td align=right id=tableControls></table>";
            return 2;
         } catch (IOException ex) {
            throw new JspException(Util.getStackTrace(ex));
         } catch (SQLException e) {
            throw new JspException(Util.getStackTrace(e));
         }
      } else {
         return 0;
      }
   }
}
