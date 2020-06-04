package com.study.gftp;

import java.io.Serializable;

public class FileDocumentation implements Serializable {
    private String currentParent;
    private long length;
    private String fileName;
    private boolean file;
    private boolean directory;

    public String getCurrentParent() {
        return currentParent;
    }

    public void setCurrentParent(String currentParent) {
        this.currentParent = currentParent;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isFile() {
        return file;
    }

    public void setFile(boolean file) {
        this.file = file;
    }

    public boolean isDirectory() {
        return directory;
    }

    public void setDirectory(boolean directory) {
        this.directory = directory;
    }

    @Override
    public String toString() {
        return "FileDocumentation{" +
                "currentParent='" + currentParent + '\'' +
                ", length=" + length +
                ", fileName='" + fileName + '\'' +
                ", file=" + file +
                ", directory=" + directory +
                '}';
    }
}
