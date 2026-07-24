package uz.fido_biznes.cms;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.zip.GZIPOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import uz.fido_biznes.sql.StoredObject;

public class Resource_IABS {
   public static final int GRID_RECORDS_PER_PAGE = 20;
   public static final String STR_FORM_JS = "fj";
   public static final String STR_FORM_CSS = "fc";
   public static final String STR_TABLE_JS = "tj";
   public static final String STR_MODAL_DIALOG = "modal";
   public static final String STR_STORED = "stored";
   public static final String STR_LANGUAGE = "lang";
   public static final String STR_USER = "user";
   public static final String STR_USER_CACHE = "user_cache";
   public static final String SESSION_EXPIRED = "_session_expired";
   public static final String STR_REQUEST = "request";
   public static final String STR_REFERENCE = "reference";
   public static final char[] HTML_OPEN = "<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=WINDOWS-1251\">".toCharArray();
   public static byte[] FORM_JS;
   public static byte[] FORM_CSS;
   public static byte[] TABLE_JS;
   public static byte[] MODAL_DIALOG;
   private static HashMap themeHashMap = new HashMap();
   private static String THEME_URL;
   private static String THEME_ID;
   private static String BROWSER_NAME;
   private static String BROWSER_VERSION;
   private static boolean IS_CROSS_BROWSER;
   private static final String STR_IE = "msie";
   private static final String STR_FIREFOX = "firefox";
   private static final String STR_CHROME = "chrome";
   private static final String STR_SAFARI = "safari";
   private static final String STR_OPERA = "opera";

   private static byte[] getGZIP(byte[] header, byte[] data) throws IOException {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      GZIPOutputStream gzip = new GZIPOutputStream(baos);
      if (header != null) {
         gzip.write(header);
      }

      gzip.write(data);
      gzip.finish();
      baos.flush();
      return baos.toByteArray();
   }

   private static void writeResource(byte[] data, HttpServletRequest request, HttpServletResponse response, String text) throws IOException {
      BufferedInputStream bis = null;
      OutputStream os = null;

      try {
         byte[] header = null;
         if (text != null) {
            header = text.getBytes();
         }

         int headerLength = 0;
         if (header != null) {
            headerLength = header.length;
         }

         response.setContentLength(headerLength + data.length);
         os = new BufferedOutputStream(response.getOutputStream());
         if (header != null) {
            os.write(header, 0, header.length);
         }

         os.write(data, 0, data.length);
      } finally {
         if (os != null) {
            try {
               os.flush();
               os.close();
               response.getOutputStream().close();
            } catch (Throwable var13) {
            }
         }

         if (bis != null) {
            bis.close();
         }

      }

   }

   private static void writeResource(byte[] data, HttpServletRequest request, HttpServletResponse response) throws IOException {
      writeResource(data, request, response, (String)null);
   }

   public static void evalRequest(User user, HttpServletRequest request, HttpServletResponse response) throws IOException, JspException {
      response.setHeader("Cache-Control", "private, no-store, no-cache, must-revalidate");
      response.setHeader("Pragma", "no-cache");
      setBrowserModel(request, response);
      if (request.getParameter("fc") != null) {
         sendFormCSS(request, response);
      } else if (request.getParameter("fj") != null) {
         sendFormJS(request, response, user);
      } else if (request.getParameter("tj") != null) {
         sendTableJS(request, response);
      } else if (request.getParameter("modal") != null) {
         sendModalDialog(request, response, user);
      }

   }

   private static byte[] getAllBytes(InputStream is) throws IOException {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      byte[] data = new byte[4096];
      System.out.println(data.length);

      int nRead;
      while((nRead = is.read(data, 0, data.length)) != -1) {
         buffer.write(data, 0, nRead);
      }

      buffer.flush();
      return buffer.toByteArray();
   }

   private static byte[] getResource(String path) throws IOException {
      InputStream is = Resource.class.getResourceAsStream(path);
      return getAllBytes(is);
   }

   private static void sendFormCSS(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("text/css; charset=Windows-1251");
      if (FORM_CSS == null) {
         FORM_CSS = getResource("/uz/fido_biznes/cms/resource/form.css.rc");
      }

      if (!IS_CROSS_BROWSER) {
         writeResource(FORM_CSS, request, response);
      }

   }

   private static void sendFormJS(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
      response.setContentType("text/javascript; charset=Windows-1251");
      String text = "CMS_VERSION='" + Util.getVersion() + "';";
      if (user != null) {
         text = text + "__contextPath='" + user.getContextPath() + "';";
      }

      if (FORM_JS == null) {
         FORM_JS = getResource("/uz/fido_biznes/cms/resource/form.js.rc");
      }

      if (!IS_CROSS_BROWSER) {
         writeResource(FORM_JS, request, response, text);
      }

   }

