package uz.fido_biznes.cms.tags;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Util;

public class RefTag extends TagSupport {
   private String name;
   private String get;
   private String put;
   private String url;
   private String callback;
   private String other;

   public void setCallback(String callback) {
      this.callback = callback;
   }

   public void setGet(String get) {
      this.get = get;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setPut(String put) {
      this.put = put;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setOther(String other) {
      this.other = other;
   }

   private String getReference() {
      StringBuffer buf = new StringBuffer("reference=\"{");
      buf.append("name:'");
      buf.append(this.name);
      buf.append("',get:{");
      buf.append(this.get);
      buf.append("}");
      if (this.put != null) {
         buf.append(",put:[");
         buf.append(this.get);
         buf.append("]");
      }

      if (this.url != null) {
         buf.append(",url:'");
         buf.append(this.url);
         buf.append("'");
      }

      if (this.callback != null) {
         buf.append(",callback:");
         buf.append(this.callback);
      }

      if (this.other != null) {
         buf.append(",");
         buf.append(this.other);
      }

      buf.append("}\"");
      return buf.toString();
   }

   public int doStartTag() throws JspException {
      try {
         JspWriter out = this.pageContext.getOut();
         out.print(this.getReference());
         return 0;
      } catch (IOException ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }
}
