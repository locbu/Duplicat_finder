package com.duplicate;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class PartialDuplicates {
    public static final int NUM_HASH_FUNCTIONS = 100;

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

    public boolean compareFilesByBytes(String filePath1, String filePath2) throws IOException {
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

    public void filesSorting(File[] files) {
        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::length)
                    .thenComparing((f1, f2) -> {
                        String ext1 = getFileExtension(f1.getName());
                        String ext2 = getFileExtension(f2.getName());
                        return ext1.compareTo(ext2);
                    }).thenComparing(File::getName));
            for (File file : files) {
                System.out.println(file.getName() + " | Size: " + file.length() + " bytes");
            }
        } else {
            System.out.println("Directory is empty or does not exist.");
        }
    }

    private String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return fileName.substring(lastDotIndex + 1);
    }

    public String findLongestCommonSubsequence(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();

        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }

        StringBuilder lcs = new StringBuilder();
        int i = m, j = n;
        while (i > 0 && j > 0) {
            if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                lcs.append(s1.charAt(i - 1));
                i--;
                j--;
            } else if (dp[i - 1][j] > dp[i][j - 1]) {
                i--;
            } else {
                j--;
            }
        }
        return lcs.reverse().toString();
    }

    public Set<String> generateNGrams(String line, int n) {
        Set<String> ngrams = new HashSet<>();
        for (int i = 0; i <= line.length() - n; i++) {
            String ngram = line.substring(i, i + n);
            ngrams.add(ngram);
        }
        return ngrams;
    }

    public boolean isPartialMatch(String line1, String line2, int n) {
        Set<String> ngrams1 = generateNGrams(line1, n);
        Set<String> ngrams2 = generateNGrams(line2, n);

        ngrams1.retainAll(ngrams2);

        return (double) ngrams1.size() / Math.min(ngrams1.size(), ngrams2.size()) > 0.5;
    }

    public List<String> readLinesFromFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public void removeLinesFromFile(File file, Set<String> linesToRemove) throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getPath()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!linesToRemove.contains(line)) {
                    lines.add(line);
                }
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getPath()))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

//    public void removeDuplicates(File file1, File file2, int n) throws IOException {
//        List<String> lines1 = readLinesFromFile(file1.getPath());
//        List<String> lines2 = readLinesFromFile(file2.getPath());
//
//        Set<String> duplicates = new HashSet<>();
//
//        for (String line1 : lines1) {
//            for (String line2 : lines2) {
//                if (isPartialMatch(line1, line2, n)) {
//                    duplicates.add(line1);
//                    break;
//                }
//            }
//        }
//
//        removeLinesFromFile(file1, duplicates);
//        removeLinesFromFile(file2, duplicates);
//    }

    public static int hashFunction(String line, int seed) {
        int hash = 0;
        for (char c : line.toCharArray()) {
            hash = (seed * hash + c) % Integer.MAX_VALUE;
        }
        return hash;
    }

    public double calculateJaccardSimilarity(int[] signature1, int[] signature2) {
        int numMatches = 0;
        for (int i = 0; i < NUM_HASH_FUNCTIONS; i++) {
            if (signature1[i] == signature2[i]) {
                numMatches++;
            }
        }
        return (double) numMatches / NUM_HASH_FUNCTIONS;
    }

    public static int[] generateMinHashSignature(List<String> lines) {
        int[] signature = new int[NUM_HASH_FUNCTIONS];
        Arrays.fill(signature, Integer.MAX_VALUE);

        for (String line : lines) {
            for (int i = 0; i < NUM_HASH_FUNCTIONS; i++) {
                int hashValue = hashFunction(line, i);
                signature[i] = Math.min(signature[i], hashValue);
            }
        }
        return signature;
    }

    public void removeDuplicates(File file1, File file2) throws IOException {
        List<String> lines1 = readLinesFromFile(file1.getPath());
        List<String> lines2 = readLinesFromFile(file2.getPath());

        int[] signature1 = generateMinHashSignature(lines1);
        int[] signature2 = generateMinHashSignature(lines2);

        Set<String> duplicates = new HashSet<>();

        double similarity = calculateJaccardSimilarity(signature1, signature2);

        if (similarity > 0.8) {
            duplicates.addAll(lines1);
            duplicates.addAll(lines2);
        }

        removeLinesFromFile(file1, duplicates);
        removeLinesFromFile(file2, duplicates);
    }

    public static List<String> tokenize(String text) {
        String[] words = text.split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String word : words) {
            String token = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
            if (!token.isEmpty()) {
                tokens.add(token);
            }
        }
        return tokens;
    }

}
