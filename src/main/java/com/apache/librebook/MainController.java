/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apache.librebook;

import com.apache.librebook.database.DataBaseHandler;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * FXML Controller class
 */
public class MainController implements Initializable {

    private static final int RENEWAL_DAYS = 14;
    private static final Double FINE = 10.0;

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
    @FXML
    private TextField idinfo;
    @FXML
    private Button issueButton;
    @FXML
    private TextField RenewID;
    @FXML
    private TextField RenewISBN;
    @FXML
    private ImageView coverImage1;
    @FXML
    private ListView<String> BookInfoList;
    @FXML
    private ListView<String> MemberInfoList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic if needed
        dataBaseHandler = DataBaseHandler.getInstance();
        checkIssueButtonState();
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
    private int selectedAvailableQuantity = 0;
    private boolean ISBNValid = false;
    private boolean MemberIDValid = false;
    private String selectedISBN = "";
    private String selectedTitle = "";
    private String selectedMemberID = "";
    private String selectedMemberName = "";

    @FXML
    private void issueBook(ActionEvent event) {
        String sql = "SELECT * FROM ISSUES WHERE BookISBN13 = ? AND MemberID = ?";
        try {
            ResultSet rs = dataBaseHandler.execQuery(sql, selectedISBN, selectedMemberID);
            if (rs.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setHeaderText(null);
                alert.setContentText(selectedMemberName + " already has a copy of " + selectedTitle);
                alert.showAndWait();
            } else {
                Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmationAlert.setHeaderText("Confirm Book Issue");
                confirmationAlert.setContentText("Do you want to issue " + selectedTitle + " to " + selectedMemberName + "?");

                // Customize the buttons
                confirmationAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

                // Show the confirmation alert and wait for response
                Optional<ButtonType> result = confirmationAlert.showAndWait();

                if (result.isPresent() && result.get() == ButtonType.YES) {
                    // Proceed to issue the book
                    java.sql.Date issueDate = new java.sql.Date(System.currentTimeMillis());
                    int renewalDays = RENEWAL_DAYS; // Example: 14 days for renewal
                    java.sql.Date returnDate = new java.sql.Date(System.currentTimeMillis() + (renewalDays * 24 * 60 * 60 * 1000));

                    // SQL to insert the issue record into the ISSUES table
                    String insertSql = "INSERT INTO ISSUES (BookISBN13, MemberID, IssueDate, ReturnDate, Fine) VALUES (?, ?, ?, ?, ?)";
                    boolean success1 = dataBaseHandler.execAction(insertSql, selectedISBN, selectedMemberID, issueDate, returnDate, FINE);

                    String updateSql = "UPDATE `books` SET `availableQuantaty`=? WHERE ISBN13 = ?";
                    boolean success2 = dataBaseHandler.execAction(updateSql, selectedAvailableQuantity - 1, selectedISBN);

                    if (success1 && success2) {
                        loadBooksinfo(event);
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText(null);
                        alert.setContentText("Book titled " + selectedTitle + " issued successfully to " + selectedMemberName + "!");
                        alert.showAndWait();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText(null);
                        alert.setContentText("Failed to issue the book.");
                        alert.showAndWait();
                    }
                } else {
                    // User chose "No", you can add any additional logic here if needed
                    Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
                    infoAlert.setHeaderText(null);
                    infoAlert.setContentText("Book issue canceled.");
                    infoAlert.showAndWait();
                }

            }
        } catch (SQLException e) {
            // Handle SQL exceptions
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Database Error");
            alert.setContentText("An error occurred while accessing the database.");
            alert.showAndWait();
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("An unexpected error occurred.");
            alert.showAndWait();
        }
    }

