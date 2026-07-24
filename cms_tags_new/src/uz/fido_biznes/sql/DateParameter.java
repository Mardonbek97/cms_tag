package uz.fido_biznes.sql;

import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.DATE;
import uz.fido_biznes.cms.Util;

public class DateParameter extends Parameter {
   private String format;

   public DateParameter() {
   }

   public DateParameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   public DateParameter(String name, Object value, Direction direction, String format) {
      super(name, value, direction);
      this.format = format;
   }

   public void setFormat(String format) {
      this.format = format;
   }

   public String getFormat() {
      return Util.nvl(this.format, "dd.mm.yyyy");
   }

   public String getLang() {
      return null;
   }

   public DATE convertToDate(String value) throws SQLException {
      if (value != null && value.length() != 0) {
         DATE date = null;

         try {
            date = DATE.fromText(value, this.getFormat(), this.getLang());
            return date;
         } catch (Exception var4) {
            throw new SQLException("Cannot convert to date\nparameter=" + this.name + "\nvalue=" + value);
         }
      } else {
         return null;
      }
   }

   public String convertToString(DATE value) throws SQLException {
      return value.toText(this.getFormat(), this.getLang());
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      this.value = this.convertToString(cs.getDATE(index));
   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, 91);
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNull(index, 91);
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setDATE(index, this.convertToDate((String)this.value));
   }
}
