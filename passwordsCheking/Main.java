package com.passwordsCheking;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;


public class Main {

    public static void main(String[] args){

        /* check parameter, if it exist, use it as the path of key.ke */
        String keyFilePath = null;
        if(args != null && args.length > 0){
            keyFilePath = args[0];
        }

        Config config;
        Mode mode;
        byte[] salt, IV;
        Pairs content = new Pairs();
        /* check the whether files exist */
        boolean ifbuilt = false; // true if exist
        if (ControlHelper.checkConfigFile() && ControlHelper.checkDataFile()) {
            if(!ControlHelper.checkKeyFile(keyFilePath)){
                System.err.println("Miss Key File !");
                return;
            }
            ifbuilt = true;
            System.out.println("# Please input master password...");
        } else {
            System.out.println("# Hello! Please CREATE a master password...");
        }
        String password = readInput(System.in);
        if(ifbuilt){
            /* read config from file using hard code password, salt and IV. The config file using CBC mode to encrypt */
            try {
                config = Config.readFromBytes(SecurityHelper.CBCDecrypt(ControlHelper.readByteFromConfig()
                        , ControlHelper.Config_File_Key
                        , ControlHelper.Config_File_IV));
            } catch ( ClassNotFoundException| NoSuchAlgorithmException
                    | BadPaddingException | IOException | InvalidKeyException
                    | InvalidAlgorithmParameterException| NoSuchPaddingException | IllegalBlockSizeException e) {
                System.err.println("decrypt config fail");
                return;
            }
            if(config == null){
                System.err.println("damaged file");
                return;
            }
            mode = config.getMode();
            salt = config.getSalt();
            IV = config.getIV();
            try {
            /* decrypt data file */
                switch (mode) {
                    case ECB:
                        if (salt == null) {
                            System.err.println("damaged file");
                            return;
                        }
                        content.readFromBytes(SecurityHelper.ECBDecrypt(ControlHelper.readByteFromFile(), password, salt));
                        break;
                    case CBC:
                        if (salt == null || IV == null) {
                            System.err.println("damaged file");
                            return;
                        }
                        content.readFromBytes(SecurityHelper.CBCDecrypt(ControlHelper.readByteFromFile(), password, salt, IV));
                        break;
                    case CTR:
                        if (salt == null || IV == null) {
                            System.err.println("damaged file");
                            return;
                        }
                        content.readFromBytes(SecurityHelper.CTRDecrypt(ControlHelper.readByteFromFile(), password, salt, IV));
                        break;
                }
            } catch (InvalidKeySpecException| ClassNotFoundException| NoSuchAlgorithmException
                    | BadPaddingException | IOException | InvalidKeyException
                    | InvalidAlgorithmParameterException| NoSuchPaddingException | IllegalBlockSizeException e) {
                System.err.println("decrypt fail");
                return;
            }
            /* config done, asking for further operation */
            furtherOperation(content,mode,password, keyFilePath);
        }else {
            /* build new config */
            config = new Config();
            System.out.println("# Please input a mode (ECB / CBC(recommend) / CTR) for encryption");
            while(true) {
                String input = readInput(System.in);
                if (ControlHelper.checkMode(input)) {
                    mode = Mode.valueOf(input);
                    break;
                }
            }
            config.setMode(mode);
            furtherOperation(content,mode,password,keyFilePath);
        }
    }


