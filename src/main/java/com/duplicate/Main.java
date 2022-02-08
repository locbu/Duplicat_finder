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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.Path.of;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        String path = scanner.nextLine();
        Map<String, Long> pathAndSize = new HashMap<>();
        try (
                Stream<Path> filePathStream = Files.walk(Paths.get(path))) {
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
                    pathAndSize.put(String.valueOf(filePath), attr.size());
                }
            });
        } catch (Exception ex) {
            System.out.println("Path is not read - " + ex);
        }
        Set<Object> set = new HashSet<>();
        List<Object> list = new ArrayList<>();
        for (
                Map.Entry<String, Long> getDublValue : pathAndSize.entrySet()) {
            if (!set.add(getDublValue.getValue())) {
                list.add(getDublValue.getValue());
            } else {
                set.add(getDublValue.getValue());
            }
        }
        for (Map.Entry<String, Long> mapVal : pathAndSize.entrySet()) {
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
                Map<String, String> finalVar = new HashMap<>();
                finalVar.put(mapVal.getKey(), String.valueOf(sb));
                Map<String, List<DuplicateItem>> groupedByHashResult = finalVar
                        .entrySet()
                        .stream()
                        .map(it -> new DuplicateItem(it.getKey(), it.getValue()))
                        .collect(Collectors.groupingBy(DuplicateItem::getHash));
                for (Map.Entry<String, List<DuplicateItem>> item : groupedByHashResult.entrySet()) {
                    System.out.println(" This files are fully dublicate");
                    for (DuplicateItem duplicateItem : item.getValue()) {
                        System.out.println(duplicateItem.getPath() + "");
                    }
                    System.out.println("---------------------");
                }
            }
        }
    }
}


