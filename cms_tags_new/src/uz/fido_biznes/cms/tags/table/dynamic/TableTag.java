package uz.fido_biznes.cms.tags.table.dynamic;

import java.util.Vector;
import javax.servlet.jsp.JspException;
import uz.fido_biznes.cms.tags.table.Field;
import uz.fido_biznes.cms.tags.table.Value;

public class TableTag extends uz.fido_biznes.cms.tags.table.TableTag {
   private static final long serialVersionUID = 1L;
   private String gridId;

   public String getGridId() {
      return this.gridId;
   }

   public void setGridId(String gridId) {
      this.gridId = gridId;
   }

   public static long getSerialversionuid() {
      return 1L;
   }

   public int doStartTag() throws JspException {
      return super.doStartTag();
   }

   public int doEndTag() throws JspException {
      this.setFrom("core_roles_v");
      Value value = new Value(1);
      value.setPageNumber(1);
      value.setRecordsPerPage("20");
      Vector columns = new Vector();
      value.setColumns(columns);
      Field field = new Field();
      field.setId("1");
      field.setName("role_id");
      field.setLabel("���");
      value.addField(field);
      return super.doEndTag();
   }
}
