/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apache.librebook;

import com.apache.librebook.database.DataBaseHandler;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bhavy
 */
public class AddBookExampleController implements Initializable {

    @FXML
    private TextField publisher;
    @FXML
    private TextField title;
    @FXML
    private TextField author;
    @FXML
    private TextField isbn10;
    @FXML
    private TextField isbn13;
    @FXML
    private TextArea description;
    
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    
    @FXML
    private Label descriptionCharCount;
    
    @FXML
    private AnchorPane rootPane;
    
    DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        // Bind the character count label to the length of the description text
        description.textProperty().addListener((observable, oldValue, newValue) -> {
        descriptionCharCount.setText(newValue.length() + "/1000 characters");
        });
        checkData();
    }
    
    @FXML
    void addBook(ActionEvent event) {
        String Title = title.getText();
        String Author = author.getText();
        String Publisher = publisher.getText();
        String ISBN10 = isbn10.getText();
        String ISBN13 = isbn13.getText();
        String Description = description.getText();
        
        if(Title.isEmpty() || Author.isEmpty() || Publisher.isEmpty() || ISBN13.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter the marked fields");
            alert.showAndWait();
            return;
        }
        if(ISBN13.length()!=13){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("ISBN13 must be exactly 13 characters long");
            alert.showAndWait();
            return;
        }
        String qu = "INSERT INTO BOOKS (title, author, publisher, description, isbn10, isbn13) VALUES (?, ?, ?, ?, ?, ?)";
        
        if(dataBaseHandler.execAction(qu, Title, Author, Publisher, Description, ISBN10, ISBN13)){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setHeaderText(null);
            alert.setContentText("Book Saved");
            alert.showAndWait();
        }else{//ERROR
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Book save fail");
            alert.showAndWait();
        }
    }
    
    

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void checkData() {
        String query = "SELECT title FROM BOOKS";
        try(ResultSet rs = dataBaseHandler.execQuery(query)){
            while(rs.next()){
                String title = rs.getString("title");
                System.err.println(title);
            }
        }
        catch(SQLException ex){
            System.err.println("Exception at checkData: AddBookCntroller" + ex.getLocalizedMessage());
        }
    }
}