    public static void furtherOperation(Pairs content, Mode mode, String password, String keyFilePath){
        while (true) {
            /* operations */
            System.out.println();
            System.out.println("###########################################################");
            System.out.println("To show all user names, enter \"all\"");
            System.out.println("To check whether a <user, password> exists, enter \"check\"");
            System.out.println("To add a <user, password> pair, enter \"add\"");
            System.out.println("To save, enter \"save\"(will save changes and exit)");
            System.out.println("To exit, enter \"exit\"(will save using old encryption mode)");
            System.out.println("###########################################################");
            System.out.println();

            String in = readInput(System.in);
            switch (in) {
                case "all":
                    showAllNames(content.showAllUser());
                    break;
                case "check":
                    System.out.println("# enter user name");
                    String name = readInput(System.in);
                    System.out.println("# enter password");
                    String pass = readInput(System.in);
                    checkPairs(content,name,pass);
                    break;
                case "add":
                    add(content);
                    break;
                case "save":
                    System.out.println("# Please enter a mode (ECB / CBC / CTR) for encryption");
                    while(true) {
                        String input = readInput(System.in);
                        if (ControlHelper.checkMode(input)) {
                            mode = Mode.valueOf(input);
                            break;
                        }
                        System.out.println("# Illegal Input");
                    }
                    try {
                        saveToFile(content, mode, password, keyFilePath);
                    }catch (InvalidKeySpecException| NoSuchAlgorithmException
                            | BadPaddingException | IOException | InvalidKeyException
                            | InvalidAlgorithmParameterException| NoSuchPaddingException | IllegalBlockSizeException e) {
                        System.err.println("save fail");
                        System.exit(0);
                    }
                    System.exit(0);
                case "exit":
                       /* save to file */
                    try {
                        saveToFile(content, mode, password, keyFilePath);
                    }catch (InvalidKeySpecException| NoSuchAlgorithmException
                            | BadPaddingException | IOException | InvalidKeyException
                            | InvalidAlgorithmParameterException| NoSuchPaddingException | IllegalBlockSizeException e) {
                        System.err.println("save fail");
                        System.exit(0);
                    }
                    System.exit(0);
                default:
                    System.out.println("# Illegal Input");
            }
        }
    }

    /**
     * add a new key-value to {@code pairs}
     *
     * @param pairs pairs
     */
    public static void add(Pairs pairs){
        System.out.println("# Enter a name");
        String name = readInput(System.in);
        System.out.println("# Enter your password");
        String password = readInput(System.in);
        if(pairs.checkName(name)){
            System.out.println("# user name already exists, enter \"yes\" will overwrite");
            String in = readInput(System.in);
            if(in.equals("yes")){
                pairs.addPair(name,password);
            }
        }else{
            pairs.addPair(name,password);
        }
    }

    /**
     * check whether {@code pairs} contains {@code name} and {@code password}
     */
    public static void checkPairs(Pairs pairs, String name, String password){
        if(pairs.checkName(name)){
            byte[] salt = pairs.get(name).getSalt();
            if(pairs.get(name).hashEqualTo(Content.generateHash(password,salt))){
                System.out.println("# exist this pair, user name and password are correct");
                return;
            }
        }
        System.out.println("# wrong user name or password");
    }

    /**
     * read input
     */
    public static String readInput(InputStream in){
        Scanner scanner = new Scanner(in);
        return scanner.next();
    }

    public static void showAllNames(Set<String> set){
        Iterator<String> it = set.iterator();
        int count = 0;
        while (it.hasNext()){
            count++;
            System.out.println(count + ". " + it.next());
        }
    }

    public static void saveToFile(Pairs pairs, Mode mode, String password, String keyFilePath) throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        /* write key file */
        ControlHelper.Config_File_Key = ControlHelper.generateRandomKey();
        ControlHelper.Config_File_IV = ControlHelper.generateRandomIV();
        ControlHelper.writeKeyFile(keyFilePath);

        /* write config file*/
        Config config = new Config();
        config.setMode(mode);
        config.setIV(ControlHelper.generateRandomIV());
        config.setSalt(ControlHelper.generateRandomSalt());
        ControlHelper.writeByteToConfig(SecurityHelper.CBCEncrypt(config.writeToBytes()
                , ControlHelper.Config_File_Key
                , ControlHelper.Config_File_IV));
        /* write data file */
        switch (mode){
            case ECB:
                ControlHelper.writeByteToFile(SecurityHelper.ECBEncrypt(pairs.writeToBytes(),password,config.getSalt()));
                break;
            case CBC:
                ControlHelper.writeByteToFile(SecurityHelper.CBCEncrypt(pairs.writeToBytes(), password, config.getSalt(), config.getIV()));
                break;
            case CTR:
                ControlHelper.writeByteToFile(SecurityHelper.CTREncrypt(pairs.writeToBytes(),password,config.getSalt(),config.getIV()));
        }
    }


}

