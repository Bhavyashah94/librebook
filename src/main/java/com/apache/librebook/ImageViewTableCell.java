/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.apache.librebook;

/**
 *
 * @author bhavy
 */
import javafx.scene.control.TableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageViewTableCell extends TableCell<vBook, String> {

    private final ImageView imageView = new ImageView();
    ImageCache imageCache = ImageCache.getImageCache();

    @Override
    protected void updateItem(String imageUrl, boolean empty) {
        super.updateItem(imageUrl, empty);
        if (empty || imageUrl == null) {
            setGraphic(null);
        } else {
            Image image = imageCache.getImage(imageUrl);
            imageView.setImage(image);
            imageView.setFitWidth(100); // Set desired width
            imageView.setPreserveRatio(true); // Maintain aspect ratio
            setGraphic(imageView);
        }
    }
}
