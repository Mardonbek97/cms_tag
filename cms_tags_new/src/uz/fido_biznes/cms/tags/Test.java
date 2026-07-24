package uz.fido_biznes.cms.tags;

import java.io.UnsupportedEncodingException;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.driver.OracleDriver;
import uz.fido_biznes.cms.ServletCallableStatement;
import uz.fido_biznes.cms.Util;
import uz.fido_biznes.sql.StoredObject;

public class Test {
   static Map<String, String> map = new HashMap();

   public static void main(String[] args) throws UnsupportedEncodingException {
      String a = "     0  : Successful\n-7011 : internal error (Sql exceptions can be)\n-7012 : Illegal State Exception and Constraint Violation Exception\n-7013 : Http Message Conversion Exception (to handle exception when tyring to pass wrong Sex Enum value) \n-7014 : Illegal Argument Exception \n-7015 : handle Type Mismatch Exception \n-7016 : handle Http Message Not Readable \n-7017 : handle Missing Servlet Request Parameter(Error while converting object to string),handleMissingServletRequestPart\n-7018 : handle Method Argument Not Valid\n-7019 : handle Missing Servlet RequestPart\n-70110 : the following request with id has already been registered\n-70111 : The following method in this version is not registered!\n-70112 : the following request is on process, please check its result later with a help of checkRequestInDb method\n-70113 : Error while receiving -4061 error code from db in second time,which means invalid package\n-70114 : Json Validation error which will be occurred while validating json according to json-schema\n-70115 : Error (perform) while converting String Array to Array_Varchar2 \n-70116 : ERROR while converting json string into JsonNode\n-70117 : ERROR(NetException) Время запроса превысело установленный лимит, повторно отправьте запрос\n-70118 : ERROR(NetException) while executing perform\n-70118 : ERROR(SQLRecoverableException) while executing perform\n-70120 : ERROR(SQLException) while executing perform\n-70121 : ERROR[DaoRunnable.run] most probably it will be occurred while closing connection,SQL Exception\n-70122 : this request is still on process, Please check again after few minutes\n-70123 : ERROR[checkRequest] SQLException | InterruptedException\n-70124 : Page number cannot be greater than max page     \n \n  ";
      long testStartTime = System.nanoTime();
      String replace = a.replace(":", "+");
      long trimTestTime = System.nanoTime() - testStartTime;
      System.out.println(trimTestTime);
      System.out.println(replace);
      long testStartTime3 = System.nanoTime();
      String s2 = Util.replace(a, ":", "+");
      long trimTestTime3 = System.nanoTime() - testStartTime3;
      System.out.println(trimTestTime3);
      System.out.println(s2);
      long testStartTime4 = System.nanoTime();
      String s4 = Util.myReplace(a, ":", "+");
      long trimTestTime4 = System.nanoTime() - testStartTime4;
      System.out.println(trimTestTime4);
      System.out.println(s4);
   }

   private static void testOra() {
      try {
         DriverManager.registerDriver(new OracleDriver());
         OracleConnection conn = (OracleConnection)DriverManager.getConnection("jdbc:oracle:thin:@//10.50.50.180:1521/iabs7", "iabs", "iabs");
         StoredObject stored = new StoredObject();
         stored.setConnection(conn);
         ServletCallableStatement cs = new ServletCallableStatement(stored);
         cs.setProcedure("Mdm_Api.Get_Message");
         cs.execute();
         if (conn != null) {
            conn.close();
         }
      } catch (Exception ex) {
         System.out.println(ex.getMessage());
         System.out.println("------------------------------");
         System.out.println(Util.getUserMessage(ex));
         System.out.println(ex.toString());
      }

   }

   private static String removeORANEW(String message) {
      String msg = message;
      message = "";

      while(msg.indexOf("ORA-") > -1) {
         int y = msg.lastIndexOf("ORA-");
         if (y > -1) {
            msg.substring(y, y + 10);
            String txy = msg.substring(y + 11).concat("\n");
            msg = msg.substring(0, y);
            message = message.concat(txy);
         } else {
            message = message.concat(msg);
         }
      }

      return message;
   }
}
