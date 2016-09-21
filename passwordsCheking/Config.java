package com.passwordsCheking;

import java.io.*;

public class Config implements Serializable {

    private Mode mode;
    private  byte[] salt;
    private  byte[] IV;


    public Mode getMode() {
        return mode;
    }

    public byte[] getSalt() {
        return salt;
    }

    public byte[] getIV() {
        return IV;
    }


    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public void setIV(byte[] IV) {
        this.IV = IV;
    }

    /**
     * transfer bytes to Config object
     *
     * @param bytes byte array
     * @return Config object
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static Config readFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        Config res = (Config) objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
        return res;
    }

    /**
     * transfer Config object to bytes
     *
     * @return byte array
     * @throws IOException
     */
    public byte[] writeToBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(this);
        byte[] res = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return res;
    }

}
