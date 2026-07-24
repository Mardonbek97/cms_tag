package uz.fido_biznes.sql;

import java.sql.SQLException;
import java.sql.Timestamp;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;
import oracle.sql.DATE;

public class ArrayDateParameter extends DateParameter {
   public ArrayDateParameter() {
   }

   public ArrayDateParameter(String name, Object value, Direction direction, String format) {
      super(name, value, direction, format);
   }

   public ArrayDateParameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      Timestamp[] d = (Timestamp[])cs.getARRAY(index).getArray();
      String[] v = new String[d.length];

      for(int i = 0; i < d.length; ++i) {
         v[i] = d[i].toString();
      }

      this.value = v;
   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, 2003, "ARRAY_DATE");
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNull(index, 2003, "ARRAY_DATE");
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      String[] v = (String[])this.value;
      DATE[] d = new DATE[v.length];

      for(int i = 0; i < v.length; ++i) {
         d[i] = this.convertToDate(v[i]);
      }

      ARRAY array = new ARRAY(stored.getArrayDateDescriptor(), stored.getConnection(), d);
      cs.setARRAY(index, array);
   }
}
