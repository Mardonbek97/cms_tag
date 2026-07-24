package uz.fido_biznes.sql;

import java.sql.Blob;
import java.sql.SQLException;
import oracle.jdbc.OracleCallableStatement;

public class BlobParameter extends Parameter {
   public BlobParameter() {
   }

   public BlobParameter(String name, Object value, Direction direction) {
      super(name, value, direction);
   }

   protected void sendNull(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setNull(index, 2004);
   }

   protected void sendValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.setBlob(index, (Blob)this.value);
   }

   protected void registerValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      cs.registerOutParameter(index, 2004);
   }

   protected void receiveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      this.value = cs.getBlob(index);
   }
}
