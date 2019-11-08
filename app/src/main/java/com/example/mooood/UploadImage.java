package com.example.mooood;

/**
 * This is a simple class for UploadImage which contains an image url string.
 * This class has a simple getter and setter.
 */
public class UploadImage {
    private String imageUrl;

    public UploadImage(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
