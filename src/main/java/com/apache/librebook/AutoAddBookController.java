/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apache.librebook;

import com.apache.librebook.AddBookControllerD.BookListCell;
import com.apache.librebook.AddBookControllerD.Book;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * FXML Controller class
 *
 * @author bhavy
 */
public class AutoAddBookController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private ListView<Book> listView;
    @FXML
    private Label pageLabel;
    
    private GoogleBooksApiService googleBooksApiService = new GoogleBooksApiService();
    private ObservableList<Book> books = FXCollections.observableArrayList();
    private int startIndex = 0;
    private static final int PAGE_SIZE = 40; // Number of books to load each time
    private boolean isLoading = false; // Flag to prevent multiple loads

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set custom cell factory for ListView
        listView.setCellFactory(param -> new BookListCell());
    }

    @FXML
    private void search(ActionEvent event) throws Exception {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            startIndex = 0; // Reset start index
            books.clear(); // Clear previous results
            loadBooks(query); // Load initial results
            System.out.println(googleBooksApiService.getTotalItems());
        }
    }

    private void loadBooks(String query) {
        isLoading = true; // Set loading flag
        try {
            books.clear();
            ObservableList<Book> newBooks = FXCollections.observableArrayList(googleBooksApiService.searchBooks(query, startIndex, PAGE_SIZE));
            books.addAll(newBooks);
            listView.setItems(books);
            //startIndex += PAGE_SIZE; // Increment the start index for next load
        } catch (Exception e) {
            e.printStackTrace(); // Handle the exception appropriately
        } finally {
            isLoading = false; // Reset loading flag
        }
    }

    private void loadMoreBooks() {
        if (searchField.getText() != null && !searchField.getText().isEmpty()) {
            loadBooks(searchField.getText());
        }
    }

    @FXML
    private void PreviousPage(ActionEvent event) {
        if(startIndex!=0){
            startIndex -= PAGE_SIZE;;
            loadMoreBooks();
            pageLabel.setText("Page "+(startIndex/PAGE_SIZE+1));
        }
    }

    @FXML
    private void nextPage(ActionEvent event) {
        if ((startIndex + PAGE_SIZE)<googleBooksApiService.getTotalItems()) {
            startIndex += PAGE_SIZE;
            loadMoreBooks();
            pageLabel.setText("Page " + (startIndex / PAGE_SIZE + 1));
        }
    }
}
