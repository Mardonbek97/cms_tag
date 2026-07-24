package uz.fido_biznes.cms.tags.table;

import java.util.TreeMap;
import java.util.Vector;
import uz.fido_biznes.cms.Util;

public class Value {
   private Vector fields;
   private Vector columns;
   private int recordsPerPage;
   private int pageNumber;
   private boolean resetCursor;
   private boolean hasFilter;
   private boolean selectedByFilter;
   private String gridDefinition;
   private String from;
   private String where;
   private String function;
   private Vector sorts;
   private Vector feet;
   private int langIndex;
   private int autoRefersh;

   public Value() {
      this.fields = new Vector();
   }

   public Value(int langIndex) {
      this.langIndex = langIndex;
      this.fields = new Vector();
   }

   public Value(int langIndex, int autoRefresh) {
      this.langIndex = langIndex;
      if (autoRefresh >= 0 && autoRefresh < 5000) {
         this.autoRefersh = 5000;
      } else {
         this.autoRefersh = autoRefresh;
      }

      this.fields = new Vector();
   }

   public void addField(Field field) {
      this.fields.addElement(field);
   }

   public Vector getFields() {
      return this.fields;
   }

   public Vector getColumns() {
      return this.columns;
   }

   public void setColumns(Vector columns) {
      this.columns = columns;
   }

   public Vector getFeet() {
      return this.feet;
   }

   public void setFeet(Vector feet) {
      this.feet = feet;
   }

   public void setPageNubmer(String pageNumber) {
      if (pageNumber == null) {
         if (this.resetCursor) {
            this.pageNumber = 1;
         }

      } else {
         try {
            this.pageNumber = Integer.parseInt(pageNumber);
            if (this.pageNumber <= 0) {
               this.pageNumber = 1;
            }
         } catch (Exception var3) {
            this.pageNumber = 1;
         }

      }
   }

   public void setPageNumber(int pageNumber) {
      this.pageNumber = pageNumber;
   }

   public int getPageNumber() {
      return this.pageNumber;
   }

   public int getRecordsPerPage() {
      return this.recordsPerPage;
   }

   public int getFilterDefinition() {
      int k = 0;
      if (this.hasFilter) {
         ++k;
      }

      if (this.selectedByFilter) {
         k += 2;
      }

      return k;
   }

   public void setRecordsPerPage(String recordsPerPage) {
      if (recordsPerPage != null) {
         try {
            this.recordsPerPage = Integer.parseInt(recordsPerPage);
            if (this.recordsPerPage < 0) {
               this.recordsPerPage = 20;
            }
         } catch (Exception var3) {
            this.recordsPerPage = 20;
         }

      }
   }

   public void clearFilter() {
      int len = this.fields.size();

      for(int i = 0; i < len; ++i) {
         Field field = (Field)this.fields.elementAt(i);
         field.clearFilterValues();
      }

   }

   public void clearSort() {
      this.sorts = null;
   }

   public void setGridDefinition(String gridDefinition) {
      this.gridDefinition = gridDefinition;
   }

   public String getTableDefinition() throws Exception {
      StringBuffer sb = new StringBuffer("<script>tdf={");
      if (this.fields.size() == 0) {
         throw new RuntimeException("Table tag must contain at least one field tag");
      } else {
         sb.append("lang:").append(this.langIndex).append(",");
         String sum = this.getSumFieldLabels();
         if (sum != null) {
            sb.append("sum:").append(sum).append(",");
         }

         if (this.autoRefersh > 0) {
            sb.append("ar:").append(this.autoRefersh).append(",");
         }

         sb.append("h:");
         sb.append(this.fields.toString());
         if (this.gridDefinition == null) {
            throw new RuntimeException("Table tag must contain Grid definition tag");
         } else {
            sb.append(this.gridDefinition);
            sb.append("}</script>");
            return sb.toString();
         }
      }
   }

   public String getSQLFields() {
      StringBuffer buf = new StringBuffer("'['");
      int len = this.fields.size();

      for(int i = 0; i < len; ++i) {
         Field field = (Field)this.fields.elementAt(i);
         buf.append("||");
         buf.append(field.getSQLFormat());
         buf.append("||");
         if (i + 1 < len) {
            buf.append("','");
         }
      }

      buf.append("']'");
      return buf.toString();
   }