    @FXML
    private void loadBooksinfo(ActionEvent event) {
        //System.out.println("Search is called");
        String sql = "SELECT * FROM BOOKS WHERE ISBN13 = ?";
        try {
            ResultSet rs = dataBaseHandler.execQuery(sql, isbnInfo.getText());
            boolean flag = false;
            while (rs.next()) {
                String isbn = rs.getString("ISBN13");
                selectedISBN = isbn;
                String title = rs.getString("title");
                selectedTitle = title;
                String authors = rs.getString("authors");
                String publisher = rs.getString("publisher");
                String imageUrl = rs.getString("imageUrl");
                int totalQuantity = rs.getInt("totalQuantity");
                int availableQuantity = rs.getInt("availableQuantaty");
                selectedAvailableQuantity = availableQuantity;
                bookInfo.setText("ISBN:" + isbn + "\nTitle" + title + "\nAuthors:" + authors + "\nPublisher" + publisher + "\nTotalQuantatiy: " + Integer.toString(totalQuantity) + "\nAvailableQuantity: " + Integer.toString(availableQuantity));
                coverImage.setImage(image.getImage(imageUrl));
                flag = true;
                if (availableQuantity <= 0) {
                    ISBNValid = false;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Available Quantity is zero");
                    alert.showAndWait();

                } else {
                    ISBNValid = true;
                }

            }
            if (!flag) {
                bookInfo.setText("Book Not Availaible");
                selectedISBN = "";
                selectedTitle = "";
                ISBNValid = false;
                selectedAvailableQuantity = 0;
                checkIssueButtonState();
            } else {
                checkIssueButtonState();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        String sql = "SELECT * FROM MEMBER WHERE ID = ?";
        try {
            ResultSet rs = dataBaseHandler.execQuery(sql, idinfo.getText());
            boolean flag = false;
            while (rs.next()) {

                String id = rs.getString("ID");
                selectedMemberID = id;
                String name = rs.getString("name");
                selectedMemberName = name;
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String address = rs.getString("address");

                memberInfo.setText("ID: " + id + "\nName: " + name + "\nEmail: " + email + "\nPhone: " + phone + "\nAddress: " + address);

                flag = true;
                MemberIDValid = true;
            }
            if (!flag) {
                memberInfo.setText("Not a Member");
                selectedMemberID = "";
                selectedMemberName = "";
                MemberIDValid = false;
                checkIssueButtonState();
            } else {
                checkIssueButtonState();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void checkIssueButtonState() {
        if (MemberIDValid && ISBNValid) {
            issueButton.setDisable(false); // Enable the button if both have values
        } else {
            issueButton.setDisable(true); // Disable the button if either is empty
        }
    }

    @FXML
    private void RenewSearch(ActionEvent event) {
        String isbn = RenewISBN.getText();
        String id = RenewID.getText();
        String sql = "SELECT * FROM ISSUES WHERE BookISBN13 = ? AND MemberID = ?";

        try {
            ResultSet rs = dataBaseHandler.execQuery(sql, isbn, id);
            if (rs.next()) {
                // If a record is found, extract the relevant data
                String bookISBN = rs.getString("BookISBN13");
                String memberID = rs.getString("MemberID");
                java.sql.Date issueDate = rs.getDate("IssueDate");
                java.sql.Date returnDate = rs.getDate("ReturnDate");
                double fine = rs.getDouble("FINE");

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Customize the format if needed
                String formattedIssueDate = dateFormat.format(issueDate);
                String formattedReturnDate = dateFormat.format(returnDate);

                String booksql = "SELECT * FROM BOOKS WHERE ISBN13 = ?";

                ResultSet rs1 = dataBaseHandler.execQuery(booksql, bookISBN);
                rs1.next();

                String bookTitle = rs1.getString("title");
                String authors = rs1.getString("authors");
                String publisher = rs1.getString("publisher");
                String imageUrl = rs1.getString("imageUrl");
                int totalQuantity = rs1.getInt("totalQuantity");
                int availableQuantity = rs1.getInt("availableQuantaty");

                coverImage1.setImage(image.getImage(imageUrl));

                BookInfoList.getItems().clear();
                BookInfoList.getItems().add("ISBN:" + bookISBN);
                BookInfoList.getItems().add("Title" + bookTitle);
                BookInfoList.getItems().add("Authors" + authors);
                BookInfoList.getItems().add("Publisher" + publisher);
                BookInfoList.getItems().add("Total Quantity" + Integer.toString(totalQuantity));
                BookInfoList.getItems().add("Available Quantity" + Integer.toString(availableQuantity));

                String membersql = "SELECT * FROM MEMBER WHERE ID = ?";
                ResultSet rs2 = dataBaseHandler.execQuery(membersql, memberID);
                rs2.next();
                String name = rs2.getString("name");
                String email = rs2.getString("email");
                String phone = rs2.getString("phone");
                String address = rs2.getString("address");

                MemberInfoList.getItems().clear();

                MemberInfoList.getItems().add("ID: " + id);
                MemberInfoList.getItems().add("Name: " + name);
                MemberInfoList.getItems().add("Email: " + email);
                MemberInfoList.getItems().add("Phone: " + phone);
                MemberInfoList.getItems().add("Address: " + address);

                java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
                long differenceInMillis = currentDate.getTime() - returnDate.getTime();
                long daysOverdue = differenceInMillis / (1000 * 60 * 60 * 24);

                MemberInfoList.getItems().add("Issue Date: " + formattedIssueDate);
                MemberInfoList.getItems().add("Return Date: " + formattedReturnDate);
                

                MemberInfoList.getItems().add("Days overdue: " + Long.toString(daysOverdue));

                Double displayFine = calculateFine(returnDate, fine);
                MemberInfoList.getItems().add("Fine to be Paid: " + Double.toString(displayFine));

            } else {
                // Show an alert if no matching record is found
                MemberInfoList.getItems().clear();
                BookInfoList.getItems().clear();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("No Such Records Found");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Database Error");
            alert.setContentText("An error occurred while accessing the database.");
            alert.showAndWait();
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Error");
            alert.setContentText("An unexpected error occurred.");
            alert.showAndWait();
        }
    }

    private double calculateFine(java.sql.Date returnDate, double fine) {
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());

        // Calculate the number of days overdue
        long differenceInMillis = currentDate.getTime() - returnDate.getTime();
        long daysOverdue = differenceInMillis / (1000 * 60 * 60 * 24); // Convert to days

        if (daysOverdue > 0) {
            return daysOverdue * fine; // Calculate fine
        } else {
            return 0; // No fine if returned on time
        }
    }

}
