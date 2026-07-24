package uz.fido_biznes.cms.tags.table;

import uz.fido_biznes.cms.JHash;
import uz.fido_biznes.cms.Util;

public class Field {
   private String id;
   private String name;
   private String label;
   private int type;
   private String numberScaleFormat = "";
   private int filterType = 0;
   private String filterFreeFormat;
   private String filterOperator;
   private String filterLabel;
   private String filterOption;
   private String filterMask;
   private String filterSize;
   private String filterInGrid;
   private String filterValue;
   private String filterValue2;
   private String filterReferenceName;
   private String filterRequestName;
   private String filterReferenceURL;
   private String filterRequestURL;
   private String color;
   private String bgColor;
   private boolean isSumTegExist;
   private String sumLabel;
   private int sumType;
   private String encrypted;
   private String entityName;
   /***Added for Dynamic Grid*****/
   private String secureUser;
   public static final int FIELD_TYPE_TEXT = 0;
   public static final int FIELD_TYPE_QUOTE = 1;
   public static final int FIELD_TYPE_DATE = 2;
   public static final int FIELD_TYPE_NUMBER = 3;
   public static final int FIELD_TYPE_SUM = 4;
   public static final int FIELD_TYPE_SUM2 = 5;
   public static final int FIELD_TYPE_DATETIME = 6;
   public static final int FIELD_TYPE_TIME = 7;
   public static final int FIELD_TYPE_NOTSEND = 8;
   public static final int FIELD_TYPE_FREE_FORMAT = 9;
   public static final int FIELD_TYPE_CARD_NUMBER = 10;
   public static final int FILTER_TYPE_SINGLE = 1;
   public static final int FILTER_TYPE_RANGE = 2;
   public static final int FILTER_TYPE_CHOICE = 3;
   public static final int FILTER_TYPE_MULTISELECT = 4;

   public String getEntityName() {
      return this.entityName;
   }

   public void setEntityName(String entityName) {
      this.entityName = entityName;
   }

   public void setEncrypted(String encrypted) {
      this.encrypted = encrypted;
   }

   /***Added for Dynamic Grid*****/
   public void setSecureUser(String secureUser) {
      this.secureUser = secureUser;
   }

   public String getEncrypted() {
      return this.encrypted;
   }

   public int getSumType() {
      return this.sumType;
   }

   public void setSumType(int sumType) {
      this.sumType = sumType;
   }

   public boolean isSumTegExist() {
      return this.isSumTegExist;
   }

   public void setSumTegExist(boolean isSumTegExist) {
      this.isSumTegExist = isSumTegExist;
   }

   public String getSumLabel() {
      if (this.isSumTegExist) {
         return this.sumLabel == null ? this.label : this.sumLabel;
      } else {
         return null;
      }
   }

   public void setSumLabel(String sumLabel) {
      this.sumLabel = sumLabel;
   }

   public void setSumType(String sumType) {
      this.sumType = this.getTypeIndex(sumType);
   }

   public String getId() {
      return this.id;
   }

   public int getFilterType() {
      return this.filterType;
   }

   public void setFilterType(int filterType) {
      this.filterType = filterType;
   }

   public void setFilterInGrid(String filterInGrid) {
      this.filterInGrid = filterInGrid;
   }

   public void setFilterLabel(String filterLabel) {
      this.filterLabel = filterLabel;
   }

   public void setFilterOperator(String filterOperator) {
      if (filterOperator != null) {
         this.filterOperator = filterOperator;
      }

   }

   public void setFilterOption(String filterOption) {
      this.filterOption = filterOption;
   }

   public void setFilterMask(String filterMask) {
      this.filterMask = filterMask;
   }

   public void setFilterSize(String filterSize) {
      this.filterSize = filterSize;
   }

   public void setFilterValue(String filterValue) {
      if (filterValue != null && !filterValue.equals("")) {
         this.filterValue = filterValue;
      } else {
         this.filterValue = null;
      }

   }

   public void setFilterValue2(String filterValue2) {
      if (filterValue2 != null && !filterValue2.equals("")) {
         this.filterValue2 = filterValue2;
      } else {
         this.filterValue2 = null;
      }

   }

   public void setId(String id) {
      this.id = id;
   }

