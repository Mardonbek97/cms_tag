package uz.fido_biznes.cms.tags.table;

import java.util.Vector;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Util;

public class FootRowTag extends TagSupport {
   private Vector footRow;

   public void addFootCell(FootCell v) {
      this.footRow.addElement(v);
   }

   public int doStartTag() throws JspException {
      this.footRow = new Vector();
      return 1;
   }

   public int doEndTag() throws JspException {
      try {
         FootTag footTag = (FootTag)this.getParent();
         footTag.addFootRow(this.footRow);
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      } finally {
         this.footRow = null;
      }

      return 6;
   }
}
