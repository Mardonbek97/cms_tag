package uz.fido_biznes.cms.tags.vfm;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

public class ColumnTag extends TagSupport {
   private String name;
   private String value;

   public int doStartTag() throws JspException {
      VfmTag vfmTag = (VfmTag)this.getParent();
      vfmTag.putColumn(this.name, this.value);
      return 0;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }
}
