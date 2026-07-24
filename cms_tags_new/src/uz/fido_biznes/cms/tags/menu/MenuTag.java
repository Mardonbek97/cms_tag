package uz.fido_biznes.cms.tags.menu;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.Tag;
import uz.fido_biznes.cms.JHash;
import uz.fido_biznes.cms.Util;

public class MenuTag extends BodyTagSupport {
   private String labelText;
   private String action;
   private String url;
   private String target;
   private String items;
   private Menu menu;

   public void setLabelText(String labelText) {
      this.labelText = labelText;
   }

   public void setTarget(String target) {
      this.target = target;
   }

   public void setUrl(String url) {
      this.url = url;
   }

   public void setAction(String action) {
      this.action = action;
   }

   public void setItems(String items) {
      this.items = items;
   }

   private String getTarget() {
      if (this.target != null) {
         return this.target;
      } else {
         Tag parent = this.getParent();
         return parent instanceof MenuTag ? ((MenuTag)parent).getTarget() : null;
      }
   }

   private String getGoParameter() {
      JHash hash = new JHash();
      hash.put("url", this.url);
      String t = this.getTarget();
      if (t != null && t.length() > 0) {
         hash.putScript("target", t);
      }

      hash.putScript("lock", "false");
      return hash.toString();
   }

   private void initMenu() {
      if ((this.url != null || this.target != null) && this.action != null) {
         throw new IllegalArgumentException("Must be used either url-target or action.");
      } else {
         this.menu = new Menu();
         this.menu.setLabel(this.labelText);
         if (this.url != null) {
            this.menu.setAction("go(" + this.getGoParameter() + ")");
         } else if (this.action != null) {
            this.menu.setAction(this.action);
         }

         if (this.items != null) {
            this.menu.setItems(this.items);
         }

      }
   }

   public int doStartTag() throws JspException {
      this.initMenu();
      Tag parent = this.getParent();
      if (parent instanceof MenuTag) {
         ((MenuTag)parent).menu.addMenu(this.menu);
      }

      return 2;
   }

   public int doEndTag() throws JspException {
      if (this.bodyContent != null) {
         this.bodyContent.clearBody();
      }

      Tag parent = this.getParent();
      if (!(parent instanceof MenuTag)) {
         try {
            JspWriter out = this.pageContext.getOut();
            out.print(this.menu.toString());
         } catch (IOException ex) {
            throw new JspException(Util.getStackTrace(ex));
         }
      }

      return 6;
   }
}
