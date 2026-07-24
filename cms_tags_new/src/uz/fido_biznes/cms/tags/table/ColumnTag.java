package uz.fido_biznes.cms.tags.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Language;
import uz.fido_biznes.cms.Util;

public class ColumnTag extends TagSupport {
   private String ref;
   private String type;
   private String name;
   private String mask;
   private String enable;
   private String required;
   private String checked;
   private String size;
   private String align;
   private String readonly;
   private String nowrap;
   /***Added for Dynamic Grid*****/
   private String rowspan;
   private String colspan;
   private int labelIndex = -1;
   private String labelText;

   public void setFor(String v) {
      this.ref = v;
   }

   public void setType(String v) {
      this.type = v;
   }

   public void setName(String v) {
      this.name = v;
   }

   public void setMask(String v) {
      this.mask = v;
   }

   public void setEnable(String v) {
      this.enable = v;
   }

   public void setRequired(String v) {
      this.required = v;
   }

   public void setChecked(String v) {
      this.checked = v;
   }

   public void setSize(String v) {
      this.size = v;
   }

   public void setAlign(String v) {
      this.align = v;
   }

   public void setReadonly(String v) {
      this.readonly = v;
   }

   public void setNowrap(String nowrap) {
      this.nowrap = nowrap;
   }

   /***Added for Dynamic Grid*****/
   public void setRowspan(String rowspan) {
      this.rowspan = rowspan;
   }

   public void setColspan(String colspan) {
      this.colspan = colspan;
   }

   public void setLabel(int v) {
      this.labelIndex = v;
   }

   public void setLabelText(String labelText) {
      this.labelText = labelText;
   }

   public int doStartTag() throws JspException {
      try {
         GridTag gridTag = (GridTag)this.getParent();
         Column column = new Column();
         column.setRef(this.ref);
         column.setType(this.type);
         column.setName(this.name);
         column.setMask(this.mask);
         column.setEnable(this.enable);
         column.setRequired(this.required);
         column.setChecked(this.checked);
         column.setSize(this.size);
         column.setAlign(this.align);
         column.setReadonly(this.readonly);
         /***Added for Dynamic Grid*****/
         column.setRowspan(this.rowspan);
         column.setColspan(this.colspan);
         if (this.nowrap != null) {
            column.setNowrap(true);
         }

         if (this.labelIndex > -1) {
            TableTag tableTag = (TableTag)this.getParent().getParent();
            Language lang = tableTag.getLang();
            column.setLabel(lang.get(this.labelIndex));
         } else if (this.labelText != null) {
            column.setLabel(this.labelText);
         }

         gridTag.addColumn(column);
         return 0;
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }
}
