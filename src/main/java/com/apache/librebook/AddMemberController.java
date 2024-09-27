/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.apache.librebook;

import com.apache.librebook.database.DataBaseHandler;
import java.net.URL;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bhavy
 */
public class AddMemberController implements Initializable {

    @FXML
    private TextField id;
    @FXML
    private TextField name;
    @FXML
    private TextField email;
    @FXML
    private TextField phone;
    @FXML
    private TextArea address;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    @FXML
    private AnchorPane rootPane;

    /**
     * Initializes the controller class.
     */
    DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void addBook(ActionEvent event) {
        String mId = id.getText();
        String mName = name.getText();
        String mEmail = email.getText();
        String mPhone = phone.getText();
        String mAddress = address.getText();

        if (mId.isEmpty() || mName.isEmpty() || mEmail.isEmpty() || mAddress.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please enter the marked fields");
            alert.showAndWait();
            return;
        }

        String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        Pattern pattern = Pattern.compile(EMAIL_REGEX);

        Matcher matcher = pattern.matcher(mEmail);

        if (!matcher.matches()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Invalid Email format");
            alert.showAndWait();
            return;
        }

        try {
            String sqlcheck1 = "SELECT * FROM `member` WHERE ID = ?";
            ResultSet rs = dataBaseHandler.execQuery(sqlcheck1, mId);
            if (rs.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("ID already exist");
                alert.showAndWait();
                return;
            }
            String sqlcheck2 = "SELECT * FROM `member` WHERE email = ?";
            ResultSet rs1 = dataBaseHandler.execQuery(sqlcheck2, mEmail);
            if (rs1.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Email in use by other member");
                alert.showAndWait();
                return;
            }
            String sqlcheck3 = "SELECT * FROM `member` WHERE phone = ?";
            ResultSet rs2 = dataBaseHandler.execQuery(sqlcheck3, mPhone);
            if (rs2.next()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Phone in use by other member");
                alert.showAndWait();
                return;
            }

        } catch (Exception e) {
        }

        String insertQuery = "INSERT INTO `member`(`ID`, `name`, `email`, `phone`, `address`) VALUES ( ?, ?, ?, ?, ?)";

        if (dataBaseHandler.execAction(insertQuery, mId, mName, mEmail, mPhone, mAddress)) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Member Saved");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Save failed");
            alert.showAndWait();
        }

    }

    @FXML
    private void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

}
