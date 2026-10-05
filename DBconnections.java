import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnections {

    private static final String SERVER_URL =
            "jdbc:mysql://localhost:3306";

    private static final String DB_URL =
            "jdbc:mysql://localhost:3306/billing_db";

    private static final String USER = "root";

    private static String getPasswordFromEnv() {
        try {
            for (String line : Files.readAllLines(Path.of(".env"))) {
                line = line.trim();
                if (line.startsWith("DB_PASSWORD=")) {
                    return line.substring("DB_PASSWORD=".length()).trim();
                }
            }
        } catch (IOException e) {
            System.out.println("Could not read .env file.");
        }
        return null;
    }

    public static Connection getServerConnection() throws SQLException {
        String password = getPasswordFromEnv();

        if (password == null || password.isBlank()) {
            throw new SQLException("DB_PASSWORD not found in .env");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found!", e);
        }

        return DriverManager.getConnection(SERVER_URL, USER, password);
    }

    public static Connection getConnection() throws SQLException {
        String password = getPasswordFromEnv();

        if (password == null || password.isBlank()) {
            throw new SQLException("DB_PASSWORD not found in .env");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found!", e);
        }

        return DriverManager.getConnection(DB_URL, USER, password);
    }

    public static void main(String[] args) {
        try (Connection con = getServerConnection()) {
            System.out.println("MySQL connection successful!");
        } catch (SQLException e) {
            System.out.println("MySQL connection failed!");
            e.printStackTrace();
        }
    }
}