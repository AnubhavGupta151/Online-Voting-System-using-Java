package com.voting;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    // Updated database name to match the project name
    private static final String URL = "jdbc:mysql://localhost:3306/online_voting";
    private static final String USER = "root";
    private static final String PASSWORD = ""; // Replace with your actual MySQL password

    public static Connection getConnection() {
        try {
            // Ensure the MySQL JDBC Driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establish and return the connection
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}