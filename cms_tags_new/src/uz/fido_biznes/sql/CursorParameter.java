package uz.fido_biznes.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;

public class CursorParameter extends Parameter {
   public CursorParameter() {
   }

   public CursorParameter(String name) {
      super(name, (Object)null, Direction.OUT);
   }

   public CursorParameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      ResultSet rs = cs.getCursor(index);
      rs.afterLast();
      rs.beforeFirst();
      this.value = rs;
   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, -10);
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      throw new UnsupportedOperationException("Not supported yet.");
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      throw new UnsupportedOperationException("Not supported yet.");
   }
}
