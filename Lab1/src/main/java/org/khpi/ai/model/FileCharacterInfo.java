package org.khpi.ai.model;

public class FileCharacterInfo {
    private final String fileName;
    private final Standard standard;

    public FileCharacterInfo(String fileName, Standard standard) {
        this.fileName = fileName;
        this.standard = standard;
    }

    public String getFileName() {
        return fileName;
    }

    public Standard getStandard() {
        return standard;
    }
}
