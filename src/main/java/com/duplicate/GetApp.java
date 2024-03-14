package com.duplicate;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
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

}

