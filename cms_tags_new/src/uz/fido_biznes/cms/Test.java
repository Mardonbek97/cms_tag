package uz.fido_biznes.cms;

import java.util.Vector;
import uz.fido_biznes.sql.StoredObject;

public class Test {
   private static Sentence[] sentences;
   private static Vector vSent;

   private static int SI(Sentence sentence) {
      if (vSent == null) {
         vSent = new Vector(100);
      }

      vSent.addElement(sentence);
      return vSent.size() - 1;
   }

   private static int SI(String st1) {
      return SI(new Sentence(st1));
   }

   private static int SI(String st1, String st2) {
      return SI(new Sentence(st1, st2));
   }

   private static int SI(String st1, String st2, String st3) {
      return SI(new Sentence(st1, st2, st3));
   }

   private static int SI(String st1, String st2, String st3, String st4) {
      return SI(new Sentence(st1, st2, st3, st4));
   }

   private static int SI(String st1, String st2, String st3, String st4, String st5) {
      return SI(new Sentence(st1, st2, st3, st4, st5));
   }

   private static int SI(String st1, String st2, String st3, String st4, String st5, String st6) {
      return SI(new Sentence(st1, st2, st3, st4, st5, st6));
   }

   public static void main(String[] args) {
      String s1 = Util.quotesSQL("select name from v_client_current_h where name = 'o''zbek'");
      new StoredObject();
      String s2 = StoredObject.quotesSQL("select name from v_client_current_h where name = ''o''zbek''");
      s2.substring(2, s2.length() - 2);
      int si_title = SI("������", "", "", "");
      int si_search = SI("����� $1 hello $2 : ", "����� $1 hello $2", "Izlash $1 hello $2", "Search $1 hello $2");

      try {
         if (vSent != null) {
            sentences = new Sentence[vSent.size()];

            for(int i = 0; i < vSent.size(); ++i) {
               sentences[i] = (Sentence)vSent.elementAt(i);
            }

            vSent = null;
         }

         Language lang = new Language(6, sentences);
         String s = lang.get(si_search, "Abbosbek");
         System.out.println(s);
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   static {
      if (vSent != null) {
         sentences = new Sentence[vSent.size()];

         for(int i = 0; i < vSent.size(); ++i) {
            sentences[i] = (Sentence)vSent.elementAt(i);
         }

         vSent = null;
      }

   }
}
