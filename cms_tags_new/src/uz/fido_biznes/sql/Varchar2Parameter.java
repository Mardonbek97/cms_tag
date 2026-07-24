package uz.fido_biznes.sql;

import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;

public class Varchar2Parameter extends Parameter {
   public Varchar2Parameter() {
   }

   public Varchar2Parameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      this.value = cs.getString(index);
   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, 12);
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNull(index, 12);
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setString(index, (String)this.value);
   }
}
