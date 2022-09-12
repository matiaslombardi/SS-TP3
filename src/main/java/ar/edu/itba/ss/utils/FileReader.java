package main.java.ar.edu.itba.ss.utils;

import java.io.File;

public class FileReader {
    public File getFile(String path) {
        try {
            return new File("src/files/" + path);
        } catch (NullPointerException e) {
            throw new RuntimeException(e);
        }
    }
}
