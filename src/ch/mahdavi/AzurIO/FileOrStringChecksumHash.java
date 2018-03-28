package ch.mahdavi.AzurIO;

/*
    File: FileOrStringChecksumHash.java
    Author: Abass MAHDAVI
    Date of creation: march 20th 2018
    Tested with Java 8

    purpose:
    FileOrStringChecksumHash is a enum class with several static methods that
    allow to provide the hash checksum of a file or a string for a given hashing
    algorithm (ex SHA-1)

 */

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public enum FileOrStringChecksumHash {

    SHA1("SHA1"), SHA256("SHA-256"), SHA512("SHA-512"), MD5("MD5");

    private String algorithmName;

    FileOrStringChecksumHash(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    private byte[] checksumByteArray(byte[] input) {
        try {
            return MessageDigest.getInstance(algorithmName).digest(input);
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private  byte[] checksumByteArray(String input) {
        return checksumByteArray(input.getBytes());
    }

    private  byte[] checksumByteArray(File input)  {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance(algorithmName);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            MessageNotifier.notifyMessage(e.getMessage());
        }
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(input);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            MessageNotifier.notifyMessage(e.getMessage());
        }
        int n = 0;
        byte[] buffer = new byte[8192];
        while (n != -1) {
            try {
                n = fileInputStream.read(buffer);
            } catch (IOException e) {
                e.printStackTrace();
                MessageNotifier.notifyMessage(e.getMessage());
            }
            if (n > 0) {
                digest.update(buffer, 0, n);
            }
        }
        return digest.digest();
    }

    private  String bytesArrayToHaxString(byte[] byteArray){
        return DatatypeConverter.printHexBinary(byteArray);
    }

    public  String checkSumString (File input){
        return bytesArrayToHaxString(checksumByteArray(input));
    }

    public  String checkSumString (String input){
        return bytesArrayToHaxString(checksumByteArray(input));
    }
}
