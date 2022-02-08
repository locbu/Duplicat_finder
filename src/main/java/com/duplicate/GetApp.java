package com.duplicate;

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
    private final Map<String, String> getMapResult = new HashMap<>();

    public Map<String, String> CallApp(String path) throws NoSuchAlgorithmException, IOException {
        getMapResult.clear();
        Map<String, String> pathAndSize = new HashMap<>();
        try (Stream<Path> filePathStream = Files.walk(Paths.get(path))) {
            filePathStream.forEach(filePath -> {
                if (Files.isRegularFile(filePath)) {
                    Path file = of(String.valueOf(filePath));
                    BasicFileAttributes attr = null;
                    try {
                        attr = Files.readAttributes(file, BasicFileAttributes.class);
                    } catch (IOException e) {
                        System.out.println("File is not read - " + e);
                    }
                    assert attr != null;
                    pathAndSize.put(String.valueOf(filePath), String.valueOf(attr.size()));
                }
            });
        } catch (Exception ex) {
            System.out.println("Path is not read - " + ex);
        }
        Set<Object> set = new HashSet<>();
        List<Object> list = new ArrayList<>();
        for (Map.Entry<String, String> getDublValue : pathAndSize.entrySet()) {

            if (!set.add(getDublValue.getValue())) {
                list.add(getDublValue.getValue());
            } else {
                set.add(getDublValue.getValue());
            }
        }
        for (Map.Entry<String, String> mapVal : pathAndSize.entrySet()) {
            StringBuilder sb;
            if (list.contains(mapVal.getValue())) {
                MessageDigest md = MessageDigest.getInstance("MD5");
                FileInputStream getFile = new FileInputStream(mapVal.getKey());
                byte[] dataBytes = new byte[1024];
                int nread;
                while ((nread = getFile.read(dataBytes)) != -1) {
                    md.update(dataBytes, 0, nread);
                }
                byte[] mdbytes = md.digest();
                sb = new StringBuilder();
                for (byte mdbyte : mdbytes) {
                    sb.append(Integer.toString((mdbyte & 0xff) + 0x100, 16).substring(1));
                }
                getMapResult.put(mapVal.getKey(), sb.toString());
            }
        }
        return getMapResult;
    }
}

