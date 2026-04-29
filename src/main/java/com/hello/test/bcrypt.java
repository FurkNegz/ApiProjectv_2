package com.hello.test;

import org.mindrot.jbcrypt.BCrypt;


public class bcrypt {

    public static String hashPassword(String password) {

        int logRounds = 12;

        String salt = BCrypt.gensalt(logRounds);

        return BCrypt.hashpw(password, salt);
    }

    public static void main(String[] args) {
        String password = "mySecurePassword123";

        String hashedPassword = hashPassword(password);

        System.out.println("Hashed Password: " + hashedPassword);
    }
}
