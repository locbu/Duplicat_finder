package com.duplicate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Please enter the path (as a file system directory) to find a duplicate files - ");
        String path = scanner.nextLine();
        GetApp app = new GetApp();
        var getAllDuplicates = app.CallApp(path);
        Map<String, List<DuplicateItem>> groupedByHashResult = getAllDuplicates
                .entrySet()
                .stream()
                .map(it -> new DuplicateItem(it.getKey(), it.getValue()))
                .collect(Collectors.groupingBy(DuplicateItem::getHash));
        for (Map.Entry<String, List<DuplicateItem>> item : groupedByHashResult.entrySet()) {
            System.out.println(" This files are fully duplicate ");
            for (DuplicateItem duplicate : item.getValue()) {
                System.out.println(duplicate.getPath());
            }
            System.out.println("---------------------");
        }
    }
}









