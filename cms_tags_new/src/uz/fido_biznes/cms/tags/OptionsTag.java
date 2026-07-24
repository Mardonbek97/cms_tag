package uz.fido_biznes.cms.tags;

import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Util;
import uz.fido_biznes.sql.StoredObject;

public class OptionsTag extends TagSupport {
   private String from;
   private String code;
   private String name;
   private String where;
   private String orderBy;
   private String distinct;

   public void setCode(String code) {
      this.code = code;
   }

   public void setFrom(String from) {
      this.from = from;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setOrderBy(String orderBy) {
      this.orderBy = orderBy;
   }

   public void setWhere(String where) {
      this.where = where;
   }

   public void setDistinct(String distinct) {
      this.distinct = distinct;
   }

   public int doStartTag() throws JspException {
      try {
         HttpSession session = this.pageContext.getSession();
         StoredObject stored = (StoredObject)session.getValue("stored");
         StringBuffer buf = new StringBuffer();
         buf.append("SELECT ");
         if ("true".equals(this.distinct)) {
            buf.append(" DISTINCT ");
         } else if (this.distinct != null && !"false".equals(this.distinct)) {
            throw new IllegalArgumentException("distinct must be 'true' or 'false'.");
         }

         buf.append("'<option value=\"'||");
         buf.append(this.code);
         buf.append("||'\">'||");
         buf.append(Util.nvl(this.name, this.code));
         buf.append(" FROM ");
         buf.append(this.from);
         if (this.where != null && this.where.length() > 0) {
            buf.append(" WHERE ");
            buf.append(this.where);
         }

         if (this.orderBy != null && this.orderBy.length() > 0) {
            buf.append(" ORDER BY ");
            buf.append(this.orderBy);
         }

         JspWriter out = this.pageContext.getOut();
         out.print(stored.execSelect(buf.toString()));
         return 0;
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }
   }
}
