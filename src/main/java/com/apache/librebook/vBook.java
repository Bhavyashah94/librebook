/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apache.librebook;

/**
 *
 * @author bhavy
 */
public class vBook {

        private String isbn;
        private String title;
        private String author;
        private String publisher;
        private int availableQuantity;
        private int totalQuantity;
        private String imageUrl; // You can also keep an Image object if preferred

        public vBook(String isbn, String title, String author, String publisher, int availableQuantity, int totalQuantity, String imageUrl) {
            this.isbn = isbn;
            this.title = title;
            this.author = author;
            this.publisher = publisher;
            this.availableQuantity = availableQuantity;
            this.totalQuantity = totalQuantity;
            this.imageUrl = imageUrl;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getPublisher() {
            return publisher;
        }

        public int getAvailableQuantity() {
            return availableQuantity;
        }

        public int getTotalQuantity() {
            return totalQuantity;
        }

        public String getImageUrl() {
            return imageUrl;
        }
    }