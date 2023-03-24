package com.duplicate;


public record DuplicateItem(String path, String hash) {

    public String getPath() {
        return path;
    }

    public String getHash() {
        return hash;
    }
}
