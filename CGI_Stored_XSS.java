import java.sql.*;
import java.util.Properties;

/**
 * Vulnerable CGI Application demonstrating Stored XSS
 * This sample should be detected by CxSAST CGI_Stored_XSS query
 */
public class CGI_Stored_XSS_Sample {
    
    public static void main(String[] args) {
        try {
            // CGI Detection Pattern 1: Access CGI environment variable via System.getProperty
            Properties p = System.getProperties();
            String queryString = p.getProperty("QUERY_STRING");
            
            // Database connection
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/testdb", "user", "password");
            
            // Read stored data from database (Source)
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT comment FROM comments");
            
            // CGI Output: Content-type header
            System.out.println("Content-type:text/html\r\n\r\n");
            System.out.println("<html><body>");
            System.out.println("<h2>User Comments</h2>");
            
            // VULNERABILITY: Stored XSS - Data from database printed directly to console output
            while (rs.next()) {
                String comment = rs.getString("comment");
                // Vulnerable sink - console output in CGI context
                System.out.println("<p>" + comment + "</p>");
            }
            
            System.out.println("</body></html>");
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Content-type:text/html\r\n\r\n");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
