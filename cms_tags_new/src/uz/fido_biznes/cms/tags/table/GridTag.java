package uz.fido_biznes.cms.tags.table;

import java.util.Vector;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Util;
import uz.fido_biznes.cms.tags.ReferenceTag;

public class GridTag extends TagSupport {
   private static final long serialVersionUID = 1L;
   private String page = "0";
   private String numbering;
   private String withoutFocus;
   private String withoutCursor;
   private String withoutSortButtons;
   private String withoutRefreshButton;
   private String resetCursor;
   private String hideFilterButton;
   private String hideExcelButton;
   private String enterDirection;
   private String rowColor;
   private Vector columns;
   private Vector feet;

   public void setPage(String v) {
      this.page = v;
   }

   public void setNumbering(String v) {
      this.numbering = v;
   }

   public void setWithoutFocus(String v) {
      this.withoutFocus = v;
   }

   public void setWithoutCursor(String v) {
      this.withoutCursor = v;
   }

   public void setWithoutSortButtons(String v) {
      this.withoutSortButtons = v;
   }

   public void setWithoutRefreshButton(String v) {
      this.withoutRefreshButton = v;
   }

   public void setResetCursor(String resetCursor) {
      this.resetCursor = resetCursor;
   }

   public void setHideFilterButton(String hideFilterButton) {
      this.hideFilterButton = hideFilterButton;
   }

   public void setHideExcelButton(String hideExcelButton) {
      this.hideExcelButton = hideExcelButton;
   }

   public void setEnterDirection(String v) {
      this.enterDirection = v;
   }

   public void setRowColor(String rowColor) {
      this.rowColor = rowColor;
   }

   public int doStartTag() throws JspException {
      if (this.getParent() == null) {
         throw new JspException("Table tag: Grid tag has to be in the table tag");
      } else {
         this.columns = new Vector();
         return 1;
      }
   }

   public void addColumn(Column column) {
      this.columns.addElement(column);
   }

   public void setFoot(Vector v) {
      this.feet = v;
   }

   public int doEndTag() throws JspException {
      try {
         if (this.columns.size() == 0) {
            throw new RuntimeException("Column definition not found in the GRID tag.");
         }

         TableTag tableTag = (TableTag)this.getParent();
         Value value = tableTag.getValue();
         StringBuffer buf = new StringBuffer(",c:");
         buf.append(this.columns.toString());
         if (this.feet != null) {
            buf.append(",t:").append(this.feet.toString());
         }

         int flag = 0;
         if (this.numbering != null) {
            flag |= 1;
         }

         if (this.withoutFocus != null) {
            flag |= 2;
         }

         if (this.withoutCursor != null) {
            flag |= 4;
         }

         if (this.withoutSortButtons != null) {
            flag |= 8;
         }

         if (this.withoutRefreshButton != null) {
            flag |= 16;
         }

         if (tableTag.isDebug()) {
            flag |= 32;
         }

         if (this.hideFilterButton != null) {
            flag |= 64;
         }

         if (this.resetCursor != null || tableTag.getParent() instanceof ReferenceTag) {
            flag |= 128;
         }

         if (this.hideExcelButton != null) {
            flag |= 256;
         }

         if (flag != 0) {
            buf.append(",f:").append(flag);
         }

         if (this.enterDirection != null) {
            buf.append(",e:");
            if (this.enterDirection.equals("up")) {
               buf.append(0);
            } else if (this.enterDirection.equals("right")) {
               buf.append(1);
            } else if (this.enterDirection.equals("left")) {
               buf.append(3);
            } else {
               buf.append(2);
            }
         }

         if (this.rowColor != null) {
            buf.append(",rc:'");
            buf.append(Util.quotesEsc(this.rowColor));
            buf.append('\'');
         }

         value.setGridDefinition(buf.toString());
         value.setRecordsPerPage(this.page);
         value.setResetCursor((flag & 128) != 0);
         value.setColumns(this.columns);
         value.setFeet(this.feet);
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      } finally {
         this.columns = null;
         this.feet = null;
      }

      return 6;
   }
}
