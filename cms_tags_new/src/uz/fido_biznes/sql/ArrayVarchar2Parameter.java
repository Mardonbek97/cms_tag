package uz.fido_biznes.sql;

import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.ARRAY;

public class ArrayVarchar2Parameter extends Varchar2Parameter {
   public ArrayVarchar2Parameter() {
   }

   public ArrayVarchar2Parameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      ARRAY r = cs.getARRAY(index);
      if (r != null) {
         this.value = cs.getARRAY(index).getArray();
      } else {
         this.value = null;
      }

   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, 2003, "ARRAY_VARCHAR2");
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNull(index, 2003, "ARRAY_VARCHAR2");
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      String[] s = (String[])this.value;
      Object[] obj = new Object[s.length];
      System.arraycopy(s, 0, obj, 0, s.length);
      ARRAY array = new ARRAY(stored.getArrayVarchar2Descriptor(), stored.getConnection(), obj);
      cs.setARRAY(index, array);
   }
}
