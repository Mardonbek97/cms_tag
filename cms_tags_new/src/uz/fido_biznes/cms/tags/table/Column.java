package uz.fido_biznes.cms.tags.table;

import uz.fido_biznes.cms.JHash;

public class Column {
   private String ref;
   private int type;
   private String name;
   private String mask;
   private String enable;
   private String required;
   private String checked;
   private String size;
   private int align;
   private String readonly;
   private String label;
   /***Added for Dynamic Grid*****/
   private String rowspan;
   private String colspan;
   private String width;
   private String height;
   private String labelHeight;
   private boolean nowrap = false;

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

   public int getAlign() {
      return this.align;
   }

   public void setChecked(String checked) {
      this.checked = checked;
   }

   public void setEnable(String enable) {
      this.enable = enable;
   }

   public void setMask(String mask) {
      this.mask = mask;
   }

   public void setName(String name) {
      this.name = name;
   }

   public String getName() {
      return this.name;
   }

   public void setReadonly(String readonly) {
      this.readonly = readonly;
   }

   public void setRef(String ref) {
      this.ref = ref;
   }

   public String getRef() {
      return this.ref;
   }

   public void setRequired(String required) {
      this.required = required;
   }

   public void setSize(String size) {
      this.size = size;
   }

   /***Added for Dynamic Grid*****/
   public void setRowspan(String rowspan) {
      this.rowspan = rowspan;
   }

   public void setColspan(String colspan) {
      this.colspan = colspan;
   }

   public void setWidth(String width) {
      this.width = width;
   }

   public void setHeight(String height) {
      this.height = height;
   }

   public void setLabelHeight(String labelHeight) {
      this.labelHeight = labelHeight;
   }

   public void setType(String type) {
      if (type == null) {
         this.type = 0;
      } else if (type.equals("text")) {
         this.type = 1;
      } else if (type.equals("checkbox")) {
         this.type = 2;
      } else if (type.equals("hidden")) {
         this.type = 4;
      } else {
         this.type = 0;
      }

   }

   public int getType() {
      return this.type;
   }

   public void setNowrap(boolean nowrap) {
      this.nowrap = nowrap;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public String getLabel() {
      return this.label;
   }

   public String toString() {
      JHash result = new JHash();
      result.put("i", this.ref);
      if (this.type != 0) {
         result.put("t", this.type);
      }

      if (this.name != null) {
         result.put("n", this.name);
      }

      if (this.mask != null) {
         result.put("m", this.mask);
      }

      if (this.enable != null) {
         result.put("e", this.enable);
      }

      if (this.required != null) {
         result.put("r", this.required);
      }

      if (this.checked != null) {
         result.put("c", this.checked);
      }

      if (this.size != null) {
         result.put("x", this.size);
      }

      if (this.align != 1) {
         result.put("a", this.align);
      }

      if (this.readonly != null) {
         result.put("z", this.readonly);
      }

      if (this.nowrap) {
         result.put("w", 1);
      }

      if (this.label != null) {
         result.put("l", this.label);
      }

      /***Added for Dynamic Grid*****/
      if (this.rowspan != null) {
         result.put("rs", this.rowspan);
      }

      if (this.colspan != null) {
         result.put("cs", this.colspan);
      }

      if (this.width != null) {
         result.put("cw", this.width);
      }

      if (this.height != null) {
         result.put("ch", this.height);
      }

      if (this.labelHeight != null) {
         result.put("lh", this.labelHeight);
      }

      return result.toString();
   }
}
