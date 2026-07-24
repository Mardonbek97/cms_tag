package uz.fido_biznes.cms.tags.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Language;
import uz.fido_biznes.cms.Util;

public class FieldTag extends TagSupport {
   private Field field;
   private String fieldId;
   private String name;
   private int labelIndex = -1;
   private String labelText;
   private String type;
   private String color;
   private String bgColor;
   private String encrypted;
   private String entityName;

   public void setEntityName(String entityName) {
      this.entityName = entityName;
   }

   public String getEntityName() {
      return this.entityName;
   }

   public void setEncrypted(String encrypted) {
      this.encrypted = encrypted;
   }

   public String getEncrypted() {
      return this.encrypted;
   }

   public void setId(String v) {
      this.fieldId = v;
   }

   public void setName(String v) {
      this.name = v;
   }

   public void setLabel(int v) {
      this.labelIndex = v;
   }

   public void setLabelText(String labelText) {
      this.labelText = labelText;
   }

   public void setType(String v) {
      this.type = v;
   }

   public void setColor(String color) {
      this.color = color;
   }

   public String getBgColor() {
      return this.bgColor;
   }

   public void setBgColor(String bgColor) {
      this.bgColor = bgColor;
   }

   public Field getField() {
      return this.field;
   }

   public int doStartTag() throws JspException {
      try {
         TableTag tableTag = (TableTag)this.getParent();
         Language lang = tableTag.getLang();
         this.field = new Field();
         this.field.setId(this.fieldId);
         this.field.setName(this.name);
         if (this.labelIndex > -1) {
            this.field.setLabel(lang.get(this.labelIndex));
         } else if (this.labelText != null) {
            this.field.setLabel(this.labelText);
         }

         if ("Y".equals(this.encrypted)) {
            if (this.entityName == null || this.entityName.isEmpty()) {
               throw new JspException("entityName attribute cannot be empty or null if encrypted attribute value was set as 'Y'");
            }

            this.field.setEncrypted(this.encrypted);
            this.field.setEntityName(this.entityName);
         }

         this.field.setType(this.type);
         this.field.setColor(this.color);
         this.field.setBgColor(this.bgColor);
         return 1;
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }

   public int doEndTag() throws JspException {
      if (this.field != null) {
         TableTag tableTag = (TableTag)this.getParent();
         Value value = tableTag.getValue();
         value.addField(this.field);
      }

      this.field = null;
      return 6;
   }
}
