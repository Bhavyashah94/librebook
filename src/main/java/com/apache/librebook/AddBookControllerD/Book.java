/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apache.librebook.AddBookControllerD;

import java.util.List;
import javafx.scene.image.Image;

/**
 *
 * @author bhavy
 */
public class Book {

    private String title;
    private String description;
    private String publisher;
    private List<String> authors;
    private String ISBN13;
    private String imageUrl;  // For displaying the image in the list
    private Image image;
    
    public Book(String title, String description, String publisher, List<String> authors, String ISBN13, String imageUrl, Image image) {
        this.title = title;
        this.authors = authors;
        this.ISBN13 = ISBN13;
        this.imageUrl = imageUrl;
        this.image = image;
        this.description = description;
        this.publisher = publisher;
        
    }

    // Getters

    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public List<String> getAuthor() {
        return authors;
    }

    public String getISBN13() {
        return ISBN13;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Image getImage() {
        return image;
    }

    
    
    
}
