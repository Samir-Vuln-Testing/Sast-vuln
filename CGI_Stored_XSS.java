import java.sql.*;

public class CGI_Stored_XSS_Sample {

    public static void main(String[] args) {
        try {
            String dbUrl = "DB_URL";
            String dbUser = "DB_USER";
            String dbPassword = "DB_PASSWORD";

            System.out.println("Content-type:text/html\r\n\r\n");
            System.out.println("<html><body>");
            System.out.println("<h2>User Comments</h2>");

            try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT comment FROM comments")) {

                while (rs.next()) {
                    String safeComment = escapeHtml(rs.getString("comment"));
                    System.out.println("<p>" + safeComment + "</p>");
                }
            }

            System.out.println("</body></html>");

        } catch (Exception e) {
            System.out.println("Content-type:text/html\r\n\r\n");
            System.out.println("An error occurred. Please try again later.");
        }
    }

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
