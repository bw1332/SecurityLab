package com.passwordsCheking;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Content implements Serializable {

    private static final int SALT_SIZE = 32;
    private static final int ITERATION = 10000;
    private static final int KEY_SIZE = 256;


    private byte[] salt;
    private byte[] passwordHash;

    public Content(String password){
        salt = generateRandomSalt();
        passwordHash = generateHash(password,salt);
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(byte[] passwordHash) {
        this.passwordHash = passwordHash;
    }

    public byte[] generateRandomSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_SIZE];
        random.nextBytes(salt);
        return salt;
    }

    /**
     * generate the hash value of the password
     * using PBKDF2WithHmacSHA512 algorithm
     */
    public static byte[] generateHash(String password, byte[] salt){
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt,ITERATION, KEY_SIZE);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            return res;

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException(e);
        }
    }

    /**
     * compare password hash
     *
     * @param hash hash code
     * @return true if passwordHash equals {@code hash}
     */
    public boolean hashEqualTo(byte[] hash){
        if(hash ==null) {
            return false;
        }
        if(hash.length != this.passwordHash.length){
            return false;
        }
        for(int i = 0; i < hash.length; i++){
            if(this.passwordHash[i] != hash[i]){
                return false;
            }
        }
        return true;
    }
}
