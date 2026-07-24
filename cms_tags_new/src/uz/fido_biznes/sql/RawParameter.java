package uz.fido_biznes.sql;

import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;
import oracle.sql.RAW;

public class RawParameter extends Parameter {
   public RawParameter() {
   }

   public RawParameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNull(index, -2);
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setRAW(index, new RAW((byte[])this.value));
   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, -2);
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      RAW raw = cs.getRAW(index);
      this.value = raw.getBytes();
   }
}
