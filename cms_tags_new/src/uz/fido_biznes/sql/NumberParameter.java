package uz.fido_biznes.sql;

import java.math.BigDecimal;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.NUMBER;

public class NumberParameter extends Parameter {
   public NumberParameter() {
   }

   public NumberParameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   public NUMBER convertToNumber(String value) throws SQLException {
      if (value != null && value.length() != 0) {
         NUMBER num = null;

         try {
            num = new NUMBER(new BigDecimal(value));
            return num;
         } catch (Exception var4) {
            throw new SQLException("Cannot convert to number\nparameter=" + this.name + "\nvalue=" + value);
         }
      } else {
         return null;
      }
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      this.value = cs.getBigDecimal(index).toString();
   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, 2);
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNull(index, 2);
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNUMBER(index, this.convertToNumber((String)this.value));
   }
}
