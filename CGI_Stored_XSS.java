import java.sql.*;
import java.util.Properties;

public class CGI_Stored_XSS_Sample {
    
    public static void main(String[] args) {
        try {
            Properties p = System.getProperties();
            String queryString = p.getProperty("QUERY_STRING");
            
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/testdb", "user", "password");
            
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT comment FROM comments");
            
            System.out.println("Content-type:text/html\r\n\r\n");
            System.out.println("<html><body>");
            System.out.println("<h2>User Comments</h2>");
            
            while (rs.next()) {
                String comment = rs.getString("comment");
                
                // ✅ FIX: Escape HTML before output
                String safeComment = escapeHtml(comment);
                
                System.out.println("<p>" + safeComment + "</p>");
            }
            
            System.out.println("</body></html>");
            
            rs.close();
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.out.println("Content-type:text/html\r\n\r\n");
            System.out.println("Error: " + escapeHtml(e.getMessage()));
        }
    }

    // Simple HTML escaping function
    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }
}

