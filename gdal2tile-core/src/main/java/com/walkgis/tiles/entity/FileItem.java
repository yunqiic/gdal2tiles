package com.walkgis.tiles.entity;


import java.io.File;

public class FileItem {
    private File file;
    private String name;
    private Integer index;

    public FileItem() {
    }

    public FileItem(File file, Integer index) {
        this.file = file;
        this.index = index;
        this.name = file.getName();
    }

    public FileItem(File file, String name, Integer index) {
        this.file = file;
        this.name = name;
        this.index = index;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}
