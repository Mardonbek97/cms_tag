package uz.fido_biznes.cms.tags;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import uz.fido_biznes.cms.Language;
import uz.fido_biznes.cms.Resource;
import uz.fido_biznes.cms.User;
import uz.fido_biznes.cms.Util;
import uz.fido_biznes.sql.StoredObject;

public class FormTag extends BodyTagSupport {
   protected int siTitle = -1;
   protected String title;
   protected String minWidth;
   protected String minHeight;
   protected String emptyForm;
   protected boolean noCache = false;
   protected String bodyClassName = "";
   protected String base;
   private JspWriter enclosingWriter;
   protected String allowedUrls = "";

   public void setBodyClassName(String bodyClassName) {
      this.bodyClassName = bodyClassName;
   }

   public void setTitle(int siTitle) {
      this.siTitle = siTitle;
   }

   public void setTitleText(String title) {
      this.title = title;
   }

   public void setMinHeight(String minHeight) {
      this.minHeight = minHeight;
   }

   public void setMinWidth(String minWidth) {
      this.minWidth = minWidth;
   }

   public void setEmptyForm(String emptyForm) {
      this.emptyForm = emptyForm;
   }

   public void setNoCache(String noCache) {
      this.noCache = true;
   }

   public void setAllowedUrls(String allowedUrls) {
      this.allowedUrls = allowedUrls;
   }

   private String myReplace(String orig, String search, String replace) {
      StringBuffer buf = new StringBuffer(orig);
      int start = -1;

      while((start = buf.toString().indexOf(search)) != -1) {
         buf.replace(start, start + search.length(), replace);
      }

      return buf.toString();
   }

   protected void printHeader() throws IOException, SQLException, JspException {
      String userAgent = ((HttpServletRequest)this.pageContext.getRequest()).getHeader("User-Agent").toLowerCase();
      JspWriter out = this.pageContext.getOut();
      out.print(Resource.HTML_OPEN);
      User user = (User)this.pageContext.getAttribute("user", 3);
      String headerMfo = (String)this.pageContext.getAttribute("headerMFO", 4);
      StoredObject stored = (StoredObject)this.pageContext.getSession().getValue("stored");
      out.print(String.format("<meta http-equiv=\"Content-Security-Policy\" content=\"default-src 'self' 'unsafe-inline' 'unsafe-eval'; connect-src 'self' %s ; img-src 'self' data:; font-src 'self' data:\">", this.allowedUrls.trim()));
      out.print(Resource.getFormCss(user, stored, userAgent));
      if (user != null && user.getContextPath().length() > 0) {
         out.print("<script>document._contextPath='" + user.getContextPath() + "'</script>");
      }

      out.print(Resource.getFormJS(user, userAgent, stored));
      out.print("</head>");
   }

   protected void printBodyClassName(JspWriter out) throws IOException {
      if (Util.isCross((HttpServletRequest)this.pageContext.getRequest())) {
         out.print("<body class=\"isCROSS " + this.bodyClassName + "\">");
      } else {
         out.print("<body class=\"isIE " + this.bodyClassName + "\">");
      }

   }

   public int doStartTag() throws JspException {
      try {
         this.printHeader();
         JspWriter out = this.pageContext.getOut();
         this.printBodyClassName(out);
         if (this.siTitle >= 0) {
            Language lang = (Language)this.pageContext.getAttribute("lang");
            this.title = lang.get(this.siTitle);
         }

         StringBuffer buf = new StringBuffer();
         if (this.emptyForm == null) {
            buf.append("<table id=base class=form align=center cellspacing=1");
            if (this.minWidth != null) {
               buf.append(" minWidth=");
               buf.append(this.minWidth);
            }

            if (this.minHeight != null) {
               buf.append(" minHeight=");
               buf.append(this.minHeight);
            }

            buf.append(">");
            if (this.title != null) {
               buf.append("<tr><td class=formTitle style=\"position:relative;\">");
               buf.append(this.title);
            }

            HttpSession session = this.pageContext.getSession();
            StoredObject stored = (StoredObject)session.getValue("stored");
            if (stored.isDebug()) {
               HttpServletRequest request = (HttpServletRequest)this.pageContext.getRequest();
               String fileUrl = (new File(this.pageContext.getServletContext().getRealPath(((HttpServletRequest)this.pageContext.getRequest()).getContextPath()), request.getRequestURI())).getPath();
               buf.append("<input type=button class=bFormLanguages style=\"height:20px;width:21px;\" value=\"@\" onclick=\"editFormLanguages('").append(this.myReplace(fileUrl, File.separator, "*")).append("')\">");
            }

            buf.append("<tr><td class=content>");
         }

         this.emptyForm = null;
         this.base = buf.toString();
         return 2;
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }

   public JspWriter getEnclosingWriter() {
      return this.enclosingWriter;
   }

   public void doInitBody() throws JspException {
      try {
         super.doInitBody();
         this.bodyContent.write(this.base);
         this.enclosingWriter = this.bodyContent.getEnclosingWriter();
      } catch (IOException ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }

   public int doEndTag() throws JspException {
      try {
         if (this.bodyContent != null) {
            JspWriter out = this.pageContext.getOut();
            this.bodyContent.writeOut(out);
            this.bodyContent.clearBody();
         }
      } catch (IOException ex) {
         throw new JspException(Util.getStackTrace(ex));
      } finally {
         this.enclosingWriter = null;
      }

      return 6;
   }
}
