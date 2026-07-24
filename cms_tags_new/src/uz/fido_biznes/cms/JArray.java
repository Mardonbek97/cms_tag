package uz.fido_biznes.cms;

import java.util.Vector;

public final class JArray {
   private Vector values = new Vector();

   public JArray push(int value) {
      this.values.addElement(new JPrimitive(value));
      return this;
   }

   public JArray push(String value) {
      this.values.addElement(new JPrimitive(value));
      return this;
   }

   public JArray push(JArray value) {
      this.values.addElement(value);
      return this;
   }

   public JArray push(JHash value) {
      this.values.addElement(value);
      return this;
   }

   public JArray pushScript(String value) {
      this.values.addElement(value);
      return this;
   }

   public String toString() {
      StringBuffer buf = new StringBuffer();
      int len = this.values.size();
      buf.append("[");

      for(int i = 0; i < len; ++i) {
         buf.append(this.values.elementAt(i).toString());
         if (i + 1 != len) {
            buf.append(",");
         }
      }

      buf.append("]");
      return buf.toString();
   }

   public static String getJSON(String v1) {
      JArray tmp = new JArray();
      tmp.push(v1);
      return tmp.toString();
   }

   public static String getJSON(String v1, String v2) {
      JArray tmp = new JArray();
      tmp.push(v1);
      tmp.push(v2);
      return tmp.toString();
   }

   public static String getJSON(String v1, String v2, String v3) {
      JArray tmp = new JArray();
      tmp.push(v1);
      tmp.push(v2);
      tmp.push(v3);
      return tmp.toString();
   }
}
