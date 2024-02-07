package com.duplicate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        var duplicates = new GetApp().call(inputAbsolutePath());
        Set<Map.Entry<String, List<DuplicateItem>>> groupedItems = groupedByHash(duplicates).entrySet();
        print(groupedItems);
    }

    private static String inputAbsolutePath() {
        System.out.print("Please enter the path (as a file system directory) to find a duplicate files - ");
        return new Scanner(System.in).nextLine();
    }

    private static Map<String, List<DuplicateItem>> groupedByHash(Map<String, String> items) {
        return items
                .entrySet()
                .stream()
                .map(it -> new DuplicateItem(it.getKey(), it.getValue()))
                .collect(Collectors.groupingBy(DuplicateItem::getHash));
    }

    private static void print(Set<Map.Entry<String, List<DuplicateItem>>> items) {
        items.forEach(entry -> {
            System.out.println("This files are fully duplicate");
            entry.getValue().forEach(duplicateItem -> System.out.println(duplicateItem.getPath()));
            System.out.println("---------------------");
        });
    }
}









