package com.duplicate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PartialDuplicates {
    public String readTheTextFile(String path) {
        try {
            String text = "";
            text = new String(Files.readAllBytes(Paths.get(path)));
            return text;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void compareFiles(String file1, String file2){
        StringBuilder stringBuilder = new StringBuilder();

    }

    public static String calculateFileHash(String filePath, String hashAlgorithm)
            throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] byteArray = new byte[8192];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }
        StringBuilder hashValue = new StringBuilder();
        for (byte b : digest.digest()) {
            hashValue.append(String.format("%02x", b));
        }
        return hashValue.toString();
    }


}
