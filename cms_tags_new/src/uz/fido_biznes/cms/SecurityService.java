package uz.fido_biznes.cms;

import java.security.SecureRandom;
import uz.fido_biznes.sql.StoredObject;

public class SecurityService {
   private byte[] secretKey;
   private byte[] secretIv;
   private static final String CIPHER = "AES";

   public void setCredentials(StoredObject storedObject) throws Exception {
      this.secretKey = this.getSecureRandomValue();
      this.secretIv = this.getSecureRandomValue();
      ServletCallableStatement cs = new ServletCallableStatement(storedObject);
      cs.setProcedure("Core_Secure_Util.Set_Credentials");
      cs.setRaw("i_secure_key", this.secretKey);
      cs.setRaw("i_iv", this.secretIv);
      cs.execute();
   }

   private byte[] getSecureRandomValue() {
      byte[] secureRandomKeyBytes = new byte[32];
      SecureRandom secureRandom = new SecureRandom();
      secureRandom.nextBytes(secureRandomKeyBytes);
      return secureRandomKeyBytes;
   }

   public String encrypt(String value) {
      return "Encrypted: " + value;
   }

   public String decrypt(String value) {
      return "Decrypted: " + value;
   }
}
