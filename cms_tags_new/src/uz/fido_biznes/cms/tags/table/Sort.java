package uz.fido_biznes.cms.tags.table;

public class Sort {
   private String fieldId;
   private String fieldName;
   private Direction direction;

   public Sort(String fieldId, String fieldName, String direction) {
      this.fieldId = fieldId;
      this.fieldName = fieldName;
      this.direction = Direction.fromString(direction);
   }

   public String getFieldId() {
      return this.fieldId;
   }

   public void setFieldId(String fieldId) {
      this.fieldId = fieldId;
   }

   public Direction getDirection() {
      return this.direction;
   }

   public void setDirection(String direction) {
      this.direction = Direction.fromString(direction);
   }

   public String getFieldName() {
      return this.fieldName;
   }

   public void setFieldName(String fieldName) {
      this.fieldName = fieldName;
   }

   public String toString() {
      return "[" + this.fieldId + ",'" + this.direction + "']";
   }

   public static enum Direction {
      ASC,
      DESC;

      public static Direction fromString(String direction) {
         try {
            return direction == null ? ASC : valueOf(direction.toUpperCase());
         } catch (Exception e) {
            throw new IllegalArgumentException(String.format("Недопустимое значение %s для сортировок, Должно быть либо ASC либо DESC", direction), e);
         }
      }
   }
}
