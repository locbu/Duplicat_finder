package com.duplicate;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.List;
import java.util.stream.Stream;

import static java.nio.file.Path.of;

public class GetApp {

    public Map<String, String> call(String path) throws NoSuchAlgorithmException, IOException {

        return prepareResults(findPathAndSize(path), findOnlyDuplicates(findPathAndSize(path)));
    }

    private Map<String, String> findPathAndSize(String path) {
        Map<String, String> pathAndSize = new HashMap<>();
        try (Stream<Path> filePathStream = Files.walk(Paths.get(path))) {
            filePathStream.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    try {
                        pathAndSize.put(String.valueOf(filePath), String.valueOf(Files.readAttributes(of(String.valueOf(filePath)), BasicFileAttributes.class).size()));
                    } catch (IOException e) {
                        System.out.println("File is not read - " + e);
                    }
                }
            });
        } catch (Exception ex) {
            System.out.println("Path is not read - " + ex);
        }
        return pathAndSize;
    }

    private List<String> findOnlyDuplicates(Map<String, String> pathAndSize) {
        Set<String> allElements;
        allElements = new HashSet<>();
        List<String> onlyDuplicates = new ArrayList<>();
        for (String item : pathAndSize.values()) {
            if (allElements.contains(item)) {
                onlyDuplicates.add(item);
            } else {
                allElements.add(item);
            }
        }
        return onlyDuplicates;
    }

    private Map<String, String> prepareResults(Map<String, String> pathAndSize, List<String> onlyDuplicates) throws NoSuchAlgorithmException, IOException {
        final Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, String> mapVal : pathAndSize.entrySet()) {
            StringBuilder sb;
            if (onlyDuplicates.contains(mapVal.getValue())) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                try {
                    FileInputStream getFile = new FileInputStream(mapVal.getKey());
                    byte[] dataBytes = new byte[1024];
                    int nread;
                    while ((nread = getFile.read(dataBytes)) != -1) {
                        md.update(dataBytes, 0, nread);
                    }
                    byte[] mdBytes = md.digest();
                    sb = new StringBuilder();
                    for (byte mdByte : mdBytes) {
                        sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
                    }
                    result.put(mapVal.getKey(), sb.toString());
                } catch (IOException e) {
                    System.out.println("Input Stream failed - " + e);
                }
                FileInputStream getFile = new FileInputStream(mapVal.getKey());
                byte[] dataBytes = new byte[1024];
                int nread;
                while ((nread = getFile.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, nread);
                }
                byte[] mdBytes = md.digest();
                sb = new StringBuilder();
                for (byte mdByte : mdBytes) {
                    sb.append(Integer.toString((mdByte & 0xff) + 0x100, 16).substring(1));
                }
                result.put(mapVal.getKey(), sb.toString());
            }
        }
        return result;
    }

    public boolean compareImages(String imageFile1, String imageFile2) throws IOException {
        BufferedImage image1 = ImageIO.read(new File(imageFile1));
        BufferedImage image2 = ImageIO.read(new File(imageFile2));
        if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight()) {
            return false;
        }
        for (int y = 0; y < image1.getHeight(); y++) {
            for (int x = 0; x < image1.getWidth(); x++) {
                if (image1.getRGB(x, y) != image2.getRGB(x, y)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isPartialDuplicate(String text1, String text2) throws IOException {
        int windowSize = 100;
        for (int i = 0; i <= text2.length() - windowSize; i++) {
            String substring = text2.substring(i, i + windowSize);
            if (text1.contains(substring)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> removeDuplicatesFromFiles(String[] filePaths) throws IOException {
        Set<String> uniqueLines = new HashSet<>();
        for (String filePath : filePaths) {
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    uniqueLines.add(line);
                }
            }
        }

        return uniqueLines;
    }

    static boolean binarySearch(String[] array, String target) {
        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int compareResult = array[mid].compareTo(target);

            if (compareResult == 0) {
                return true;
            } else if (compareResult < 0) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return false;
    }

    public String calculateFileHash(String filePath) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (InputStream inputStream = new FileInputStream(filePath)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }
        byte[] hashBytes = digest.digest();
        StringBuilder hexHash = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexHash.append('0');
            hexHash.append(hex);
        }
        return hexHash.toString();
    }

    public int calculateHammingDistance(String hash1, String hash2) {
        int distance = 0;
        for (int i = 0; i < hash1.length(); i++) {
            if (hash1.charAt(i) != hash2.charAt(i)) {
                distance++;
            }
        }
        return distance;
    }

    public String computeAverageHash(File imageFile) throws IOException {
        BufferedImage image = ImageIO.read(imageFile);

        Image resizedImage = image.getScaledInstance(8, 8, Image.SCALE_SMOOTH);
        BufferedImage resizedBufferedImage = new BufferedImage(8, 8, BufferedImage.TYPE_BYTE_GRAY);

        Graphics2D graphics = resizedBufferedImage.createGraphics();
        graphics.drawImage(resizedImage, 0, 0, null);
        graphics.dispose();

        int averagePixelValue = 0;
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                averagePixelValue += resizedBufferedImage.getRGB(x, y) & 0xFF;
            }
        }
        averagePixelValue /= 64;

        StringBuilder hashBuilder = new StringBuilder();
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                int pixelValue = resizedBufferedImage.getRGB(x, y) & 0xFF;
                if (pixelValue >= averagePixelValue) {
                    hashBuilder.append("1");
                } else {
                    hashBuilder.append("0");
                }
            }
        }

        return hashBuilder.toString();
    }


}

