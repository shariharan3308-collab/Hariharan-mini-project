import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDatabase {
    public static void main(String[] args) {
        String sql = "CREATE DATABASE IF NOT EXISTS billing_db";

        try (Connection con = DBconnections.getServerConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(sql);
            System.out.println("Database 'billing_db' created successfully!");

        } catch (SQLException e) {
            System.out.println("Failed to create database.");
            e.printStackTrace();
        }
    }
}