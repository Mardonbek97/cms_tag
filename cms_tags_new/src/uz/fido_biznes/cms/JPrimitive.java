package uz.fido_biznes.cms;

public final class JPrimitive {
   private String value;

   public JPrimitive(int value) {
      this.value = String.valueOf(value);
   }

   public JPrimitive(String value) {
      this.value = "'" + Util.quotesEsc(value) + "'";
   }

   public String toString() {
      return this.value;
   }

   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         JPrimitive other = (JPrimitive)obj;
         if (this.value == null) {
            if (other.value != null) {
               return false;
            }
         } else if (!this.value.equals(other.value)) {
            return false;
         }

         return true;
      }
   }

   public int hashCode() {
      int hash = 3;
      hash = 97 * hash + (this.value != null ? this.value.hashCode() : 0);
      return hash;
   }
}
