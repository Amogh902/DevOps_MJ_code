package com.example;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;

public class StudentServlet extends HttpServlet {
    // Update these with your RDS details
    private static final String JDBC_URL = "jdbc:mysql://mydb.csj0yw6ui35x.us-east-1.rds.amazonaws.com:3306/student_info?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String JDBC_USER = "admin";        // RDS username
    private static final String JDBC_PASSWORD = "admin12345"; // RDS password

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            String sql = "INSERT INTO student (name, email) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.executeUpdate();

            stmt.close();
            conn.close();

            response.sendRedirect("list.jsp");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Error: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect("index.jsp");
    }
}

