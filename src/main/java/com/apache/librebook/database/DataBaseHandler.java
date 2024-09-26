package com.apache.librebook.database;

/**
 *
 * @author bhavy
 */

import java.sql.*;

public class DataBaseHandler {
    
    private static DataBaseHandler handle = null;
    
    private static final String DB_URL = "jdbc:mysql://localhost/librebooks";
    private static final String USER = "root";
    private static final String PASS = "";
    private Connection conn = null;
    private Statement stmt = null;
    
    private DataBaseHandler(){
       createConnection();
       setupBookTable();
       setupMemberTable();
    }
    
    public static DataBaseHandler getInstance() {
        if (handle == null) {
            handle = new DataBaseHandler();
        }
        return handle;
    }
    
    public void createConnection() {
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Connection established successfully.");
        } catch (SQLException e) {
            System.err.println("Error while connecting to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    void setupBookTable() {
        try {
            // Check if the table exists
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet tables = dbMetaData.getTables(null, null, "BOOKS", null);

            if (tables.next()) {
                System.out.println("Table already exists.");
            } else {
                // Table doesn't exist, so create it
                String sql = "CREATE TABLE BOOKs " +
                             "(title VARCHAR(500), " +
                             " authors VARCHAR(500), " +
                             " publisher VARCHAR(500), " +
                             " description VARCHAR(10000)," +
                             " imageUrl VARCHAR(1000)," +
                             " ISBN13 VARCHAR(13)," +
                             " PTR DOUBLE," +
                             " MRP DOUBLE," +
                             " totalQuantity INT," +
                             " availableQuantaty INT," +
                             " PRIMARY KEY (ISBN13)) ";
                stmt = conn.createStatement(); // Initialize statement
                stmt.executeUpdate(sql);
                System.out.println(" Books table created.");
            }
        } catch (SQLException e) {
            System.err.println("Error during setupBookTable: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources if they were opened
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    void setupMemberTable() {
        try {
            // Check if the table exists
            DatabaseMetaData dbMetaData = conn.getMetaData();
            ResultSet tables = dbMetaData.getTables(null, null, "MEMBER", null);

            if (tables.next()) {
                System.out.println("Table already exists.");
            } else {
                // Table doesn't exist, so create it
                String sql = "CREATE TABLE MEMBER " +
                             "(ID VARCHAR(50), " +
                             " name VARCHAR(200), " +
                             " email VARCHAR(200), " +
                             " phone VARCHAR(20)," +
                             " address VARCHAR(1000)," +
                             " PRIMARY KEY (ID)) ";
                stmt = conn.createStatement(); // Initialize statement
                stmt.executeUpdate(sql);
                System.out.println("Member table created.");
            }
        } catch (SQLException e) {
            System.err.println("Error during setMemberTable: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources if they were opened
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    public ResultSet execQuery(String query, Object... params) {
        ResultSet result = null;
        try {
            PreparedStatement pstmt = conn.prepareStatement(query);
            // Set parameters
            for (int i = 0; i < params.length; i++) {
                pstmt.setObject(i + 1, params[i]);
            }
            result = pstmt.executeQuery();
        } catch (SQLException ex) {
            System.err.println("Exception at execQuery: DatabaseHandler - " + ex.getLocalizedMessage());
        }
        return result; // Be cautious: caller needs to close it properly!
    }
    
    public boolean execAction(String qu, Object... params) { //params 0,1,2,3,4,
        boolean success = false;

        try (PreparedStatement pstmt = conn.prepareStatement(qu)) {
            // Bind the parameters dynamically
            for (int i = 0; i < params.length; i++) {
               pstmt.setObject(i + 1, params[i]); // Bind parameter (index starts at 1 for SQL)
            }

            // Execute the query (INSERT, UPDATE, DELETE)
            int rowsAffected = pstmt.executeUpdate();
            success = rowsAffected > 0; // If rows were affected, the execution was successful
        } catch (SQLException ex) {
           System.out.println("Exxeption at execAction: DataBaseHandler - "+ ex.getLocalizedMessage());
        }

    return success;
    }
    
}
