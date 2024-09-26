/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apache.librebook;

import com.apache.librebook.database.DataBaseHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

public class ViewBooksController implements Initializable {

    @FXML
    private TableColumn<vBook, String> imageCol;
    @FXML
    private TableColumn<vBook, String> isbnCol;
    @FXML
    private TableColumn<vBook, String> titleCol;
    @FXML
    private TableColumn<vBook, String> authorCol;
    @FXML
    private TableColumn<vBook, String> publisherCol;
    @FXML
    private TableColumn<vBook, Integer> availableCol;
    @FXML
    private TableColumn<vBook, Integer> totalCol;
    @FXML
    private TableView<vBook> bookTableView;
    @FXML
    private TextField searchField; // Search text field

    private ObservableList<vBook> bookList = FXCollections.observableArrayList();
    private FilteredList<vBook> filteredBooks; // Filtered list to handle search

    DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bookTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Initialize the columns with PropertyValueFactory
        imageCol.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availableCol.setCellValueFactory(new PropertyValueFactory<>("availableQuantity"));
        totalCol.setCellValueFactory(new PropertyValueFactory<>("totalQuantity"));
        imageCol.setCellFactory(column -> new ImageViewTableCell());

        // Load data from the database
        loadBooks();

        // Initialize FilteredList with the original bookList
        filteredBooks = new FilteredList<>(bookList, b -> true); // Start with no filters

        // Bind the filtered list to the TableView
        SortedList<vBook> sortedBooks = new SortedList<>(filteredBooks);
        sortedBooks.comparatorProperty().bind(bookTableView.comparatorProperty());

        // Set the items in the TableView
        bookTableView.setItems(sortedBooks);

        // Add a mouse click listener to handle book selection
        bookTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                vBook selectedBook = bookTableView.getSelectionModel().getSelectedItem();
                if (selectedBook != null) {
                    openBookDetails(selectedBook);
                }
            }
        });
    }

    // Load books from the database into bookList
    private void loadBooks() {
        String sql = "SELECT * FROM BOOKS";
        try {
            ResultSet rs = dataBaseHandler.execQuery(sql);
            while (rs.next()) {
                String isbn = rs.getString("ISBN13");
                String title = rs.getString("title");
                String authors = rs.getString("authors");
                String publisher = rs.getString("publisher");
                String imageUrl = rs.getString("imageUrl");
                int totalQuantity = rs.getInt("totalQuantity");
                int availableQuantity = rs.getInt("availableQuantaty");

                // Create a new Book object and add it to the list
                vBook book = new vBook(isbn, title, authors, publisher, totalQuantity, availableQuantity, imageUrl);
                bookList.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Open a new window to display the selected book's details
    private void openBookDetails(vBook book) {
        // Implementation to open the details of the selected book
    }

    // Search books based on title, author, or ISBN
    @FXML
    private void search(ActionEvent event) {
        String searchText = searchField.getText().toLowerCase();

        filteredBooks.setPredicate(book -> {
            // If search text is empty, display all books
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Compare title, author, and ISBN fields with the search text
            String lowerTitle = book.getTitle().toLowerCase();
            String lowerAuthor = book.getAuthor().toLowerCase();
            String lowerIsbn = book.getIsbn().toLowerCase();

            return lowerTitle.contains(searchText) ||
                   lowerAuthor.contains(searchText) ||
                   lowerIsbn.contains(searchText);
        });
    }
    
    @FXML
    private void copyISBN() {
        // Get the selected book
        vBook selectedBook = bookTableView.getSelectionModel().getSelectedItem();
        if (selectedBook != null) {
            // Get the ISBN of the selected book
            String isbn = selectedBook.getIsbn();
            
            // Copy ISBN to clipboard
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(isbn);
            clipboard.setContent(content);
        }
    }
}

