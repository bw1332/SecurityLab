package com.passwordsCheking;

import java.io.*;
import java.util.HashMap;
import java.util.Set;

public class Pairs {

    private HashMap<String, Content> pair;

    public Pairs(){
        pair = new HashMap<>();
    }

    public boolean checkName(String name){
        return pair.containsKey(name);
    }

    public void addPair(String name, String password){
        pair.put(name,new Content(password));
    }

    /**
     * translate the HashMap<String, Content> object to bytes
     * @return
     * @throws IOException
     */
    public byte[] writeToBytes() throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(pair);
        byte[] res = byteArrayOutputStream.toByteArray();
        byteArrayOutputStream.close();
        objectOutputStream.close();
        return res;
    }

    /**
     * translate bytes to HashMap<String, Content> object
     */
    public void readFromBytes(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        pair = (HashMap<String, Content>)objectInputStream.readObject();
        byteArrayInputStream.close();
        objectInputStream.close();
    }

    public Content get(String key){
        return pair.get(key);
    }

    public Set<String> showAllUser(){
        if(pair!=null){
            return pair.keySet();
        }
        return null;
    }

}
