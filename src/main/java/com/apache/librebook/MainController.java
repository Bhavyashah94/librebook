/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apache.librebook;

import com.apache.librebook.database.DataBaseHandler;
import java.io.IOException;
import java.net.URL;
import static java.sql.JDBCType.INTEGER;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;


/**
 * FXML Controller class
 */
public class MainController implements Initializable {

    @FXML
    private ImageView coverImage;
    
    DataBaseHandler dataBaseHandler;
    ImageCache image = ImageCache.getImageCache();
    
    @FXML
    private TextArea bookInfo;
    @FXML
    private TextArea memberInfo;
    @FXML
    private TextField isbnInfo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic if needed
        dataBaseHandler = DataBaseHandler.getInstance();
    }

    // Load different FXML views
    @FXML
    private void addMember(ActionEvent event) {
        loadFXMLView("AddMember", "Add Member", event);
    }
    @FXML
    private void addBook(ActionEvent event) {
        loadFXMLView("AutoAddBook", "Add Book", event);
    }
    @FXML
    private void viewMember(ActionEvent event) {
        loadFXMLView("ViewMembers", "View Members", event);
    }

    @FXML
    private void viewBook(ActionEvent event) {
        loadFXMLView("ViewBooks", "View Books", event);
    }

    @FXML
    private void viewRecords(ActionEvent event) {
        loadFXMLView("ViewRecords", "View Records", event);
    }

    @FXML
    private void openSettings(ActionEvent event) {
        loadFXMLView("Settings", "Settings", event);
    }

    // Method to load an FXML view
    private void loadFXMLView(String fxmlFileName, String title, ActionEvent event) {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + fxmlFileName + ".fxml"));
            Parent parent = loader.load();

            // Get the stage information from the event source
            Stage stage = new Stage();
            stage.setScene(new Scene(parent));
            stage.setTitle(title);

            // Get the current stage (parent window) and close it when the new stage is closed
            Stage currentStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            currentStage.setOnCloseRequest(e -> stage.close());

            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void issueBook(ActionEvent event) {
    }

    @FXML
    private void loadBooksinfo(ActionEvent event) {
        System.out.println("Search is called");
        String sql = "SELECT * FROM BOOKS WHERE ISBN13 = ?";
        try {
            ResultSet rs = dataBaseHandler.execQuery(sql,isbnInfo.getText());
            boolean flag = false;
            while (rs.next()) {
                String isbn = rs.getString("ISBN13");
                String title = rs.getString("title");
                String authors = rs.getString("authors");
                String publisher = rs.getString("publisher");
                String imageUrl = rs.getString("imageUrl");
                int totalQuantity = rs.getInt("totalQuantity");
                int availableQuantity = rs.getInt("availableQuantaty");
                bookInfo.setText("ISBN:"+isbn+"\nTitle"+title+"\nAuthors:"+authors+"\nPublisher"+publisher+"\nTotalQuantatiy: "+Integer.toString(totalQuantity)+"\nAvailableQuantity: "+Integer.toString(availableQuantity));
                coverImage.setImage(image.getImage(imageUrl));
                flag = true;
            }
            if (!flag) {
                bookInfo.setText("Book Not Availaible");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
    }

}

