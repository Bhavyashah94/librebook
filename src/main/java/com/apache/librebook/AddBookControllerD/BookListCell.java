package com.apache.librebook.AddBookControllerD;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class BookListCell extends ListCell<Book> {

    private ImageView imageView = new ImageView();
    private Label titleLabel = new Label();
    private Label authorLabel = new Label();
    private Label publisherLabel = new Label();
    private Label descriptionLabel = new Label();
    private Button addButton = new Button("Add");

    private VBox vbox = new VBox(titleLabel, authorLabel, publisherLabel, descriptionLabel);
    private HBox hbox = new HBox(imageView, vbox, addButton);

    public BookListCell() {
        // Set image size
        imageView.setFitWidth(180);
        imageView.setPreserveRatio(true);

        // Configure layout
        hbox.setSpacing(10);
        vbox.setSpacing(5);
        descriptionLabel.setWrapText(true);

        // Ensure VBox grows and takes up available space, but not vertically
        HBox.setHgrow(vbox, Priority.ALWAYS);
        VBox.setVgrow(descriptionLabel, Priority.ALWAYS);

        // Bind description max width to available space in HBox
        descriptionLabel.maxWidthProperty().bind(
                hbox.widthProperty().subtract(imageView.getFitWidth() + 100) // Adjust for button size
        );

        // Prevent HBox from expanding vertically
        hbox.setPrefHeight(USE_COMPUTED_SIZE);
        hbox.setMaxHeight(USE_PREF_SIZE);

        // Configure button
        addButton.setMinWidth(60);  // Optional: set a minimum width for the button
        HBox.setHgrow(addButton, Priority.NEVER);

        // Ensure the HBox fills available width, but not height
        hbox.setFillHeight(false);

        // Make the ListCell resizable
        setMaxWidth(Double.MAX_VALUE);

        listViewProperty().addListener((obs, oldListView, newListView) -> {
            if (newListView != null) {
                hbox.maxWidthProperty().bind(newListView.widthProperty().subtract(18));  // Account for scrollbar width
            }
        });

        // Set the HBox as the graphic for the cell
        setGraphic(hbox);
    }

    @Override
    protected void updateItem(Book book, boolean empty) {
        super.updateItem(book, empty);
        if (empty || book == null) {
            setGraphic(null);
        } else {
            titleLabel.setText("Title: " + book.getTitle());
            //convert author list to string
            String authorsString = String.join(", ", book.getAuthor());
            authorLabel.setText("Authors: " + authorsString);

            publisherLabel.setText("Publisher: " + book.getPublisher());

            descriptionLabel.setText("Description: " + book.getDescription());
            
            imageView.setImage(book.getImage()); // Use cached image
            // Set the action for the button to pass the ISBN
            addButton.setOnAction(event -> {
                String isbn = book.getISBN13();  // Assuming Book class has getIsbn13() method
                System.out.println(isbn);
                toAddBook(isbn);
            });

            setGraphic(hbox);
        }
    }

    private void toAddBook(String isbn) {
         try {
            // Create an instance of FXMLLoader
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/addBook.fxml"));

            // Load the FXML file and get the root node (Parent)
            Parent parent = loader.load();

            // Get the controller instance from the loader
            AddBookController controller = loader.getController();

            // Pass the ISBN to the controller
            controller.setIsbn(isbn);

            // Get the parent stage (ListView's window)
            Stage parentStage = (Stage) this.getScene().getWindow(); // Current window of BookListCell

            // Create a new stage for the AddBook window
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));

            // Add a listener to close the AddBook stage when the parent stage is closed
            parentStage.setOnCloseRequest(event -> stage.close());

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
