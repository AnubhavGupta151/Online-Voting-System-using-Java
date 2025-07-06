// package com.voting;

// import java.io.IOException;
// import java.sql.Connection;
// import java.sql.PreparedStatement;
// import java.sql.ResultSet;
// import java.util.Random;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.annotation.WebServlet;
// import jakarta.servlet.http.HttpServlet;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;

// // @WebServlet("/register")
// public class RegisterServlet extends HttpServlet {
//     private static final long serialVersionUID = 1L;

//     @Override
//     protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//         String name = request.getParameter("name");
//         String ageParam = request.getParameter("age");
//         String email = request.getParameter("email");
//         String password = request.getParameter("password");

//         response.setContentType("text/html");

//         // Validate input
//         if (name == null || name.isEmpty() || ageParam == null || email == null || password == null ||
//                 name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty()) {
//             response.getWriter().println("<h3>All fields are required. Please try again.</h3>");
//             return;
//         }

//         int age;
//         try {
//             age = Integer.parseInt(ageParam);
//         } catch (NumberFormatException e) {
//             response.getWriter().println("<h3>Invalid age. Please enter a valid number.</h3>");
//             return;
//         }

//         if (age < 18) {
//             response.getWriter().println("<h3>Age must be 18 or above to register.</h3>");
//             return;
//         }

//         try (Connection con = DBConnection.getConnection()) {
//             // Ensure the connection is not null
//             if (con == null) {
//                 response.getWriter().println("<h3>Database connection failed. Please try again later.</h3>");
//                 return;
//             }

//             // Generate a unique 5-digit ID
//             Random rand = new Random();
//             int uniqueId;
//             boolean exists;

//             do {
//                 uniqueId = 10000 + rand.nextInt(90000); // Generates a 5-digit number (10000 to 99999)

//                 // Check if uniqueId already exists in the database
//                 String checkQuery = "SELECT * FROM users WHERE unique_id = ?";
//                 try (PreparedStatement checkPs = con.prepareStatement(checkQuery)) {
//                     checkPs.setInt(1, uniqueId);
//                     try (ResultSet rs = checkPs.executeQuery()) {
//                         exists = rs.next(); // If true, regenerate a new ID
//                     }
//                 }
//             } while (exists);

//             // Insert new user into the database
//             String insertQuery = "INSERT INTO users (name, age, email, unique_id, password) VALUES (?, ?, ?, ?, ?)";
//             try (PreparedStatement insertPs = con.prepareStatement(insertQuery)) {
//                 insertPs.setString(1, name);
//                 insertPs.setInt(2, age);
//                 insertPs.setString(3, email);
//                 insertPs.setInt(4, uniqueId);
//                 insertPs.setString(5, password);

//                 int rows = insertPs.executeUpdate();
//                 if (rows > 0) {
//                     response.getWriter().println("<h3>Registration Successful! Your Unique ID: <strong>" + uniqueId + "</strong></h3>");
//                     response.getWriter().println("<a href='login.html'>Go to Login</a>");
//                 } else {
//                     response.getWriter().println("<h3>Registration failed. Please try again.</h3>");
//                 }
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
//             response.getWriter().println("<h3>An error occurred. Please try again later.</h3>");
//         }
//     }
// }
package com.voting;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// @WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String ageParam = request.getParameter("age");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String uniqueIdParam = request.getParameter("unique_id");

        response.setContentType("text/html");

        // Validate input
        if (name == null || name.isEmpty() || ageParam == null || email == null || password == null || uniqueIdParam == null ||
                name.trim().isEmpty() || email.trim().isEmpty() || password.trim().isEmpty() || uniqueIdParam.trim().isEmpty()) {
            response.getWriter().println("<h3>All fields are required. Please try again.</h3>");
            return;
        }

        int age;
        int uniqueId;
        try {
            age = Integer.parseInt(ageParam);
            uniqueId = Integer.parseInt(uniqueIdParam);
        } catch (NumberFormatException e) {
            response.getWriter().println("<h3>Invalid input. Please enter valid numbers.</h3>");
            return;
        }

        if (age < 18) {
            response.getWriter().println("<h3>Age must be 18 or above to register.</h3>");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                response.getWriter().println("<h3>Database connection failed. Please try again later.</h3>");
                return;
            }

            // Insert new user into the database
            String insertQuery = "INSERT INTO users (name, age, email, unique_id, password) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertPs = con.prepareStatement(insertQuery)) {
                insertPs.setString(1, name);
                insertPs.setInt(2, age);
                insertPs.setString(3, email);
                insertPs.setInt(4, uniqueId);
                insertPs.setString(5, password);

                int rows = insertPs.executeUpdate();
                if (rows > 0) {
                    response.getWriter().println("<h3>Registration Successful! <a href='login.html'>Go to Login</a></h3>");
                } else {
                    response.getWriter().println("<h3>Registration failed. Please try again.</h3>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("<h3>An error occurred. Please try again later.</h3>");
        }
    }
}
