/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apache.librebook.AddBookControllerD;

import com.apache.librebook.GoogleBooksApiService;
import com.apache.librebook.database.DataBaseHandler;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextFormatter;
import java.util.function.UnaryOperator;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bhavy
 */
public class AddBookController implements Initializable {

    @FXML
    private Label title;
    @FXML
    private Label authors;
    @FXML
    private Label description;
    @FXML
    private Label publishing;
    @FXML
    private Label label;
    @FXML
    private ImageView image;
    @FXML
    private TextField quantity;
    @FXML
    private TextField PTR;
    @FXML
    private TextField MRP;

    private String isbn;

    private DetailedBook dBook;

    private GoogleBooksApiService googleBooksApiService = new GoogleBooksApiService();

    DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();

    private int Quantity;
    private int price;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupNumericTextFieldInteger(quantity); // Setup numeric input for quantity
        setupNumericTextFieldFloat(PTR); // Setup numeric input for price
        setupNumericTextFieldFloat(MRP); // Setup numeric input for price
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
        loadbook();
    }

    private void loadbook() {
        try {
            dBook = googleBooksApiService.displayBook(isbn);
        } catch (Exception ex) {
            Logger.getLogger(AddBookController.class.getName()).log(Level.SEVERE, null, ex);
        }
        title.setText("Title: " + dBook.getTitle());
        description.setText("Description: " + dBook.getDescription());
        image.setImage(dBook.getImage());
        String authorsString = String.join(", ", dBook.getAuthors());
        authors.setText("Authors: " + authorsString);
        publishing.setText("Publisher:  " + dBook.getPublisher() + "\nPublishing Date: " + dBook.getPubllisherDate());

        try {
            String checkQuery = "SELECT PTR, MRP FROM books WHERE ISBN13 = ?";
            ResultSet rs = dataBaseHandler.execQuery(checkQuery, isbn);
            System.out.println("loaded:" + isbn);
            //System.out.println(rs.next());
            if (rs.next()) {

                System.out.println(rs.getDouble("MRP"));
                System.out.println(rs.getDouble("PTR"));

                MRP.setText(String.valueOf(rs.getDouble("MRP")));
                PTR.setText(String.valueOf(rs.getDouble("PTR")));
            } else {
                MRP.setText(dBook.getPrice());
            }
        } catch (SQLException ex) {
            System.err.println(ex.getLocalizedMessage());
        }

    }

    private void setupNumericTextFieldInteger(TextField textField) {
        // Create a filter that only allows numeric input
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) { // Only digits
                return change;
            }
            return null; // Reject the change
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    private void setupNumericTextFieldFloat(TextField textField) {
        // Create a filter that only allows numeric input
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*(\\.\\d{0,2})?")) { // Only digits
                return change;
            }
            return null; // Reject the change
        };

        TextFormatter<String> textFormatter = new TextFormatter<>(filter);
        textField.setTextFormatter(textFormatter);
    }

    @FXML
    private void increaseQuantity(ActionEvent event) {
        String Squantity = this.quantity.getText();
        Quantity = Integer.parseInt(Squantity);
        Quantity += 1;
        Squantity = String.valueOf(Quantity);
        this.quantity.setText(Squantity);
    }

    @FXML
    private void decreaseQuantity(ActionEvent event) {
        String Squantity = this.quantity.getText();
        Quantity = Integer.parseInt(Squantity);
        if (Quantity > 0) {
            Quantity -= 1;
        }
        Squantity = String.valueOf(Quantity);
        this.quantity.setText(Squantity);
    }

    @FXML
    private void addBook(ActionEvent event) throws SQLException {

        String Squantity = this.quantity.getText();
        Quantity = Integer.parseInt(Squantity);

        if (Quantity == 0) {
            return;
        }
        double mrp = Double.valueOf(MRP.getText());
        double ptr = Double.valueOf(PTR.getText());

        String checkQuery = "SELECT `totalQuantity`, `availableQuantaty` FROM `books` WHERE `ISBN13` = ?";

        try (ResultSet rs = dataBaseHandler.execQuery(checkQuery, isbn);) {
            if (rs.next()) {
                // Book exists, so update the totalQuantity and availableQuantaty
                int totalQuantity = rs.getInt("totalQuantity");
                int availableQuantaty = rs.getInt("availableQuantaty");

                String updateQuery = "UPDATE `books` SET `totalQuantity` = ?, `availableQuantaty` = ?, `MRP` = ?, `PTR` = ? WHERE `ISBN13` = ?";
                dataBaseHandler.execAction(updateQuery, (totalQuantity + Quantity), (availableQuantaty + Quantity), mrp, ptr, isbn);
                
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("Book Quantity added, MRP changes made");
                alert.showAndWait();
                
            } else {
                String authorsString = String.join(", ", dBook.getAuthors());

                String insertQuery = "INSERT INTO `books`(`title`, `authors`, `publisher`, `description`, `imageUrl`, `ISBN13`, `PTR`, `MRP`, `totalQuantity`, `availableQuantaty`) VALUES (?,?,?,?,?,?,?,?,?,?)";
                dataBaseHandler.execAction(insertQuery, dBook.getTitle(), authorsString, dBook.getPublisher(), dBook.getDescription(), dBook.getImageUrl(), dBook.getISBN13(), ptr, mrp, Quantity, Quantity);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("NEW Book Saved");
                alert.showAndWait();
            }
            
            
        } catch (SQLException ex) {
            System.err.println("Exception at checkData: AddBookCntroller" + ex.getLocalizedMessage());
        }
        ((Stage) title.getScene().getWindow()).close();
    }
}
