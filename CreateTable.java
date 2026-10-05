import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTable {
    public static void main(String[] args) {
        String productSql = """
                CREATE TABLE IF NOT EXISTS products (
                    product_id INT PRIMARY KEY AUTO_INCREMENT,
                    product_name VARCHAR(100) NOT NULL,
                    price DECIMAL(10,2) NOT NULL,
                    stock INT NOT NULL
                )
                """;

        String billSql = """
                CREATE TABLE IF NOT EXISTS bills (
                    bill_id INT PRIMARY KEY AUTO_INCREMENT,
                    product_id INT NOT NULL,
                    quantity INT NOT NULL,
                    total DECIMAL(10,2) NOT NULL,
                    bill_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (product_id) REFERENCES products(product_id)
                )
                """;

        try (Connection con = DBconnections.getConnection();
             Statement stmt = con.createStatement()) {

            stmt.executeUpdate(productSql);
            stmt.executeUpdate(billSql);

            System.out.println("Products and bills tables created successfully!");

        } catch (SQLException e) {
            System.out.println("Failed to create tables.");
            e.printStackTrace();
        }
    }
}