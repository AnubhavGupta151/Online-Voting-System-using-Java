package com.voting;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uniqueId = request.getParameter("uniqueId");
        String password = request.getParameter("password");

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                response.setContentType("text/html");
                response.getWriter().println("<h3>Database connection failed. Please try again later.</h3>");
                return;
            }

            //Check user credentials
            String query = "SELECT * FROM users WHERE unique_id = ? AND password = ?";
            try (PreparedStatement ps = con.prepareStatement(query)) {
                ps.setString(1, uniqueId);
                ps.setString(2, password);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // Store uniqueId in session after successful login
                        HttpSession session = request.getSession();
                        session.setAttribute("uniqueId", uniqueId);

                        //Redirect to voting page
                        response.sendRedirect("voting.html");
                    } else {
                        // Invalid credentials
                        response.setContentType("text/html");
                        response.getWriter().println("<h3>Invalid credentials. Please try again.</h3>");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.setContentType("text/html");
            response.getWriter().println("<h3>An error occurred. Please try again later.</h3>");
        }
    }
}
