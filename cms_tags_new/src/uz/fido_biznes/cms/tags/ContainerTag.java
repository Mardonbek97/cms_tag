package uz.fido_biznes.cms.tags;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import uz.fido_biznes.cms.User;
import uz.fido_biznes.cms.Util;

public abstract class ContainerTag extends BodyTagSupport {
   public static final int COMPRESS_DETECT = 0;
   public static final int COMPRESS_ALWAYS = 1;
   public static final int COMPRESS_NEVER = 2;
   private static int compressMode = 0;
   private static String compressEncoding = "Cp1251";

   public static void setCompressMode(int compressMode) {
      ContainerTag.compressMode = compressMode;
   }

   public static void setCompressEncoding(String compressEncoding) {
      ContainerTag.compressEncoding = compressEncoding;
   }

   protected boolean isGZIPSupported() {
      if (compressMode == 1) {
         return true;
      } else {
         return compressMode == 2 ? false : Util.isGZIPSupported((HttpServletRequest)this.pageContext.getRequest());
      }
   }

   protected void sessionOut() throws IOException {
      JspWriter out = this.pageContext.getOut();
      User user = (User)this.pageContext.getAttribute("user", 3);
      out.println("<script>" + user.getSessionOutScript() + "</script>");
   }

   public boolean isSessionOut() {
      return this.pageContext.getAttribute("_session_expired") == Boolean.TRUE;
   }

   public int doStartTag() throws JspException {
      if (this.getParent() != null) {
         throw new JspException("This tag could not be included by another tag.");
      } else {
         try {
            HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
            response.setHeader("Cache-Control", "no-store");
            JspWriter out = this.pageContext.getOut();
            out.clearBuffer();
            if (this.isItRequestedContainer()) {
               if (this.isSessionOut()) {
                  this.sessionOut();
                  return 0;
               } else {
                  return 2;
               }
            } else {
               return 0;
            }
         } catch (Exception ex) {
            throw new JspException(Util.getStackTrace(ex));
         }
      }
   }

   private void writeBodyContent() throws JspException {
      try {
         if (this.isGZIPSupported() && this.bodyContent.getBufferSize() - this.bodyContent.getRemaining() > 150) {
            HttpServletResponse response = (HttpServletResponse)this.pageContext.getResponse();
            response.setHeader("Content-Encoding", "gzip");
            GZIPOutputStream gzip = new GZIPOutputStream(response.getOutputStream());
            OutputStreamWriter out = new OutputStreamWriter(gzip, this.getCompressEncoding());
            this.bodyContent.writeOut(out);
            out.flush();
            gzip.finish();
         } else {
            Writer out = this.pageContext.getOut();
            this.bodyContent.writeOut(out);
         }

      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }

   protected String getCompressEncoding() {
      return compressEncoding;
   }

   public abstract boolean isItRequestedContainer();

   public int doEndTag() throws JspException {
      if (this.isItRequestedContainer()) {
         if (this.isSessionOut()) {
            return 5;
         } else {
            this.writeBodyContent();
            return 5;
         }
      } else {
         return 6;
      }
   }
}
