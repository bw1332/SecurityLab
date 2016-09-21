package com.passwordsCheking;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class ControlHelper {

    public static SecretKey Config_File_Key = null;
    public static byte[] Config_File_IV = null;
    private final static String CONFIG_PATH = "config.co";
    private final static String FILE_PATH = "date.se";
    private final static String KEY_PATH = "key.ke";
    private static final int SALT_SIZE = 16;
    private static final int IV_SIZE = 16;
    private static SecureRandom secureRandom = new SecureRandom();


    /**
     * write bytes to data file
     */
    public static void writeByteToFile(byte[] bytes) throws IOException {
        File file = new File(FILE_PATH);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    /**
     * write bytes to config file
     */
    public static void writeByteToConfig(byte[] bytes) throws IOException {
        File file = new File(CONFIG_PATH);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    /**
     * read data from file
     */
    public static byte[] readByteFromFile() throws IOException {
        return Files.readAllBytes(Paths.get(FILE_PATH));
    }

    /**
     * read config from file
     */
    public static byte[] readByteFromConfig() throws IOException {
        return Files.readAllBytes(Paths.get(CONFIG_PATH));
    }

    /**
     *  generate a random salt
     */
    public static byte[] generateRandomSalt(){
        byte[] salt = new byte[SALT_SIZE];
        secureRandom.nextBytes(salt);
        return salt;
    }

    /**
     * to generate a random initial vector
     */
    public static byte[] generateRandomIV(){
        byte[] IV = new byte[IV_SIZE];
        secureRandom.nextBytes(IV);
        return IV;
    }

    /**
     * to generate a random Secretkey for encrypting config.co
     * the value will store in key.ke
     */
    public static SecretKey generateRandomKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(128);
        return keyGenerator.generateKey();
    }

    /**
     * check whether the file exists
     * @return true if exists
     */
    public static boolean checkDataFile(){
        return Files.exists(Paths.get(FILE_PATH));
    }
    /**
     * check whether the file exists
     * @return true if exists
     */
    public static boolean checkConfigFile(){
        return Files.exists(Paths.get(CONFIG_PATH));
    }

    /**
     * check whether the mode correct
     * @return true if correct
     */
    public static boolean checkMode(String mode){
        try{
            Mode.valueOf(mode);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static boolean checkKeyFile(String path){
        String realPath = KEY_PATH;
        /* if path exists, use the path, if not use KEY_PATH as default path */
        if(path != null){
            realPath = path;
        }
        if(Files.exists(Paths.get(realPath))){
            /* fiel exists*/
            try {
                byte[] fileBytes = Files.readAllBytes(Paths.get(realPath));
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(Base64.getDecoder().decode(fileBytes));
                ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
                byte[] IV = new byte[16];
                objectInputStream.read(IV);
                SecretKey key = (SecretKey)objectInputStream.readObject();
                if(key == null) {return false;}
                /* set IV and key value from the files*/
                Config_File_IV = IV;
                Config_File_Key = key;
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }else {
            return false;
        }
        return false;
    }

    /**
     * write key.ke file
     * if it hasa path, use the path. If not, use default path
     * @param path key.ke file path
     * @throws IOException
     */
    public static void writeKeyFile(String path) throws IOException {
        String realPath = KEY_PATH;
        if(path != null){
            realPath = path;
        }
        ByteArrayOutputStream byteArrayInputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayInputStream);
        objectOutputStream.write(Config_File_IV);
        objectOutputStream.writeObject(Config_File_Key);
        File file = new File(realPath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(Base64.getEncoder().encode(byteArrayInputStream.toByteArray()));
        byteArrayInputStream.close();
        objectOutputStream.close();
        fileOutputStream.close();

    }
}
