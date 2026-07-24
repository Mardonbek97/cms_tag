package uz.fido_biznes.cms.tags;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import uz.fido_biznes.cms.JPrimitive;

public class Cache {
   private Hashtable cacheNames = new Hashtable();
   private static final char[] CHAR_SCRIPT = new char[]{'s', 'c', 'r', 'i', 'p', 't'};
   private static final String STR_CACHE_KEY = "_CACHE3_";
   private static final String STR_CACHE_INDEX = "_CACHE_INDEX_";

   public static Cache getFromSession(HttpSession session) {
      Hashtable userCache = (Hashtable)session.getValue("user_cache");
      if (userCache == null) {
         userCache = new Hashtable();
         session.putValue("user_cache", userCache);
      }

      Cache cache = (Cache)userCache.get("_CACHE3_");
      if (cache == null) {
         cache = new Cache();
         userCache.put("_CACHE3_", cache);
      }

      return cache;
   }

   public String getScript(String name, Object body) {
      return this.getScript(new Key(name, name), body);
   }

   public String getScript(PageContext pageContext, Object body) {
      return this.getScript(this.getKeyFromPageContext(pageContext), body);
   }

   private String getScript(Key key, Object body) {
      String result = null;
      if (this.isCached(key)) {
         result = this.getFromCache(key);
      } else {
         result = this.putIntoCache(key, body);
      }

      return result;
   }

   private boolean isCached(Key key) {
      return this.cacheNames.containsKey(key);
   }

   private String getFromCache(Key key) {
      return "top._t().CACHE.get(document," + key.clientKey.toString() + ");";
   }

   private String putIntoCache(Key key, Object body) {
      StringBuffer buf = new StringBuffer();
      buf.append("top._t().CACHE.put(document,");
      buf.append(key.clientKey.toString());
      buf.append(",'");
      String st = null;
      if (body instanceof String) {
         st = (String)body;
      } else if (body instanceof BodyContent) {
         st = ((BodyContent)body).getString();
      } else {
         st = body.toString();
      }

      buf.append(escape(removeWhiteSpaces(st)));
      buf.append("');");
      this.cacheNames.put(key, Boolean.TRUE);
      return buf.toString();
   }

   private Key getKeyFromPageContext(PageContext pageContext) {
      HttpServletRequest request = (HttpServletRequest)pageContext.getRequest();
      String name = request.getRequestURI();
      Integer index = (Integer)pageContext.getAttribute("_CACHE_INDEX_");
      int i = 0;
      if (index != null) {
         i = index;
      }

      pageContext.setAttribute("_CACHE_INDEX_", new Integer(i + 1));
      return new Key(name, i);
   }

   public static void main(String[] args) {
      String a = "<html></script><sel\"\"ec't><fieldset></html>";
      System.out.println(escape(a));
   }

   public static String escape(String s) {
      if (s == null) {
         return null;
      } else {
         int len = s.length();
         if (len == 0) {
            return s;
         } else {
            StringBuffer buf = null;
            char[] cb = s.toCharArray();

            for(int i = 0; i < len; ++i) {
               char ch = cb[i];
               if (ch != '\'' && ch != '\\') {
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
                  } else {
                     if (ch == CHAR_SCRIPT[0]) {
                        int j;
                        for(j = 0; j < CHAR_SCRIPT.length && i + j < len && cb[i + j] == CHAR_SCRIPT[j]; ++j) {
                        }

                        if (j >= CHAR_SCRIPT.length) {
                           if (buf == null) {
                              buf = new StringBuffer(s.substring(0, i));
                           }

                           buf.append('s').append('c').append('r');
                           buf.append('\'').append('+').append('\'');
                           buf.append('i').append('p').append('t');
                           i += j - 1;
                           continue;
                        }
                     }

                     if (buf != null) {
                        buf.append(ch);
                     }
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

   public static String removeWhiteSpaces(String s) {
      if (s == null) {
         return null;
      } else {
         int len = s.length();
         if (len == 0) {
            return s;
         } else {
            StringBuffer buf = new StringBuffer();
            char[] cb = s.toCharArray();

            for(int i = 0; i < cb.length; ++i) {
               char ch = cb[i];
               if (ch != '\n') {
                  buf.append(ch);
               } else {
                  char ch2 = ch;

                  int j;
                  for(j = i - 1; j >= 0; --j) {
                     ch2 = cb[j];
                     if (ch2 != ' ' && ch2 != '\t' && ch2 != '\n' && ch2 != '\r') {
                        ++j;
                        break;
                     }
                  }

                  int k = buf.length() - (i - j);
                  buf.setLength(k < 0 ? 0 : k);

                  while(i < len) {
                     ch = cb[i];
                     if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
                        --i;
                        break;
                     }

                     ++i;
                  }

                  if (ch != '<' && ch2 != '>' && buf.length() != 0) {
                     buf.append('\n');
                  }
               }
            }

            int i;
            for(i = buf.length() - 1; i >= 0; --i) {
               char ch = buf.charAt(i);
               if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r') {
                  ++i;
                  break;
               }
            }

            buf.setLength(i);
            return buf.toString();
         }
      }
   }

   public static class Key {
      String serverKey;
      JPrimitive clientKey;

      public Key(String serverKey, JPrimitive clientKey) {
         this.serverKey = serverKey;
         this.clientKey = clientKey;
      }

      public Key(String serverKey, int clientKey) {
         this.serverKey = serverKey;
         this.clientKey = new JPrimitive(clientKey);
      }

      public Key(String serverKey, String clientKey) {
         this.serverKey = serverKey;
         this.clientKey = new JPrimitive(clientKey);
      }

      public boolean equals(Object obj) {
         if (obj == null) {
            return false;
         } else if (this.getClass() != obj.getClass()) {
            return false;
         } else {
            Key other = (Key)obj;
            if (this.serverKey == null) {
               if (other.serverKey != null) {
                  return false;
               }
            } else if (!this.serverKey.equals(other.serverKey)) {
               return false;
            }

            return true;
         }
      }

      public int hashCode() {
         return this.serverKey.hashCode();
      }
   }
}
