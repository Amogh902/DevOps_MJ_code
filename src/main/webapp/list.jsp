<%@ page import="java.sql.*" %>
<html>
<head>
    <title>Student List</title>
</head>
<body>
<h2>Registered Students</h2>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Name</th>
        <th>Email</th>
    </tr>

<%
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/student_info", "root", "root");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM student");

        while(rs.next()) {
%>
    <tr>
        <td><%= rs.getInt("id") %></td>
        <td><%= rs.getString("name") %></td>
        <td><%= rs.getString("email") %></td>
    </tr>
<%
        }
        rs.close();
        stmt.close();
        conn.close();
    } catch(Exception e) {
        out.println("Error: " + e.getMessage());
    }
%>
</table>

<a href="index.jsp">Add New Student</a>
</body>
</html>

