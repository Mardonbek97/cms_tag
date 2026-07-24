package uz.fido_biznes.cms;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import uz.fido_biznes.sql.StoredObject;

public class Util {
   public static final String CSRF_SECRET = "CSRF_SECRET";
   private static final String ALGORITHM_SHA256 = "HmacSHA256";
   private static final String STR_IE_MSIA = "msie";
   private static final String STR_IE_TRIDENT = "trident/";
   private static String[] UZC_UPPER = new String[]{"А", "Б", "В", "Г", "Ғ", "Д", "Е", "Ё", "Ж", "З", "И", "Й", "К", "Қ", "Л", "М", "Н", "О", "П", "Р", "С", "Т", "У", "Ў", "Ф", "Х", "Ҳ", "Ч", "Ш", "Ъ", "Э", "Ю", "Я", "Ы"};
   private static String[] UZL_UPPER = new String[]{"A", "B", "V", "G", "G'", "D", "E", "YO", "J", "Z", "I", "Y", "K", "Q", "L", "M", "N", "O", "P", "R", "S", "T", "U", "O`", "F", "X", "H", "C", "SH", "'", "E", "YU", "YA", "I"};
   private static final String version = "CMS_3_3 of 14.11.2024";
   public static final String EMPTY_STRING = "";
   public static final String ENCODING_CP1251 = "Cp1251";
   public static final String ENCODING_ISO8859 = "ISO-8859-1";
   public static final String ENCODING_UTF8 = "UTF-8";
   protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();

   private Util() {
   }

   public static String getVersion() {
      return "CMS_3_3 of 14.11.2024";
   }

   private static String removeORA(String message) {
      String msg = message;
      message = "";

      while(msg.indexOf("ORA-") > -1) {
         String errCode = msg.substring(0, 10);
         msg = msg.substring(11);
         int y = msg.indexOf("ORA-");
         if (y > -1) {
            String txy = msg.substring(0, y).concat("\n");
            msg = msg.substring(y);
            if (errCode.indexOf("ORA-06512") < 0 && errCode.indexOf("ORA-04088") < 0) {
               message = message.concat(txy);
            }
         } else if (errCode.indexOf("ORA-06512") < 0 && errCode.indexOf("ORA-04088") < 0) {
            message = message.concat(msg);
         }
      }

      return replace(message.trim(), "\n\n", "\n");
   }

   public static String getUserMessage(Exception e) {
      String message = e.getMessage();
      if (e instanceof SQLException) {
         SQLException ex = (SQLException)e;
         if (ex.getErrorCode() >= 20000 && ex.getErrorCode() < 20999) {
            message = removeORA(message);
         } else {
            try {
               message = encodeISO(message);
            } catch (Throwable var4) {
            }
         }
      }

      if (message == null) {
         message = e.getClass().getName();
      }

      return message;
   }

   public static void alertUserMessage(Exception e, Writer out) throws IOException {
      alertUserMessage(e, out, true);
   }

   public static void alertUserMessage(Exception e, Writer out, boolean writeScriptTag) throws IOException {
      if (writeScriptTag) {
         out.write("<script>");
      }

      out.write("alert('");
      out.write(quotesEsc(getUserMessage(e)));
      out.write("');");
      if (writeScriptTag) {
         out.write("</script>");
      }

   }

   public static String getLatErrorId(StoredObject stored) {
      try {
         return stored.execFunction("Mlm_Service.Last_Error_Id");
      } catch (Exception var2) {
         return null;
      }
   }

   public static void showUserMessage(StoredObject s, Exception e, Writer out) throws IOException {
      showUserMessage(s, e, out, true);
   }

   public static void showUserMessage(StoredObject s, Exception e, Writer out, boolean writeScriptTag) throws IOException {
      if (getLatErrorId(s) == null) {
         alertUserMessage(e, out, writeScriptTag);
      } else {
         if (writeScriptTag) {
            out.write("<script>");
         }

         out.write("top._t().showUserMessage();");
         if (writeScriptTag) {
            out.write("</script>");
         }
      }

   }

   public static String quotesEsc(String s) {
      if (s == null) {
         return null;
      } else {
         int len = s.length();
         if (len == 0) {
            return s;
         } else {
            StringBuffer buf = null;

            for(int i = 0; i < len; ++i) {
               char ch = s.charAt(i);
               if (ch != '\'' && ch != '\\' && ch != '"') {
                  if (ch == '\n') {
                     if (buf == null) {
                        buf = new StringBuffer(s.substring(0, i));
                     }

                     buf.append('\\');
                     buf.append('n');
                  } else if (ch == '\r') {
                     if (buf == null) {
                        buf = new StringBuffer(s.substring(0, i));
                     }

                     buf.append('\\');
                     buf.append('r');
                  } else if (buf != null) {
                     buf.append(ch);
                  }
               } else {
                  if (buf == null) {
                     buf = new StringBuffer(s.substring(0, i));
                  }

                  buf.append('\\');
                  buf.append(ch);
               }
            }

            if (buf != null) {
               return buf.toString();
            } else {
               return s;
            }
         }
      }
   }

