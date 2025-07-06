package com.voting;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class VoteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the uniqueId from session instead of request parameter
        HttpSession session = request.getSession();
        String uniqueId = (String) session.getAttribute("uniqueId");
        String candidate = request.getParameter("candidate");

        response.setContentType("text/html");

        // If uniqueId is null (i.e., user is not logged in) or candidate is empty
        if (uniqueId == null || uniqueId.isEmpty() || candidate == null || candidate.isEmpty()) {
            response.getWriter().println("<h3>Invalid input. Please ensure all fields are filled.</h3>");
            response.getWriter().println("<a href='voting.html'>Go back to voting page</a>");
            return;
        }

        try (Connection con = DBConnection.getConnection()) {
            if (con == null) {
                response.getWriter().println("<h3>Database connection failed. Please try again later.</h3>");
                return;
            }

            // Check if the user has already voted for any candidate
            String checkVoteQuery = "SELECT candidate_name FROM votes WHERE unique_id = ?";
            try (PreparedStatement checkVotePs = con.prepareStatement(checkVoteQuery)) {
                checkVotePs.setString(1, uniqueId);
                try (ResultSet rs = checkVotePs.executeQuery()) {
                    if (rs.next()) {
                        String votedCandidate = rs.getString("candidate_name");
                        if (!votedCandidate.equals(candidate)) {
                            response.getWriter().println("<h3>You have already voted for " + votedCandidate + ". You cannot vote again.</h3>");
                            return;
                        } else {
                            response.getWriter().println("<h3>You have already voted for this candidate.</h3>");
                            return;
                        }
                    }
                }
            }

            // Insert vote into votes table (only if they haven't voted)
            String insertVoteQuery = "INSERT INTO votes (unique_id, candidate_name) VALUES (?, ?)";
            try (PreparedStatement insertVotePs = con.prepareStatement(insertVoteQuery)) {
                insertVotePs.setString(1, uniqueId);
                insertVotePs.setString(2, candidate);
                insertVotePs.executeUpdate();
            }

            // Update vote count in candidates table
            String updateCandidateQuery = "UPDATE candidates SET votes = votes + 1 WHERE candidate_name = ?";
            try (PreparedStatement updateCandidatePs = con.prepareStatement(updateCandidateQuery)) {
                updateCandidatePs.setString(1, candidate);
                int rowsUpdated = updateCandidatePs.executeUpdate();

                if (rowsUpdated > 0) {
                    response.getWriter().println("<h3>Your vote has been recorded successfully.</h3>");
                } else {
                    response.getWriter().println("<h3>Voting failed. Candidate not found.</h3>");
                    return;
                }
            }

            // Fetch the latest vote counts and store them in session for results.jsp
            String getVotesQuery = "SELECT candidate_name, votes FROM candidates";
            try (PreparedStatement getVotesPs = con.prepareStatement(getVotesQuery);
                 ResultSet rs = getVotesPs.executeQuery()) {

                StringBuilder resultTable = new StringBuilder();
                resultTable.append("<table border='1'><tr><th>Candidate</th><th>Votes</th></tr>");

                while (rs.next()) {
                    resultTable.append("<tr><td>")
                            .append(rs.getString("candidate_name"))
                            .append("</td><td>")
                            .append(rs.getInt("votes"))
                            .append("</td></tr>");
                }
                resultTable.append("</table>");

                // Store the results table in session
                session.setAttribute("resultsTable", resultTable.toString());
            }

            // Redirect to results page to show updated vote counts
            response.sendRedirect("results.jsp");

        } catch (Exception e) {  // Corrected placement of catch block
            e.printStackTrace();
            response.getWriter().println("<h3>An error occurred. Please try again later.</h3>");
        }
    }
}
