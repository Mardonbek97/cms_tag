package uz.fido_biznes.cms.tags.menu;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;
import uz.fido_biznes.cms.Language;
import uz.fido_biznes.cms.ServletCallableStatement;
import uz.fido_biznes.cms.User;
import uz.fido_biznes.cms.Util;
import uz.fido_biznes.sql.StoredObject;

public class SubmenuTag extends TagSupport {
   private String formCode;
   private String defaultUrl;
   private String isExitBtnEnabled;
   private String jsFunction;
   private int errorMsg = -1;
   private String errorMsgText;
   private int exitMsg = -1;
   private String exitMsgText;
   private HttpServletRequest request;
   private StoredObject stored;

   public void setFormCode(String formCode) {
      this.formCode = formCode;
   }

   public void setDefaultUrl(String defaultUrl) {
      this.defaultUrl = defaultUrl;
   }

   public void setIsExitBtnEnabled(String isExitBtnEnabled) {
      this.isExitBtnEnabled = isExitBtnEnabled;
   }

   public void setJsFunction(String jsFunction) {
      this.jsFunction = jsFunction;
   }

   public void setErrorMsg(int errorMsg) {
      this.errorMsg = errorMsg;
   }

   public void setErrorMsgText(String errorMsgText) {
      this.errorMsgText = errorMsgText;
   }

   public void setExitMsg(int exitMsg) {
      this.exitMsg = exitMsg;
   }

   public void setExitMsgText(String exitMsgText) {
      this.exitMsgText = exitMsgText;
   }

   public int doStartTag() throws JspException {
      try {
         if (this.getParent() == null) {
            throw new JspException("Form tag: submenu tag has to be in the form tag");
         }

         this.request = (HttpServletRequest)this.pageContext.getRequest();
         HttpSession session = this.pageContext.getSession();
         this.stored = (StoredObject)session.getAttribute("stored");
         User user = (User)this.pageContext.getAttribute("user", 3);
         Language lang = (Language)this.pageContext.getAttribute("lang");
         if (this.errorMsg >= 0) {
            this.errorMsgText = Util.nvl(lang.get(this.errorMsg), this.setDefaultErrorTxt(user.getLanguageIndex() - 1));
         } else {
            this.errorMsgText = this.setDefaultErrorTxt(user.getLanguageIndex() - 1);
         }

         if (this.exitMsg >= 0) {
            this.exitMsgText = Util.nvl(lang.get(this.exitMsg), this.setDefaultExitTxt(user.getLanguageIndex() - 1));
         } else {
            this.exitMsgText = this.setDefaultExitTxt(user.getLanguageIndex() - 1);
         }
      } catch (Exception ex) {
         throw new JspException(Util.getStackTrace(ex));
      }

      return super.doStartTag();
   }

   public int doEndTag() throws JspException {
      try {
         JspWriter out = this.pageContext.getOut();

         try {
            ServletCallableStatement cs = new ServletCallableStatement(this.stored, this.request);
            cs.setProcedure("core_menu.Get_Sub_Menu_As_Array");
            cs.setNumber("i_Form_Code", this.formCode);
            cs.registerArrayString("o_Result");
            cs.execute();
            String[] results = cs.getArray("o_Result");
            out.write("<script>var mn = " + Util.stringArrayToString(results));
            out.write(";</script>");
         } catch (Exception var5) {
            Exception ex = var5;

            try {
               Util.alertUserMessage(ex, out);
               out.write("<script>location.href=\"/ibs/contents.jsp\";</script>");
            } catch (IOException var4) {
               throw new JspException(Util.getStackTrace(var5));
            }
         }

         out.write("<script> function onLoad(){if (mn.items.length == 0){ alert(\"");
         out.write(this.errorMsgText);
         out.write("\");");
         out.write("go({url:'/ibs/contents.jsp',lock:false});");
         out.write("} else {drawTab(mn.items);");
         if (this.jsFunction != null) {
            out.write(this.jsFunction);
         }

         out.write("}}</script>");
         out.write("<table width=\"100%\" height=\"100%\" cellspacing=0 cellpadding=0>");
         out.write("<tr height=\"30px\"><td id=\"tabControls\" class=\"menuBlock\">&nbsp;");
         out.write("<td align=\"right\" class=\"menuBlock\" >");
         if (this.isExitBtnEnabled != null) {
            out.write("<input type=\"button\" class=btn onclick=\"go({url:'/ibs/contents.jsp'});\" value=\"" + this.exitMsgText + "\" >");
         } else {
            out.write("&nbsp;");
         }

         out.write("</td></tr>");
         out.write("<tr>\n\t\t<td style=\"height:100%\" colspan=2>\n\t\t<iframe name=\"menuBody\" src=\"" + this.defaultUrl + "\" width=100% height=100% marginheight=0 frameborder=0></iframe>\n" + "\t\t</td>\n" + "\t</tr>\t\n" + "</table>");
      } catch (Exception e) {
         throw new JspException(Util.getStackTrace(e));
      }

      return super.doEndTag();
   }

   private String setDefaultErrorTxt(int languageIndex) {
      switch (languageIndex) {
         case 0:
            return "� ��� ��� ������� � ����� ���� ����������.��������� ��������� ����� ���� �� ������.";
         case 1:
            return "��� ���� ����� �������� ���� ��������.������ ���� ��������� ���������.";
         case 2:
            return "Siz ushbu tizim menyusiga kira olmaysiz.Mavjud menyu rollarini tayinlang.";
         case 3:
            return "You do not have access to this subsystem menu.Assign the available menu forms to roles";
         default:
            return "� ��� ��� ������� � ����� ���� ����������.��������� ��������� ����� ���� �� ������.";
      }
   }

   private String setDefaultExitTxt(int languageIndex) {
      switch (languageIndex) {
         case 0:
            return "�����";
         case 1:
            return "��&#1179;��";
         case 2:
            return "Chiqish";
         case 3:
            return "Exit";
         default:
            return "�����";
      }
   }
}