   public void setLabel(String label) {
      this.label = label;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setType(String type) {
      this.filterFreeFormat = type;
      this.type = this.getTypeIndex(type);
   }

   public int getType() {
      return this.type;
   }

   public void setFilterReferenceName(String filterReferenceName) {
      this.filterReferenceName = filterReferenceName;
   }

   public void setFilterRequestName(String filterRequestName) {
      this.filterRequestName = filterRequestName;
   }

   public void setFilterReferenceURL(String filterReferenceURL) {
      this.filterReferenceURL = filterReferenceURL;
   }

   public void setFilterRequestURL(String filterRequestURL) {
      this.filterRequestURL = filterRequestURL;
   }

   public void setColor(String color) {
      this.color = color;
   }

   public void setBgColor(String bgColor) {
      this.bgColor = bgColor;
   }

   private int getTypeIndex(String v) {
      if (v == null) {
         return 0;
      } else if (v.equals("quote")) {
         return 1;
      } else if (v.equals("date")) {
         return 2;
      } else if (v.equals("sum")) {
         return 4;
      } else if (v.length() >= 6 && v.substring(0, 6).equals("number")) {
         if (v.length() > 6) {
            int scale = 0;

            try {
               scale = Integer.parseInt(v.substring(7));
            } catch (NumberFormatException var5) {
               throw new NumberFormatException(v);
            }

            if (scale > 10) {
               throw new RuntimeException("Scale is bigger than 10 in type " + v);
            }

            char[] tmp = new char[scale + 1];
            tmp[0] = '.';

            for(int i = 1; i <= scale; ++i) {
               tmp[i] = '0';
            }

            this.numberScaleFormat = new String(tmp, 0, tmp.length);
         }

         return 3;
      } else if (v.equals("sum2")) {
         return 5;
      } else if (v.equals("datetime")) {
         return 6;
      } else if (v.equals("time")) {
         return 7;
      } else if (v.equals("card_number")) {
         return 10;
      } else {
         return v.equals("notSend") ? 8 : 9;
      }
   }

   public String getSumFormat() {
      if (this.isSumTegExist) {
         switch (this.sumType) {
            case 3:
               return "''''||to_char(nvl(sum(" + this.name + "),0),'FM999,999,999,999,999,990" + this.numberScaleFormat + "')||''''";
            case 4:
               return "''''||to_char(nvl(sum(" + this.name + "),0),'FM999,999,999,999,999,990.00')||''''";
            case 5:
               return "''''||to_char(nvl(sum(" + this.name + "),0),'FM999G999G999G999G999G990D00')||''''";
            default:
               return "nvl(sum(" + this.name + "),0)";
         }
      } else {
         return null;
      }
   }

   /***Added for Dynamic Grid*****/
   private String encryptSql(String expression) {
      return "Sql_Util.Encrypt_Field(" + expression + ",'" + this.sqlLiteral(this.entityName) + "','" + this.sqlLiteral(this.secureUser) + "','" + this.sqlLiteral(this.name) + "')";
   }

   private String sqlLiteral(String value) {
      return value == null ? "" : Util.quotesSQL(value);
   }

   public String getSQLFormat() {
      if ("Y".equals(this.encrypted)) {
         switch (this.type) {
            case 1:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("SQL_Util.QuotesEsc2(" + this.name + ")") + "||''''";
            case 2:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("to_char(" + this.name + ",'dd.mm.yyyy')") + "||''''";
            case 3:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("to_char(" + this.name + ",'FM999,999,999,999,999,990" + this.numberScaleFormat + "')") + "||''''";
            case 4:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("to_char(" + this.name + ",'FM999,999,999,999,999,990.00')") + "||''''";
            case 5:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("to_char(" + this.name + ",'FM999G999G999G999G999G990D00')") + "||''''";
            case 6:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("to_char(" + this.name + ",'dd.mm.yyyy hh24:mi:ss')") + "||''''";
            case 7:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("to_char(" + this.name + ", 'hh24:mi:ss')") + "||''''";
            case 8:
               return "''";
            case 9:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("to_char(" + this.name + ", '" + this.filterFreeFormat + "')") + "||''''";
            case 10:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql("(case when length(" + this.name + ") = 16 then substr(" + this.name + ",1,6) || '******' || substr(" + this.name + ",13,4) else " + this.name + " end)") + "||''''";
            default:
               /***Added for Dynamic Grid*****/
               return "''''||" + this.encryptSql(this.name) + "||''''";
         }
      } else {
         switch (this.type) {
            case 1:
               return "''''||SQL_Util.QuotesEsc2(" + this.name + ")||''''";
            case 2:
               return "''''||to_char(" + this.name + ",'dd.mm.yyyy')||''''";
            case 3:
               return "''''||to_char(" + this.name + ",'FM999,999,999,999,999,990" + this.numberScaleFormat + "')||''''";
            case 4:
               return "''''||to_char(" + this.name + ",'FM999,999,999,999,999,990.00')||''''";
            case 5:
               return "''''||to_char(" + this.name + ",'FM999G999G999G999G999G990D00')||''''";
            case 6:
               return "''''||to_char(" + this.name + ",'dd.mm.yyyy hh24:mi:ss')||''''";
            case 7:
               return "''''||to_char(" + this.name + ", 'hh24:mi:ss')||''''";
            case 8:
               return "''";
            case 9:
               return "''''||to_char(" + this.name + ", '" + this.filterFreeFormat + "')||''''";
            case 10:
               return "''''||case when length(" + this.name + ") = 16 then substr(" + this.name + ",1,6) || '******' || substr(" + this.name + ",13,4) else " + this.name + " end||''''";
            default:
               return "''''||SQL_Util.QuotesEsc2(" + this.name + ")||''''";
         }
      }
   }

   private String getFilterFormat(String value) {
      if ("in".equals(this.filterOperator)) {
         return value;
      } else {
         value = Util.quotesSQL(value);
         switch (this.type) {
            case 2:
               return " to_date('" + value + "', 'dd.mm.yyyy') ";
            case 3:
               return " to_number('" + Util.quotesEsc(value) + "'," + "'999999999999999999999D9999999999','NLS_NUMERIC_CHARACTERS=''. '''" + ")";
            case 4:
               return " to_number('" + Util.quotesEsc(value) + "'," + "'999999999999999999999D9999999999','NLS_NUMERIC_CHARACTERS=''. '''" + ")";
            case 5:
            default:
               return " '" + value + "' ";
            case 6:
               return " to_date('" + value + "', 'dd.mm.yyyy hh24:mi:ss') ";
         }
      }
   }

   private String getFieldNameFiltered(String columnName, int type) {
      switch (type) {
         case 10:
            return "(substr(" + columnName + ",1,6) || '******' || substr(" + columnName + ",13,4))";
         default:
            return columnName;
      }
   }

   public String formatInForSQL(String value) {
      String[] elements = value.split(",");
      StringBuilder result = new StringBuilder();

      for(int i = 0; i < elements.length; ++i) {
         String element = elements[i].trim();
         result.append("'").append(element).append("'");
         if (i < elements.length - 1) {
            result.append(",");
         }
      }

      return result.toString();
   }

   public String getFilter() {
      StringBuffer buf = null;
      if (this.filterValue != null) {
         if (buf == null) {
            buf = new StringBuffer();
         }

         String operator = this.filterOperator;
         String value = this.filterValue;
         if (operator.indexOf("search") > -1) {
            buf.append("upper(");
            buf.append(this.name);
            buf.append(") ");
            if (operator.equals("_search_")) {
               value = "%" + value + "%";
               operator = "like";
            } else if (operator.equals("_search")) {
               value = "%" + value;
               operator = "like";
            } else if (operator.equals("search_")) {
               value = value + "%";
               operator = "like";
            }

            buf.append(operator);
            buf.append(" upper(");
            buf.append(this.getFilterFormat(value));
            buf.append(")");
         } else if (operator.indexOf("translit") > -1) {
            buf.append("(upper(");
            buf.append(this.name);
            buf.append(") ");
            if (operator.equals("_translit_")) {
               value = "%" + value + "%";
               operator = "like";
            } else if (operator.equals("_translit")) {
               value = "%" + value;
               operator = "like";
            } else if (operator.equals("translit_")) {
               value = value + "%";
               operator = "like";
            }

            buf.append(operator);
            buf.append(" upper(");
            buf.append(this.getFilterFormat(value));
            buf.append(") or upper(");
            operator = this.filterOperator;
            value = this.filterValue;
            buf.append(this.name);
            buf.append(") ");
            if (operator.equals("_translit_")) {
               value = "%" + Util.replaceUzcToUzland(value) + "%";
               operator = "like";
            } else if (operator.equals("_translit")) {
               value = "%" + Util.replaceUzcToUzland(value);
               operator = "like";
            } else if (operator.equals("translit_")) {
               value = Util.replaceUzcToUzland(value) + "%";
               operator = "like";
            }

            buf.append(operator);
            buf.append(" upper(");
            buf.append(this.getFilterFormat(value));
            buf.append("))");
         } else {
            buf.append(this.getFieldNameFiltered(this.name, this.type));
            buf.append(" ");
            if (operator.equals("_like_")) {
               value = "%" + value + "%";
               operator = "like";
            } else if (operator.equals("_like")) {
               value = "%" + value;
               operator = "like";
            } else if (operator.equals("like_")) {
               value = value + "%";
               operator = "like";
            } else if (operator.equals("in")) {
               value = "(" + this.formatInForSQL(value) + ")";
               operator = " in ";
            }

            buf.append(operator);
            buf.append(this.getFilterFormat(value));
         }
      }

      if (this.filterValue2 != null) {
         if (buf == null) {
            buf = new StringBuffer();
         } else {
            buf.append(" and ");
         }

         buf.append(this.name);
         buf.append("<=");
         buf.append(this.getFilterFormat(this.filterValue2));
      }

      return buf != null ? buf.toString() : null;
   }

   public void clearFilterValues() {
      this.setFilterValue((String)null);
      this.setFilterValue2((String)null);
   }

   public String getFilterValues() {
      StringBuffer ret = null;
      String value = this.filterValue;
      String value2 = this.filterValue2;
      if (this.filterType == 2) {
         if (value == null) {
            if (value2 != null) {
               value = "";
            }
         } else {
            value2 = Util.nvl(value2);
         }
      }

      if (value != null) {
         if (ret == null) {
            ret = new StringBuffer();
         }

         ret.append('\'');
         ret.append(Util.quotesEsc(value));
         ret.append('\'');
      }

      if (value2 != null) {
         if (ret == null) {
            ret = new StringBuffer();
         } else {
            ret.append(',');
         }

         ret.append('\'');
         ret.append(Util.quotesEsc(value2));
         ret.append('\'');
      }

      if (ret != null) {
         ret.insert(0, "f" + this.id + ":[").append("]");
         if (this.filterType == 3) {
            ret.append(",fo");
            ret.append(this.id);
            ret.append(":");
            ret.append('\'');
            ret.append(this.filterOperator);
            ret.append('\'');
         }

         return ret.toString();
      } else {
         return null;
      }
   }

   public String getName() {
      return this.name;
   }

   public String getLabel() {
      return this.label;
   }

   public String toString() {
      JHash result = new JHash();
      result.put("i", this.id);
      if (this.label != null) {
         result.put("l", this.label);
      }

      if (this.color != null) {
         result.put("c", this.color);
      }

      if (this.bgColor != null) {
         result.put("bgc", this.bgColor);
      }

      if (this.filterType > 0) {
         JHash filter = new JHash();
         filter.put("t", this.filterType);
         if (this.filterInGrid != null) {
            filter.put("g", 1);
         }

         if (this.filterLabel != null) {
            filter.put("l", this.filterLabel);
         }

         if (this.filterOption != null) {
            filter.put("s", this.filterOption);
         }

         if (this.filterMask != null) {
            filter.put("m", this.filterMask);
         }

         if (this.filterSize != null) {
            filter.put("c", this.filterSize);
         }

         if (this.filterReferenceName != null) {
            filter.put("rf", this.filterReferenceName);
         }

         if (this.filterRequestName != null) {
            filter.put("rq", this.filterRequestName);
         }

         if (this.filterReferenceURL != null) {
            filter.put("rfu", this.filterReferenceURL);
         }

         if (this.filterRequestURL != null) {
            filter.put("rqu", this.filterRequestURL);
         }

         result.put("f", filter);
      }

      return result.toString();
   }
}
