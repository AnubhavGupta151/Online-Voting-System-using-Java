<%@ page import="java.sql.*" %>
<%@ page import="com.voting.DBConnection" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Election Results</title>
    <link rel="stylesheet" href="css/styles.css">
</head>
<body>
    <h2>Election Results</h2>

    <table border="1">
        <tr>
            <th>Candidate</th>
            <th>Votes</th>
        </tr>

        <%
            try (Connection con = DBConnection.getConnection()) {
                if (con == null) {
                    out.println("<h3>Database connection failed.</h3>");
                } else {
                    String query = "SELECT candidate_name, COUNT(*) AS votes FROM votes GROUP BY candidate_name";
                    try (PreparedStatement ps = con.prepareStatement(query);
                         ResultSet rs = ps.executeQuery()) {

                        while (rs.next()) {
                            String candidateName = rs.getString("candidate_name");
                            int votes = rs.getInt("votes");
        %>
        <tr>
            <td><%= candidateName %></td>
            <td><%= votes %></td>
        </tr>
        <%
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                out.println("<h3>Error fetching results.</h3>");
            }
        %>
    </table>

    <br>
    <a href="login.html">Back to Admin Dashboard</a>
</body>
</html>
