package uz.fido_biznes.cms.tags.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Util;

public class FootCellTag extends TagSupport {
   private String ref;
   private String type;
   private String rows;
   private String size;
   private String align;
   private String conditionalFormat;
   private String rowspan;
   private String colspan;

   public void setFor(String v) {
      this.ref = v;
   }

   public void setType(String v) {
      this.type = v;
   }

   public void setRows(String v) {
      this.rows = v;
   }

   public void setSize(String v) {
      this.size = v;
   }

   public void setAlign(String v) {
      this.align = v;
   }

   public void setRowspan(String v) {
      this.rowspan = v;
   }

   public void setColspan(String v) {
      this.colspan = v;
   }

   public int doStartTag() throws JspException {
      try {
         FootRowTag footRowTag = (FootRowTag)this.getParent();
         FootCell footCell = new FootCell();
         footCell.setRef(this.ref);
         footCell.setType(this.type);
         footCell.setRows(this.rows);
         footCell.setSize(this.size);
         footCell.setAlign(this.align);
         footCell.setRowspan(this.rowspan);
         footCell.setColspan(this.colspan);
         footRowTag.addFootCell(footCell);
         return 0;
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }
}
