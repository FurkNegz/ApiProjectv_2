package com.hello.classes;


import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
        public static String hashPassword(String password) {
            int saltLength=7;
            String hash= BCrypt.gensalt(saltLength);

            return BCrypt.hashpw(password, hash);
    }
    public static boolean checkPassword(String password, String hash) {
            return BCrypt.checkpw(password, hash);
    }
}
