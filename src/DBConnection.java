import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // For development: allow trusting server certificate (use only on dev environment)
    private static final String URL = "jdbc:sqlserver://localhost:1434;databaseName=QuanLySach;encrypt=true;trustServerCertificate=true;integratedSecurity=true;";
    private static final String USER = "";   // bỏ trống
    private static final String PASSWORD = ""; // bỏ trống

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Microsoft SQL Server JDBC driver not found on classpath. Add the mssql-jdbc JAR to your project's lib folder.", e);
        }
        if (USER != null && !USER.isEmpty()) {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
        return DriverManager.getConnection(URL);
    }
}
