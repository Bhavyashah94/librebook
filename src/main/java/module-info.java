module com.apache.librebook {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;
    requires java.sql;
    requires json;
    
    opens com.apache.librebook to javafx.fxml;
    exports com.apache.librebook;
    
    opens com.apache.librebook.AddBookControllerD to javafx.fxml;
}
