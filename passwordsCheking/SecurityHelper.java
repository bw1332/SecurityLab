package com.passwordsCheking;


import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class SecurityHelper {

    private static final int ITERATION_COUNT = 10000;
    private static final int SIZE = 128;


    /* ECB Mode */
    public static byte[] ECBEncrypt(byte[] plaintext, String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE,getKey(password,salt));
        return cipher.doFinal(plaintext);
    }

    public static byte[] ECBDecrypt(byte[] code, String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,getKey(password, salt));
        return  cipher.doFinal(code);
    }

    /* CBC mode */
    public static byte[] CBCEncrypt(byte[] plaintext, String password, byte[] salt, byte[] IV) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getKey(password, salt), new IvParameterSpec(IV));
        return cipher.doFinal(plaintext);
    }

    public static byte[] CBCDecrypt(byte[] code, String password, byte[] salt, byte[] IV) throws BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException, InvalidKeyException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE,getKey(password,salt), new IvParameterSpec(IV));
        return cipher.doFinal(code);
    }

    public static byte[] CBCEncrypt(byte[] plaintext, SecretKey key, byte[] IV) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV));
        return cipher.doFinal(plaintext);
    }

    public static byte[] CBCDecrypt(byte[] code, SecretKey key, byte[] IV) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV));
        return cipher.doFinal(code);
    }

    /* CTR mode */
    public static byte[] CTREncrypt(byte[] plaintext, String password, byte[] salt, byte[] IV) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, getKey(password,salt), new IvParameterSpec(IV));
        return cipher.doFinal(plaintext);
    }

    public static byte[] CTRDecrypt(byte[] code, String password, byte[] salt, byte[] IV) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeySpecException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, getKey(password,salt), new IvParameterSpec(IV));
        return cipher.doFinal(code);
    }

    /* generate a key for cipher */
    private static SecretKeySpec getKey(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, SIZE);
        SecretKey secretKey = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec);
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getEncoded(),"AES");
        return secretKeySpec;
    }


}
