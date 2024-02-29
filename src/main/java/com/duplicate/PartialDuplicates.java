package com.duplicate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;

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


}
