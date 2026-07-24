package uz.fido_biznes.cms.tags.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Language;
import uz.fido_biznes.cms.Util;

public class SumTag extends TagSupport {
   private static final long serialVersionUID = 1L;
   private int labelIndex = -1;
   private String labelText;
   private String type;

   public String getLabelText() {
      return this.labelText;
   }

   public void setLabelText(String labelText) {
      this.labelText = labelText;
   }

   public void setLabel(int v) {
      this.labelIndex = v;
   }

   public String getType() {
      return this.type;
   }

   public void setType(String type) {
      this.type = type;
   }

   public int doStartTag() throws JspException {
      try {
         FieldTag fieldTag = (FieldTag)this.getParent();
         TableTag tableTag = (TableTag)fieldTag.getParent();
         Language lang = tableTag.getLang();
         Field field = fieldTag.getField();
         if (this.labelIndex > -1) {
            field.setSumLabel(lang.get(this.labelIndex));
         } else if (this.labelText != null) {
            field.setSumLabel(this.labelText);
         }

         if (field.isSumTegExist()) {
            throw new JspException("Table tag: You can not put more than one sum to field (id=" + field.getId() + ").");
         } else {
            field.setSumTegExist(true);
            if (this.type != null) {
               field.setSumType(this.type);
            } else {
               field.setSumType("");
            }

            return 0;
         }
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }
}
