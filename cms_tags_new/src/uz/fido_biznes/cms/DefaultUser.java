package uz.fido_biznes.cms;

public class DefaultUser extends AbstractUser {
   private String userId;
   private String userName;
   private String filialCode;
   private int languageIndex;
   private String modalTitle;

   public String getFilialCode() {
      return this.filialCode;
   }

   public void setFilialCode(String filialCode) {
      this.filialCode = filialCode;
   }

   public String getUserId() {
      return this.userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   public String getUserName() {
      return this.userName;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public int getLanguageIndex() {
      return this.languageIndex;
   }

   public void setLanguageIndex(int languageIndex) {
      this.languageIndex = languageIndex;
   }

   public String getModalTitle() {
      return this.modalTitle;
   }

   public void setModalTitle(String modalTitle) {
      this.modalTitle = modalTitle;
   }
}
