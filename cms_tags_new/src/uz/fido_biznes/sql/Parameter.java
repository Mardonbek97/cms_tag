package uz.fido_biznes.sql;

import java.sql.SQLException;
import java.util.Locale;
import oracle.jdbc.OracleCallableStatement;

public abstract class Parameter {
   protected String name;
   protected Object value;
   protected Direction direction;

   public Parameter() {
   }

   public Parameter(String name, Object value, Direction direction) {
      this.setName(name);
      this.value = value;
      this.direction = direction;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name.toLowerCase(Locale.ENGLISH);
   }

   public boolean equalName(String name) {
      return this.name.equals(name.toLowerCase(Locale.ENGLISH));
   }

   public boolean isInDirection() {
      return this.direction == Direction.IN || this.direction == Direction.IN_OUT;
   }

   public boolean isOutDirection() {
      return this.direction == Direction.OUT || this.direction == Direction.IN_OUT;
   }

   public boolean isInOutDirection() {
      return this.direction == Direction.IN_OUT;
   }

   public void setDirection(Direction direction) {
      this.direction = direction;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public Object getValue() {
      return this.value;
   }

   public void setValue(Object value) {
      this.value = value;
   }

   protected abstract void sendNull(StoredObject var1, OracleCallableStatement var2, int var3) throws SQLException;

   protected abstract void sendValue(StoredObject var1, OracleCallableStatement var2, int var3) throws SQLException;

   protected abstract void registerValue(StoredObject var1, OracleCallableStatement var2, int var3) throws SQLException;

   protected abstract void receiveValue(StoredObject var1, OracleCallableStatement var2, int var3) throws SQLException;

   public void bindValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      if (this.isInDirection()) {
         if (this.value == null) {
            this.sendNull(stored, cs, index);
         } else {
            this.sendValue(stored, cs, index);
         }
      }

      if (this.isOutDirection()) {
         this.registerValue(stored, cs, index);
      }

   }

   public void retrieveValue(StoredObject stored, OracleCallableStatement cs, int index) throws SQLException {
      if (this.isOutDirection()) {
         this.receiveValue(stored, cs, index);
      }

   }
}
