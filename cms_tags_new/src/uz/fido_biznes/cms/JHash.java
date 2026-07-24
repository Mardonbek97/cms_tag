package uz.fido_biznes.cms;

import java.util.Enumeration;
import java.util.Hashtable;

public final class JHash {
   private Hashtable values = new Hashtable();

   public JHash put(String key, int value) {
      this.values.put(key, new JPrimitive(value));
      return this;
   }

   public JHash put(String key, String value) {
      this.values.put(key, new JPrimitive(value));
      return this;
   }

   public JHash put(String key, JArray value) {
      this.values.put(key, value);
      return this;
   }

   public JHash put(String key, JHash value) {
      this.values.put(key, value);
      return this;
   }

   public JHash putScript(String key, String value) {
      this.values.put(key, value);
      return this;
   }

   public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("{");
      Enumeration e = this.values.keys();

      while(e.hasMoreElements()) {
         String key = (String)e.nextElement();
         buf.append(key);
         buf.append(":");
         buf.append(this.values.get(key).toString());
         if (e.hasMoreElements()) {
            buf.append(",");
         }
      }

      buf.append("}");
      return buf.toString();
   }

   public static String getJSON(String key, String value) {
      JHash tmp = new JHash();
      tmp.put(key, value);
      return tmp.toString();
   }
}
