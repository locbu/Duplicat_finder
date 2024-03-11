package com.duplicate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

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


    private void compareFiles(String file1, String file2) {
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

    public static boolean compareFilesByBytes(String filePath1, String filePath2) throws IOException {
        try (FileInputStream fis1 = new FileInputStream(filePath1);
             FileInputStream fis2 = new FileInputStream(filePath2)) {

            byte[] buffer1 = new byte[8192];
            byte[] buffer2 = new byte[8192];

            int bytesRead1 = 0;
            int bytesRead2 = 0;

            while ((bytesRead1 = fis1.read(buffer1)) != -1 && (bytesRead2 = fis2.read(buffer2)) != -1) {
                if (!Arrays.equals(Arrays.copyOf(buffer1, bytesRead1), Arrays.copyOf(buffer2, bytesRead2))) {
                    return false;
                }
            }
            return bytesRead1 == -1 && bytesRead2 == -1;
        }
    }


}