   public String getSQLSumFields() {
      StringBuffer buf = new StringBuffer("'['");
      boolean hasSum = false;
      int len = this.fields.size();

      for(int i = 0; i < len; ++i) {
         Field field = (Field)this.fields.elementAt(i);
         String sumFormat = field.getSumFormat();
         if (sumFormat != null) {
            if (hasSum) {
               buf.append("','");
            }

            hasSum = true;
            buf.append("||");
            buf.append(sumFormat);
            buf.append("||");
         }
      }

      buf.append("']'");
      if (hasSum) {
         return buf.toString();
      } else {
         return null;
      }
   }

   public String getSumFieldLabels() {
      StringBuffer buf = new StringBuffer("[");
      boolean hasSum = false;
      int len = this.fields.size();

      for(int i = 0; i < len; ++i) {
         Field field = (Field)this.fields.elementAt(i);
         String sumLabel = field.getSumLabel();
         if (sumLabel != null) {
            if (hasSum) {
               buf.append(",");
            }

            hasSum = true;
            buf.append("'");
            buf.append(Util.quotesEsc(sumLabel));
            buf.append("'");
         }
      }

      buf.append("]");
      if (hasSum) {
         return buf.toString();
      } else {
         return null;
      }
   }

   public String getSQLFilter() {
      StringBuffer buf = null;
      int len = this.fields.size();
      this.hasFilter = false;

      for(int i = 0; i < len; ++i) {
         Field field = (Field)this.fields.elementAt(i);
         if (field.getFilterType() > 0) {
            this.hasFilter = true;
            String filterSQL = field.getFilter();
            if (filterSQL != null) {
               if (buf == null) {
                  buf = new StringBuffer();
               } else {
                  buf.append(" and ");
               }

               buf.append(filterSQL);
            }
         }
      }

      if (buf == null) {
         this.selectedByFilter = false;
         return null;
      } else {
         this.selectedByFilter = true;
         return buf.toString();
      }
   }

   public String getSQLOrderByClause() {
      if (this.sorts == null) {
         return "";
      } else {
         StringBuffer buf = new StringBuffer(" order by");

         for(int i = 0; i < this.sorts.size(); ++i) {
            Sort sort = (Sort)this.sorts.elementAt(i);
            if (i == 0) {
               buf.append(" ");
            } else {
               buf.append(", ");
            }

            buf.append(sort.getFieldName());
            if (sort.getDirection() != null && !sort.equals("")) {
               buf.append(" ");
               buf.append(sort.getDirection());
            }
         }

         return buf.toString();
      }
   }

   public String getFilterValues() {
      StringBuffer buf = null;
      int len = this.fields.size();

      for(int i = 0; i < len; ++i) {
         Field field = (Field)this.fields.elementAt(i);
         if (field.getFilterType() > 0) {
            String value = field.getFilterValues();
            if (value != null) {
               if (buf == null) {
                  buf = new StringBuffer("w:{");
               } else {
                  buf.append(",");
               }

               buf.append(value);
            }
         }
      }

      if (buf == null) {
         return null;
      } else {
         return buf.append("}").toString();
      }
   }

   public String getSortValues() {
      return this.sorts == null ? null : "s:" + this.sorts.toString();
   }

   public void setSorts(TreeMap sortsMap) {
      if (sortsMap != null) {
         this.sorts = new Vector();

         for(Object sort : sortsMap.values()) {
            this.sorts.addElement(sort);
         }

      }
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public String getFrom() {
      return this.from;
   }

   public void setWhere(String where) {
      this.where = where;
   }

   public String getWhere() {
      return this.where;
   }

   public boolean isResetCursor() {
      return this.resetCursor;
   }

   public void setResetCursor(boolean resetCursor) {
      this.resetCursor = resetCursor;
   }

   public String getFunction() {
      return this.function;
   }

   public void setFunction(String function) {
      this.function = function;
   }
}
