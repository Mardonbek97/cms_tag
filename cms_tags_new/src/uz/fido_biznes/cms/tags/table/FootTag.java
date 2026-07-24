package uz.fido_biznes.cms.tags.table;

import java.util.Vector;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Util;

public class FootTag extends TagSupport {
   private Vector feet;

   public void addFootRow(Vector v) {
      this.feet.addElement(v);
   }

   public int doStartTag() throws JspException {
      this.feet = new Vector();
      return 1;
   }

   public int doEndTag() throws JspException {
      try {
         GridTag parent = (GridTag)this.getParent();
         parent.setFoot(this.feet);
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      } finally {
         this.feet = null;
      }

      return 6;
   }
}
