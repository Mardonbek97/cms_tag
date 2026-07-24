package uz.fido_biznes.cms.tags.table;

public class FootCell {
   private String ref;
   private int type;
   private String rows;
   private String size;
   private int align;
   private String rowspan;
   private String colspan;
   private String label;
   private String name;

   public void setAlign(String align) {
      if (align == null) {
         this.align = 1;
      } else if (align.equals("left")) {
         this.align = 0;
      } else if (align.equals("right")) {
         this.align = 2;
      } else {
         this.align = 1;
      }

   }

   public void setColspan(String colspan) {
      this.colspan = colspan;
   }

   public void setRef(String ref) {
      this.ref = ref;
   }

   public void setRows(String rows) {
      this.rows = rows;
   }

   public void setRowspan(String rowspan) {
      this.rowspan = rowspan;
   }

   public void setSize(String size) {
      this.size = size;
   }

   public void setType(String type) {
      if (type == null) {
         this.type = 0;
      } else if (type.equals("textarea")) {
         this.type = 1;
      } else {
         this.type = 0;
      }

   }

   public String getRef() {
      return this.ref;
   }

   public int getType() {
      return this.type;
   }

   public String getRows() {
      return this.rows;
   }

   public String getSize() {
      return this.size;
   }

   public int getAlign() {
      return this.align;
   }

   public String getRowspan() {
      return this.rowspan;
   }

   public String getColspan() {
      return this.colspan;
   }

   public String getLabel() {
      return this.label;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String toString() {
      StringBuffer buf = new StringBuffer("{i:");
      buf.append(this.ref);
      if (this.type != 0) {
         buf.append(",t:").append(this.type);
      }

      if (this.rows != null) {
         buf.append(",y:").append(this.rows);
      }

      if (this.size != null) {
         buf.append(",x:").append('\'').append(this.size).append('\'');
      }

      if (this.align != 1) {
         buf.append(",a:").append(this.align);
      }

      if (this.rowspan != null) {
         buf.append(",r:").append(this.rowspan);
      }

      if (this.colspan != null) {
         buf.append(",c:").append(this.colspan);
      }

      buf.append('}');
      return buf.toString();
   }
}
