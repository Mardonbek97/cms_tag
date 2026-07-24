package uz.fido_biznes.cms;

public class Main {
   public static void main(String[] args) {
      if ("mozilla/5.0 (windows nt 10.0; win64; x64) applewebkit/537.36 (khtml, like gecko) chrome/70.0.3538.77 safari/537.36".indexOf("msie") < 0) {
         System.out.println("CHROME");
      } else {
         System.out.println("IE");
      }

   }
}
