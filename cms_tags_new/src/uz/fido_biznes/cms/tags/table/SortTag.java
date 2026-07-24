package uz.fido_biznes.cms.tags.table;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Util;

public class SortTag extends TagSupport {
   private String direction;
   private String orderKey;

   public void setDirection(String direction) {
      this.direction = direction;
   }

   public void setOrderKey(String orderKey) {
      this.orderKey = orderKey;
   }

   public int doStartTag() throws JspException {
      try {
         FieldTag fieldTag = (FieldTag)this.getParent();
         TableTag tableTag = (TableTag)fieldTag.getParent();
         Field field = fieldTag.getField();
         tableTag.putSort(this.orderKey, new Sort(field.getId(), field.getName(), this.direction));
         return 0;
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }
}
