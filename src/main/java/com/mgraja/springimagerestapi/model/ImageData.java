package com.mgraja.springimagerestapi.model;

public class ImageData {
    private String id;
    private int width;
    private int height;

    public ImageData(String id, int width, int height) {
        this.id = id;
        this.width = width;
        this.height = height;
    }
    public String getId() {
        return id;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
