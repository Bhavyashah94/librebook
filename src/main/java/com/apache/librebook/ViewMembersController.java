package com.apache.librebook;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
import com.apache.librebook.database.DataBaseHandler;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 * FXML Controller class for viewing members
 */
public class ViewMembersController implements Initializable {

    @FXML
    private TableColumn<vMember, String> idCol;
    @FXML
    private TableColumn<vMember, String> nameCol;
    @FXML
    private TableColumn<vMember, String> emailCol;
    @FXML
    private TableColumn<vMember, String> phoneCol;
    @FXML
    private TableColumn<vMember, String> addressCol;
    @FXML
    private TextField searchField;
    @FXML
    private TableView<vMember> memberTableView;

    private ObservableList<vMember> memberList = FXCollections.observableArrayList();
    private FilteredList<vMember> filteredMembers;

    DataBaseHandler dataBaseHandler = DataBaseHandler.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set column resize policy
        memberTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Bind table columns to vMember properties
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("mail"));
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

        // Load data from the database
        loadMembers();

        // Initialize FilteredList with the original memberList
        filteredMembers = new FilteredList<>(memberList, b -> true);

        // Wrap the FilteredList in a SortedList
        SortedList<vMember> sortedMembers = new SortedList<>(filteredMembers);
        sortedMembers.comparatorProperty().bind(memberTableView.comparatorProperty());

        // Bind the sorted list to the TableView
        memberTableView.setItems(sortedMembers);

        memberTableView.setOnMouseClicked((MouseEvent event) -> {
            if (event.getClickCount() == 2) {
                vMember selectedMember = memberTableView.getSelectionModel().getSelectedItem();
                if (selectedMember != null) {
                    openMemberDetails(selectedMember);
                }
            }
        });
    }

    // Load members from the database into memberList
    private void loadMembers() {
        memberList.clear();
        String sql = "SELECT * FROM MEMBER";
        try {
            ResultSet rs = dataBaseHandler.execQuery(sql);
            while (rs.next()) {
                String id = rs.getString("ID");
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                String phone = rs.getString("Phone");
                String address = rs.getString("Address");

                // Create a new vMember object and add it to the list
                vMember member = new vMember(id, name, email, phone, address);
                memberList.add(member);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Implement search functionality
    @FXML
    private void search(ActionEvent event) {
        
        String searchText = searchField.getText().toLowerCase();

        filteredMembers.setPredicate(member -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Compare id, name, email, phone, and address with the search text
            String lowerId = member.getId().toLowerCase();
            String lowerName = member.getName().toLowerCase();
            String lowerEmail = member.getMail().toLowerCase();
            String lowerPhone = member.getPhone().toLowerCase();
            String lowerAddress = member.getAddress().toLowerCase();

            return lowerId.contains(searchText)
                    || lowerName.contains(searchText)
                    || lowerEmail.contains(searchText)
                    || lowerPhone.contains(searchText)
                    || lowerAddress.contains(searchText);
        });
    }

    private void openMemberDetails(vMember selectedMember) {

    }

    @FXML
    private void delete(ActionEvent event) {

        vMember selectedMember = memberTableView.getSelectionModel().getSelectedItem();
        if (selectedMember != null) {
            String ID = selectedMember.getId();
            try {
                String sql = "SELECT * FROM ISSUES WHERE MemberID = ?";
                ResultSet rs = dataBaseHandler.execQuery(sql, ID);

                if (rs.next()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Cannot delete the Member, They have books");
                    alert.showAndWait();
                    return;
                }

                String deletesql = "DELETE FROM `Member` WHERE ID = ?";
                boolean success = dataBaseHandler.execAction(deletesql, ID);

                if (success) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("Member Deleted");
                    alert.showAndWait();
                    loadMembers();
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setHeaderText(null);
                    alert.setContentText("Member delete failed");
                    alert.showAndWait();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }
}
