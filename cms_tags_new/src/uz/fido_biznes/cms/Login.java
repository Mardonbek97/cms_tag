package uz.fido_biznes.cms;

import java.util.HashMap;

public class Login {
   private static final String version = "2.2.15";
   private static HashMap LoginList = new HashMap();
   private static boolean isOracleRac = true;
   private String userLogin;

   public void setUserLogin(String userLogin) {
      this.userLogin = userLogin;
   }

   public static boolean isOracleRac() {
      return isOracleRac;
   }

   public static String getVersion() {
      return "2.2.15";
   }

   public void setOracleRacData(String isOracleRac, String[] logins, String[] racNumbers) {
      synchronized(LoginList) {
         if ("Y".equals(isOracleRac)) {
            Login.isOracleRac = true;
         } else {
            Login.isOracleRac = false;
         }

         if (logins != null && racNumbers != null) {
            for(int i = 0; i < logins.length; ++i) {
               LoginList.put(logins[i], racNumbers[i]);
            }
         }

      }
   }

   public static String getRacNumber(String userLogin) {
      return (String)LoginList.get(userLogin);
   }

   public static boolean isLoginRegistered(String userLogin) {
      return getRacNumber(userLogin) != null;
   }

   public String LoginListString() {
      return LoginList.toString();
   }

   public static void main(String[] args) {
      String[] users = null;
      String[] racs = null;
      String userLogin = "lasso";
      Login login = new Login();
      login.setUserLogin(userLogin);
      login.setOracleRacData("N", users, racs);
      System.out.println(LoginList.toString());
      System.out.println(isOracleRac());
   }
}
