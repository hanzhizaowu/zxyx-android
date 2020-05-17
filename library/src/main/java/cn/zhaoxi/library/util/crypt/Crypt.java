package cn.zhaoxi.library.util.crypt;

import java.security.GeneralSecurityException;

public class Crypt {
    private static final String password = "hzpawd";

    public static String Encrypt(String msg) {
        String encryptedMsg = null;
        try {
            encryptedMsg = AESCrypt.encrypt(password, msg);
        }catch (GeneralSecurityException e){
            e.printStackTrace();
        }

        return encryptedMsg;
    }

    public static String Decrypt(String msg) {
        String messageAfterDecrypt = null;
        try {
            messageAfterDecrypt = AESCrypt.decrypt(password, msg);
        }catch (GeneralSecurityException e){
            //handle error
            e.printStackTrace();
        }

        return messageAfterDecrypt;
    }
}
