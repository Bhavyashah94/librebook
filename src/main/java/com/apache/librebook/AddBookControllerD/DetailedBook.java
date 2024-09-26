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

    

public class DetailedBook {

    private String title;
    private String description;
    private String publisher;
    private String publlisherDate;
    private List<String> authors;
    private String ISBN13;
    private String price;
    private String imageUrl; 
    private Image image;

    public DetailedBook(String title, String description, String publisher, String publlisherDate, List<String> authors, String ISBN13, String price, String imageUrl, Image image) {
        this.title = title;
        this.description = description;
        this.publisher = publisher;
        this.publlisherDate = publlisherDate;
        this.authors = authors;
        this.ISBN13 = ISBN13;
        this.price = price;
        this.imageUrl = imageUrl;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getPubllisherDate() {
        return publlisherDate;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public String getISBN13() {
        return ISBN13;
    }

    public String getPrice() {
        //System.out.println(price);
        return price;
        
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Image getImage() {
        return image;
    }
    
    
}