   private static void sendTableJS(HttpServletRequest request, HttpServletResponse response) throws IOException {
      response.setContentType("text/javascript; charset=Windows-1251");
      if (TABLE_JS == null) {
         TABLE_JS = getResource("/uz/fido_biznes/cms/resource/table.js.rc");
      }

      writeResource(TABLE_JS, request, response);
   }

   private static void sendModalDialog(HttpServletRequest request, HttpServletResponse response, User user) throws IOException, JspException {
      response.setContentType("text/html; charset=Windows-1251");
      if (MODAL_DIALOG == null) {
         MODAL_DIALOG = getResource("/uz/fido_biznes/cms/resource/modal.html.rc");
      }

      String userAgent = request.getHeader("User-Agent").toLowerCase();
      StoredObject stored = (StoredObject)request.getSession().getValue("stored");
      writeResource(MODAL_DIALOG, request, response, getFormCss(user, stored, userAgent) + getFormJS(user, userAgent, stored));
   }

   public static String getFormCss(User user, StoredObject stored, String userAgent) throws JspException {
      String contextPath = "";
      if (user != null) {
         contextPath = user.getContextPath();
      }

      String cssPath = "form";
      if (THEME_URL != null) {
         cssPath = "themes/" + THEME_URL;
      } else {
         iabs.User user2 = (iabs.User)user;
         if (user2 != null && getThemeURL(user2, userAgent) != null) {
            cssPath = "themes/" + getThemeURL(user2, userAgent);
         } else {
            cssPath = "themes/theme_FIDO";
         }
      }

      return userAgent.indexOf("msie") < 0 ? "<link rel=stylesheet type=\"text/css\" href=\"" + contextPath + "/" + cssPath + "_cross.css?" + "fc" + "=" + Util.getVersion() + "\">" : "<link rel=stylesheet type=\"text/css\" href=\"" + contextPath + "/" + cssPath + ".css?" + "fc" + "=" + Util.getVersion() + "\">";
   }

   public static String getFormJS(User user, String userAgent, StoredObject stored) throws JspException {
      String contextPath = "";
      if (user != null) {
         contextPath = user.getContextPath();
      }

      return userAgent.indexOf("msie") < 0 ? "<script type=\"text/javascript\" src=\"" + contextPath + "/form_cross.js?" + "fj" + "=" + Util.getVersion() + "\"></script>" : "<script type=\"text/javascript\" src=\"" + contextPath + "/form.js?" + "fj" + "=" + Util.getVersion() + "\"></script>";
   }

   public static String getTableJS(User user, String userAgent, StoredObject stored) throws JspException {
      String contextPath = "";
      if (user != null) {
         contextPath = user.getContextPath();
      }

      return userAgent.indexOf("msie") < 0 ? "<script type=\"text/javascript\" src=\"" + contextPath + "/table_cross.js?" + "tj" + "=" + Util.getVersion() + "\"></script>" : "<script type=\"text/javascript\" src=\"" + contextPath + "/table.js?" + "tj" + "=" + Util.getVersion() + "\"></script>";
   }

   public static void setThemeUrl(String userId, String userAgent, String themeURL) {
      themeHashMap.put(userId + userAgent, themeURL);
   }

   public static String getThemeURL(User user, String userAgent) {
      iabs.User user2 = (iabs.User)user;
      return (String)themeHashMap.get(user2.getUserCode() + userAgent);
   }

   public static void setThemeURL(String themeURL) {
      THEME_URL = themeURL;
   }

   public static String getThemeURL() {
      return THEME_URL;
   }

   public static void setThemeID(String themeID) {
      THEME_ID = themeID;
   }

   public static String getThemeID() {
      return THEME_ID;
   }

   public static void setBrowserModel(HttpServletRequest request, HttpServletResponse response) {
      String userAgent = request.getHeader("User-Agent").toLowerCase();
      if (!"msie".equals(BROWSER_NAME)) {
         IS_CROSS_BROWSER = true;
      } else {
         String v = BROWSER_VERSION.substring(0, BROWSER_VERSION.indexOf("."));
         if (Integer.parseInt(v) > 10) {
            IS_CROSS_BROWSER = true;
         } else {
            IS_CROSS_BROWSER = false;
         }
      }

   }

   public boolean isCrossBrowser() {
      return IS_CROSS_BROWSER;
   }

   public String getBrowserModel() {
      return IS_CROSS_BROWSER ? "Browser : " + BROWSER_NAME + " version : " + BROWSER_VERSION + " ===> CROSS" : "Browser : " + BROWSER_NAME + " version : " + BROWSER_VERSION + " ===> NOT CROSS";
   }
}
