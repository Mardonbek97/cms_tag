package uz.fido_biznes.cms.tags.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Language;
import uz.fido_biznes.cms.Util;

public class FilterTag extends TagSupport {
   private String operator;
   private int labelIndex = -1;
   private String labelText;
   private int optionIndex = -1;
   private String optionSQL;
   private String mask;
   private String size;
   private String showInGrid;
   private String value;
   private String value2;
   private String referenceName;
   private String requestName;
   private String referenceURL;
   private String requestURL;

   public void setOperator(String v) {
      this.operator = v;
   }

   public void setLabel(int v) {
      this.labelIndex = v;
   }

   public void setLabelText(String labelText) {
      this.labelText = labelText;
   }

   public void setOption(int v) {
      this.optionIndex = v;
   }

   public void setOptionSQL(String v) {
      this.optionSQL = v;
   }

   public void setMask(String mask) {
      this.mask = mask;
   }

   public void setSize(String size) {
      this.size = size;
   }

   public void setShowInGrid(String v) {
      this.showInGrid = v;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public void setValue2(String value2) {
      this.value2 = value2;
   }

   public void setReferenceName(String referenceName) {
      this.referenceName = referenceName;
   }

   public void setRequestName(String requestName) {
      this.requestName = requestName;
   }

   public void setReferenceURL(String referenceURL) {
      this.referenceURL = referenceURL;
   }

   public void setRequestURL(String requestURL) {
      this.requestURL = requestURL;
   }

   public int doStartTag() throws JspException {
      try {
         FieldTag fieldTag = (FieldTag)this.getParent();
         TableTag tableTag = (TableTag)fieldTag.getParent();
         Language lang = tableTag.getLang();
         Field field = fieldTag.getField();
         int filterType = 1;
         if (this.operator == null) {
            this.operator = "=";
         } else if (this.operator.equals("range")) {
            filterType = 2;
            this.operator = ">=";
         } else if (this.operator.equals("choice")) {
            filterType = 3;
            this.operator = "_like_";
         } else if (this.operator.equals("in")) {
            filterType = 4;
            this.operator = "in";
         }

         if (field.getFilterType() > 0) {
            throw new JspException("Table tag: You can not put more than one filter to field (id=" + field.getId() + ").");
         } else {
            field.setFilterType(filterType);
            field.setFilterOperator(this.operator);
            if (this.labelIndex > -1) {
               field.setFilterLabel(lang.get(this.labelIndex));
            } else if (this.labelText != null) {
               field.setFilterLabel(this.labelText);
            }

            if (this.optionIndex > -1) {
               field.setFilterOption(lang.get(this.optionIndex));
            } else if (this.optionSQL != null) {
               field.setFilterOption(tableTag.getStored().execSelect(this.optionSQL));
            }

            field.setFilterMask(this.mask);
            field.setFilterInGrid(this.showInGrid);
            field.setFilterValue(this.value);
            field.setFilterValue2(this.value2);
            field.setFilterSize(this.size);
            field.setFilterReferenceName(this.referenceName);
            field.setFilterRequestName(this.requestName);
            field.setFilterReferenceURL(this.referenceURL);
            field.setFilterRequestURL(this.requestURL);
            return 0;
         }
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }
}
