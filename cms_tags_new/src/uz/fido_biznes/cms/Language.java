package uz.fido_biznes.cms;

public class Language {
   private Sentence[] sentences;
   int languageIndex = -1;

   private Language() {
   }

   public Language(int languageIndex, Sentence[] sentences) {
      this.languageIndex = languageIndex - 1;
      this.sentences = sentences;
   }

   private String getSentence(String[] args, int name) throws Exception {
      return this.sentences[name].getSentence(this.languageIndex, args);
   }

   public String get(int name) throws Exception {
      return this.getSentence((String[])null, name);
   }

   public String get(int name, String arg1) throws Exception {
      String[] v = new String[]{arg1};
      return this.getSentence(v, name);
   }

   public String get(int name, String arg1, String arg2) throws Exception {
      String[] v = new String[]{arg1, arg2};
      return this.getSentence(v, name);
   }

   public String get(int name, String arg1, String arg2, String arg3) throws Exception {
      String[] v = new String[]{arg1, arg2, arg3};
      return this.getSentence(v, name);
   }

   public String get(int name, String arg1, String arg2, String arg3, String arg4) throws Exception {
      String[] v = new String[]{arg1, arg2, arg3, arg4};
      return this.getSentence(v, name);
   }

   public String get(int name, String arg1, String arg2, String arg3, String arg4, String arg5) throws Exception {
      String[] v = new String[]{arg1, arg2, arg3, arg4, arg5};
      return this.getSentence(v, name);
   }

   public String get(int name, String arg1, String arg2, String arg3, String arg4, String arg5, String arg6) throws Exception {
      String[] v = new String[]{arg1, arg2, arg3, arg4, arg5, arg6};
      return this.getSentence(v, name);
   }
}
