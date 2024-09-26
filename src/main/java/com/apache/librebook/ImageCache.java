/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apache.librebook;

/**
 *
 * @author bhavy
 */
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import javafx.scene.image.Image;

public class ImageCache {
    private ConcurrentHashMap<String, Image> cache = new ConcurrentHashMap<>();
    
    private static ImageCache image = null;
    
    public static ImageCache getImageCache(){
        if(image == null){
            image = new ImageCache();
        }
        return image;
    }

    public Image getImage(String url) {
        // Handle null or empty URL
        if (url == null || url.isEmpty()) {
            return null; // or return a default image
        }

        return cache.computeIfAbsent(url, k -> {
            try {
                return new Image(k, true); // Load image and cache it
            } catch (Exception e) {
                e.printStackTrace(); // Log the error
                return null; // Return null if loading fails
            }
        });
    }
}

