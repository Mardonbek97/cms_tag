package uz.fido_biznes.cms;

public abstract class AbstractUser implements User {
   public static final String[] MSG_SESSION_OUT = new String[]{"alert('������� ���������� ����� �����������.\\n��� ����������� ������ �������� ������� ������.');", "alert('������� ������������� ������� ������.\\n���� ����� ������� ���� �������� ������� ��������.');", "alert('Xavfsiz xarakatsizlik muddati tugadi.\\nIshni davom ettirish uchun parolni yangidan kiriting.');", "alert('Expired save period of inactivity.\\nEnter password again to continue work.');"};
   protected String sessionOutScript;
   protected String contextPath;

   public abstract int getLanguageIndex();

   public void setContextPath(String contextPath) {
      this.contextPath = contextPath;
   }

   public void setSessionOutScript(String sessionOutScript) {
      this.sessionOutScript = sessionOutScript;
   }

   public String getContextPath() {
      return Util.nvl(this.contextPath, "");
   }

   public String getSessionOutScript() {
      return Util.nvl(this.sessionOutScript, MSG_SESSION_OUT[this.getLanguageIndex() - 1]);
   }
}