   public static String quotesSQL(String instr) {
      if (instr == null) {
         return null;
      } else {
         int lastPos = 0;

         int k;
         for(k = -1; (k = instr.indexOf(39, lastPos)) != -1; lastPos = k + 2) {
            instr = instr.substring(0, k) + "'" + instr.substring(k);
         }

         return instr;
      }
   }

   public static String nvl(String value, String defaultValue) {
      return value == null ? defaultValue : value;
   }

   public static String nvl(String value) {
      return nvl(value, "");
   }

   public static String encode(String s, String fromEncode, String toEncode) throws UnsupportedEncodingException {
      if (s != null && s.length() != 0) {
         String s1 = new String(s.getBytes(fromEncode), toEncode);

         for(int i = s1.indexOf(63, 0); i >= 0; i = s1.indexOf(63, i + 1)) {
            if (s.charAt(i) != '?') {
               return s;
            }
         }

         return s1;
      } else {
         return s;
      }
   }

   public static String[] encode(String[] s, String fromEncode, String toEncode) throws UnsupportedEncodingException {
      if (s == null) {
         return null;
      } else {
         String[] e = new String[s.length];

         for(int i = 0; i < s.length; ++i) {
            e[i] = encode(s[i], fromEncode, toEncode);
         }

         return e;
      }
   }

   public static String encodeISO(String s, String toEncode) throws UnsupportedEncodingException {
      return encode(s, "ISO-8859-1", toEncode);
   }

   public static String[] encodeISO(String[] s, String toEncode) throws UnsupportedEncodingException {
      return encode(s, "ISO-8859-1", toEncode);
   }

   public static String encodeISO(String s) throws UnsupportedEncodingException {
      return encodeISO(s, "Cp1251");
   }

   public static String[] encodeISO(String[] s) throws UnsupportedEncodingException {
      return encodeISO(s, "Cp1251");
   }

   public static void write(Writer out) throws IOException {
      InputStream stream = Util.class.getResourceAsStream("newjavascript.js");
      InputStreamReader reader = new InputStreamReader(stream);
      BufferedReader br = new BufferedReader(reader);
      String line = null;

      while((line = br.readLine()) != null) {
         out.write(line);
      }

   }

   public static boolean isNoDataFound(Exception ex) {
      if (ex instanceof SQLException) {
         SQLException e = (SQLException)ex;
         if (e.getErrorCode() == 1403) {
            return true;
         }
      }

      return false;
   }

   public static String getStackTrace(Exception ex) {
      StringWriter writer = new StringWriter();
      ex.printStackTrace(new PrintWriter(writer));
      return writer.toString();
   }

   public static boolean isGZIPSupported(HttpServletRequest request) {
      String acceptEncoding = request.getHeader("Accept-Encoding");
      return acceptEncoding != null && acceptEncoding.indexOf("gzip") >= 0;
   }

   public static String bytesToHex(byte[] bytes) {
      char[] hexChars = new char[bytes.length * 2];

      for(int j = 0; j < bytes.length; ++j) {
         int v = bytes[j] & 255;
         hexChars[j * 2] = hexArray[v >>> 4];
         hexChars[j * 2 + 1] = hexArray[v & 15];
      }

      return new String(hexChars);
   }

   public static byte[] hexStringToByteArray(String s) {
      byte[] b = new byte[s.length() / 2];

      for(int i = 0; i < b.length; ++i) {
         int index = i * 2;
         int v = Integer.parseInt(s.substring(index, index + 2), 16);
         b[i] = (byte)v;
      }

      return b;
   }

