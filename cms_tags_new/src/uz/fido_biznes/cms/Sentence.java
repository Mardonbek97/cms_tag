package uz.fido_biznes.cms;

import java.util.Vector;

public class Sentence {
   public static final int LANG_RUSSIAN = 0;
   public static final int LANG_UZBEK_CYR = 1;
   public static final int LANG_UZBEK_LATIN = 2;
   public static final int LANG_ENGLISH = 3;
   public static final int LANG_KARAKALPAK = 4;
   public static final int LANG_TAJIK = 5;
   private Exception exception = null;
   private Object[][] sentences;

   private Sentence() {
   }

   public Sentence(String v1) {
      String[] st = new String[]{v1};
      this.setSentences(st);
   }

   public Sentence(String v1, String v2) {
      String[] st = new String[]{v1, v2};
      this.setSentences(st);
   }

   public Sentence(String v1, String v2, String v3) {
      String[] st = new String[]{v1, v2, v3};
      this.setSentences(st);
   }

   public Sentence(String v1, String v2, String v3, String v4) {
      String[] st = new String[]{v1, v2, v3, v4};
      this.setSentences(st);
   }

   public Sentence(String v1, String v2, String v3, String v4, String v5) {
      String[] st = new String[]{v1, v2, v3, v4, v5};
      this.setSentences(st);
   }

   public Sentence(String v1, String v2, String v3, String v4, String v5, String v6) {
      String[] st = new String[]{v1, v2, v3, v4, v5, v6};
      this.setSentences(st);
   }

   public String getSentence(int langIndex, String[] args) throws Exception {
      if (this.exception != null) {
         throw this.exception;
      } else {
         if (langIndex >= this.sentences.length) {
            langIndex = 0;
         }

         Object[] ret = this.sentences[langIndex];
         if (ret.length == 0) {
            ret = this.sentences[0];
         }

         if (ret.length == 1) {
            return (String)ret[0];
         } else if (args == null) {
            throw new RuntimeException("Аргумент не передан");
         } else {
            StringBuffer buf = new StringBuffer();

            for(int i = 0; i < ret.length; ++i) {
               if (ret[i] instanceof Integer) {
                  buf.append(args[(Integer)ret[i]]);
               } else {
                  buf.append((String)ret[i]);
               }
            }

            return buf.toString();
         }
      }
   }

   private void setSentences(String[] st) {
      try {
         if (st == null) {
            throw new RuntimeException("Passed argument is null");
         }

         this.sentences = new Object[st.length][];

         for(int i = 0; i < st.length; ++i) {
            this.sentences[i] = this.parseSentence(st[i]);
         }
      } catch (Exception ex) {
         if (st == null) {
            this.exception = ex;
         } else {
            this.exception = new RuntimeException(ex.getMessage() + " ->" + st[0]);
         }
      }

   }

   private Object[] parseSentence(String v) throws Exception {
      int len = v.length();
      int start = 0;
      Vector term = new Vector();

      for(int i = 0; i < len; ++i) {
         if (v.charAt(i) == '$') {
            int j;
            for(j = i + 1; j < len && '0' <= v.charAt(j) && v.charAt(j) <= '9'; ++j) {
            }

            if (j - i > 1) {
               if (start < i) {
                  term.addElement(v.substring(start, i));
               }

               term.addElement(new Integer(Integer.parseInt(v.substring(i + 1, j)) - 1));
               start = j;
            }
         }
      }

      if (start < len) {
         term.addElement(v.substring(start, len));
      }

      return term.toArray();
   }

   public static void main(String[] args) throws Exception {
      Sentence s = new Sentence("Лицевая карточка юридического лица", "", "");
      System.out.println(s.getSentence(1, (String[])null));
   }
}
