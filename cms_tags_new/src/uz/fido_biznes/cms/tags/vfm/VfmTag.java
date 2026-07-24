package uz.fido_biznes.cms.tags.vfm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyTagSupport;
import uz.fido_biznes.cms.Language;
import uz.fido_biznes.cms.ServletCallableStatement;
import uz.fido_biznes.cms.User;
import uz.fido_biznes.cms.Util;
import uz.fido_biznes.sql.StoredObject;

public class VfmTag extends BodyTagSupport {
   private String table;
   private int siTitle = -1;
   private String title;
   private static final String procedure = "vfm_api.Get_Vfm_Fields_As_Arr";
   private Map<String, String> columns;
   private HttpServletRequest request;
   private HttpSession session;
   private StoredObject stored;
   private User user;

   public int doStartTag() throws JspException {
      try {
         if (this.getParent() == null) {
            throw new JspException("Form tag: Vfm tag has to be in the form tag");
         }

         this.request = (HttpServletRequest)this.pageContext.getRequest();
         this.session = this.pageContext.getSession();
         this.stored = (StoredObject)this.session.getValue("stored");
         this.user = (User)this.pageContext.getAttribute("user", 3);
         if (this.siTitle >= 0) {
            Language lang = (Language)this.pageContext.getAttribute("lang");
            this.title = lang.get(this.siTitle);
         }
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }

      return super.doStartTag();
   }

   public int doEndTag() throws JspException {
      try {
         ServletCallableStatement cs = new ServletCallableStatement(this.stored);
         cs.setFunction("vfm_api.Get_Vfm_Fields_As_Arr");
         cs.registerArrayStringResult();
         cs.setString("i_Table_Name", this.table);
         String[] names = (String[])this.columns.keySet().toArray(new String[0]);
         String[] values = (String[])this.columns.values().toArray(new String[0]);
         cs.setArrayString("i_Column_Names", names);
         cs.setArrayString("i_Column_Values", values);
         cs.execute();
         String[] arrayResult = cs.getArrayResult();
         StringBuilder stringBuilder = new StringBuilder();

         for(String s : arrayResult) {
            stringBuilder.append(s);
         }

         JspWriter out = this.pageContext.getOut();
         String vf;
         if (this.title != null) {
            vf = this.title;
         } else {
            int languageIndex = this.user.getLanguageIndex() - 1;
            if (languageIndex == 0) {
               vf = "����������� ����";
            } else if (languageIndex == 1) {
               vf = "������� ����";
            } else if (languageIndex == 2) {
               vf = "Virtual maydonlar";
            } else if (languageIndex == 3) {
               vf = "Virtual field";
            } else {
               vf = "����������� ����";
            }
         }

         out.write("<script> var vfm=" + stringBuilder.toString() + ";</script>");
         out.write("<fieldset id=\"vfm\">\n     <legend onclick=\"vfmToggle(this)\">" + vf + ":  <span>&#x25B2;</span></legend>\n" + "     <div id=\"vfm-content\"></div>\n" + "</fieldset>");
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      } finally {
         this.columns = null;
         this.table = null;
      }

      return super.doEndTag();
   }

   public void putColumn(String name, String value) {
      if (this.columns == null) {
         this.columns = new HashMap();
      }

      this.columns.put(name, value);
   }

   public String getTable() {
      return this.table;
   }

   public void setTable(String table) {
      this.table = table;
   }

   public int getTitle() {
      return this.siTitle;
   }

   public void setTitle(int siTitle) {
      this.siTitle = siTitle;
   }

   public String getTitleText() {
      return this.title;
   }

   public void setTitleText(String title) {
      this.title = title;
   }
}