   public static String replaceUzcToUzland(String s) {
      String ss = s.toUpperCase();
      StringBuffer sb = new StringBuffer(ss.length());
      boolean isFound = false;

      for(int i = 0; i < ss.length(); ++i) {
         isFound = false;
         String c = ss.substring(i, i + 1);
         if (!" ".equals(c) && !"0".equals(c) && !"1".equals(c) && !"2".equals(c) && !"3".equals(c) && !"4".equals(c) && !"5".equals(c) && !"6".equals(c) && !"7".equals(c) && !"8".equals(c) && !"9".equals(c)) {
            for(int j = 0; j < UZC_UPPER.length; ++j) {
               if (UZC_UPPER[j].equals(c)) {
                  if ("Ц".equals(ss.substring(i, i + 1))) {
                     if (!"А".equals(s.substring(i - 1, i)) && !"И".equals(ss.substring(i - 1, i)) && !"Е".equals(ss.substring(i - 1, i)) && !"У".equals(ss.substring(i - 1, i)) && !"Й".equals(ss.substring(i - 1, i)) && !"О".equals(ss.substring(i - 1, i))) {
                        sb.append("S");
                     } else {
                        sb.append("TS");
                     }
                  } else {
                     sb.append(UZL_UPPER[j]);
                  }

                  isFound = true;
                  break;
               }
            }

            if (!isFound) {
               for(int j = 0; j < UZL_UPPER.length; ++j) {
                  if (UZL_UPPER[j].equals(c)) {
                     if ("S".equals(c) && ss.length() >= i + 1) {
                        if (ss.length() >= i + 2 && "H".equals(ss.substring(i + 1, i + 2))) {
                           sb.append("Ш");
                           ++i;
                        } else {
                           sb.append("C");
                        }
                     } else if ("C".equals(c) && ss.length() >= i + 1) {
                        if (ss.length() >= i + 2 && "H".equals(ss.substring(i + 1, i + 2))) {
                           sb.append("Ч");
                           ++i;
                        } else {
                           sb.append("_");
                        }
                     } else if ("Y".equals(c) && ss.length() >= i + 1) {
                        if (ss.length() >= i + 2 && "A".equals(ss.substring(i + 1, i + 2))) {
                           sb.append("Я");
                           ++i;
                        } else {
                           sb.append("Й");
                        }
                     } else if ("Y".equals(c) && ss.length() >= i + 1) {
                        if (ss.length() >= i + 2 && "O".equals(ss.substring(i + 1, i + 2))) {
                           sb.append("Ё");
                           ++i;
                        } else {
                           sb.append("Й");
                        }
                     } else if ("Y".equals(c) && ss.length() >= i + 1) {
                        if (ss.length() >= i + 2 && "U".equals(ss.substring(i + 1, i + 2))) {
                           sb.append("Ю");
                           ++i;
                        } else {
                           sb.append("Й");
                        }
                     } else {
                        sb.append(UZC_UPPER[j]);
                     }

                     isFound = true;
                     break;
                  }
               }

               if (!isFound) {
                  sb.append("_");
               }

               isFound = false;
            }
         } else {
            sb.append(c);
         }
      }

      System.out.println("###########################################################");
      System.out.println("src=" + ss + " dest=" + sb.toString());
      System.out.println("###########################################################");
      return sb.toString();
   }

   public static boolean isCross(HttpServletRequest request) {
      String userAgent = request.getHeader("User-Agent").toLowerCase();
      return userAgent.indexOf("msie") <= 0 && userAgent.indexOf("trident/") <= 0;
   }

   public static String replace(String str, String searchChars, String replaceChars) {
      if (!"".equals(str) && !"".equals(searchChars) && !searchChars.equals(replaceChars)) {
         if (replaceChars == null) {
            replaceChars = "";
         }

         int strLength = str.length();
         int searchCharsLength = searchChars.length();
         StringBuilder buf = new StringBuilder(str);
         boolean modified = false;

         for(int i = 0; i < strLength; ++i) {
            int start = buf.indexOf(searchChars, i);
            if (start == -1) {
               if (i == 0) {
                  return str;
               }

               return buf.toString();
            }

            buf = buf.replace(start, start + searchCharsLength, replaceChars);
            modified = true;
         }

         if (!modified) {
            return str;
         } else {
            return buf.toString();
         }
      } else {
         return str;
      }
   }

   public static String myReplace(String orig, String search, String replace) {
      StringBuffer buf = new StringBuffer(orig);
      int start = -1;

      while((start = buf.toString().indexOf(search)) != -1) {
         buf.replace(start, start + search.length(), replace);
      }

      return buf.toString();
   }

   public static String stringArrayToString(String[] stringArray) {
      if (stringArray == null) {
         return null;
      } else {
         StringBuilder sb = new StringBuilder();

         for(String aStringArray : stringArray) {
            sb.append(aStringArray);
         }

         return sb.toString();
      }
   }

   public static Integer parseInt(String value) {
      return value != null && !value.isEmpty() ? Integer.parseInt(value) : null;
   }

   public static Long parseLong(String value) {
      return value != null && !value.isEmpty() ? Long.parseLong(value) : null;
   }

   public static Double parseDouble(String value) {
      return value != null && !value.isEmpty() ? Double.parseDouble(value) : null;
   }

   public static Float parseFloat(String value) {
      return value != null && !value.isEmpty() ? Float.parseFloat(value) : null;
   }

   public static Short parseShort(String value) {
      return value != null && !value.isEmpty() ? Short.parseShort(value) : null;
   }

   public static String sanitize(String value) {
      if (value != null) {
         value = value.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\\(", "&#40;").replaceAll("\\)", "&#41;").replaceAll("eval\\((.*)\\)", "").replaceAll("[\\\"\\'][\\s]*javascript:(.*)[\\\"\\']", "\"\"").replaceAll("script", "");
      }

      return value;
   }

   public static String[] sanitize(String[] values) {
      if (values == null) {
         return null;
      } else {
         for(int i = 0; i < values.length; ++i) {
            values[i] = sanitize(values[i]);
         }

         return values;
      }
   }

   public static String desanitize(String value) {
      return value;
   }

   public static String[] desanitize(String[] values) {
      return values;
   }

   public static String hmacWithJava(String data) throws NoSuchAlgorithmException, InvalidKeyException {
      SecretKeySpec secretKeySpec = new SecretKeySpec("CSRF_SECRET".getBytes(), "HmacSHA256");
      Mac mac = Mac.getInstance("HmacSHA256");
      mac.init(secretKeySpec);
      return bytesToHex(mac.doFinal(data.getBytes()));
   }

   public static String getSessionId(HttpServletRequest httpReq) {
      HttpSession session = httpReq.getSession();
      return session.getId();
   }
}
