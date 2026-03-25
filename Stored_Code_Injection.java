package com.example.vulnerable.codeinjection;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

/**
 * VULNERABLE CODE SAMPLE
 * Demonstrates: Stored_Code_Injection
 * 
 * This code will be DETECTED by CxSAST because it:
 * 1. Reads data from persistent storage (Database)
 * 2. Passes that data to ScriptEngine.eval()
 * 
 * VULNERABILITY: Stored code from database is executed via ScriptEngine.eval
 */
@WebServlet("/execute-tasks")
public class Stored_Code_Injection_Sample extends HttpServlet {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/taskdb";
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "password";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // User input - store to database
        String scriptCode = request.getParameter("script");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            String insertSQL = "INSERT INTO scripts (code) VALUES (?)";
            PreparedStatement pstmt = conn.prepareStatement(insertSQL);
            pstmt.setString(1, scriptCode);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.sendRedirect("/execute-tasks");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        out.println("<html><body>");
        out.println("<h2>Stored Scripts</h2>");

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            
            // STEP 1: Read from persistent storage (Database)
            String selectSQL = "SELECT code FROM scripts";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(selectSQL);

            // STEP 2: Execute stored code via ScriptEngine.eval
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");

            while (rs.next()) {
                String storedCode = rs.getString("code");
                out.println("<p>Executing: " + storedCode + "</p>");

                // VULNERABILITY: This is the critical sink
                // Data from database flows to ScriptEngine.eval
                try {
                    engine.eval(storedCode);
                } catch (ScriptException e) {
                    out.println("<p>Error: " + e.getMessage() + "</p>");
                }
            }

        } catch (SQLException e) {
            out.println("<p>Database error: " + e.getMessage() + "</p>");
        }

        out.println("<form method='post'>");
        out.println("<input type='text' name='script' placeholder='Enter script code' />");
        out.println("<input type='submit' value='Store Script' />");
        out.println("</form>");

        out.println("</body></html>");
    }
}
