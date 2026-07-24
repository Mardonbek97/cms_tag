package uz.fido_biznes.sql;

import java.math.BigDecimal;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;
import oracle.sql.NUMBER;

public class ArrayNumberParameter extends NumberParameter {
   public ArrayNumberParameter() {
   }

   public ArrayNumberParameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      BigDecimal[] n = (BigDecimal[])cs.getARRAY(index).getArray();
      String[] v = new String[n.length];

      for(int i = 0; i < n.length; ++i) {
         v[i] = n[i].toString();
      }

      this.value = v;
   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, 2003, "ARRAY_NUMBER");
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNull(index, 2003, "ARRAY_NUMBER");
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      String[] v = (String[])this.value;
      NUMBER[] n = new NUMBER[v.length];

      for(int i = 0; i < v.length; ++i) {
         n[i] = this.convertToNumber(v[i]);
      }

      ARRAY array = new ARRAY(stored.getArrayNumberDescriptor(), stored.getConnection(), n);
      cs.setARRAY(index, array);
   }
}
