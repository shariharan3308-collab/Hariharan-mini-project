import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class AllOperation {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        while (true) {
            System.out.println("\n=================================");
            System.out.println("      PRODUCT BILLING SYSTEM");
            System.out.println("=================================");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Generate Bill");
            System.out.println("4. View Bills");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addProduct();
                case 2 -> viewProducts();
                case 3 -> generateBill();
                case 4 -> viewBills();
                case 5 -> {
                    System.out.println("Thank you!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void addProduct() {
        String sql = "INSERT INTO products (product_name, price, stock) VALUES (?, ?, ?)";

        try (Connection con = DBconnections.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            System.out.print("Enter product name: ");
            String name = sc.nextLine();

            System.out.print("Enter price: ");
            double price = sc.nextDouble();

            System.out.print("Enter stock quantity: ");
            int stock = sc.nextInt();
            sc.nextLine();

            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setInt(3, stock);

            if (ps.executeUpdate() > 0)
                System.out.println("Product added successfully!");

        } catch (SQLException e) {
            System.out.println("Error adding product.");
            e.printStackTrace();
        }
    }

    static void viewProducts() {
        String sql = "SELECT * FROM products";

        try (Connection con = DBconnections.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n---------------------------------------------");
            System.out.printf("%-5s %-20s %-10s %-10s%n",
                    "ID", "Product", "Price", "Stock");
            System.out.println("---------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-10.2f %-10d%n",
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("stock"));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing products.");
            e.printStackTrace();
        }
    }

    static void generateBill() {
        System.out.print("Enter product ID: ");
        int productId = sc.nextInt();

        System.out.print("Enter quantity: ");
        int quantity = sc.nextInt();
        sc.nextLine();

        String selectSql =
                "SELECT product_name, price, stock FROM products WHERE product_id = ?";

        String insertBillSql =
                "INSERT INTO bills (product_id, quantity, total) VALUES (?, ?, ?)";

        String updateStockSql =
                "UPDATE products SET stock = stock - ? WHERE product_id = ?";

        try (Connection con = DBconnections.getConnection();
             PreparedStatement select = con.prepareStatement(selectSql)) {

            select.setInt(1, productId);

            try (ResultSet rs = select.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Product not found!");
                    return;
                }

                String name = rs.getString("product_name");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stock");

                if (quantity <= 0 || quantity > stock) {
                    System.out.println("Invalid quantity or insufficient stock!");
                    return;
                }

                double total = price * quantity;

                try (PreparedStatement bill = con.prepareStatement(insertBillSql);
                     PreparedStatement update = con.prepareStatement(updateStockSql)) {

                    bill.setInt(1, productId);
                    bill.setInt(2, quantity);
                    bill.setDouble(3, total);
                    bill.executeUpdate();

                    update.setInt(1, quantity);
                    update.setInt(2, productId);
                    update.executeUpdate();
                }

                System.out.println("\n========== BILL ==========");
                System.out.println("Product  : " + name);
                System.out.printf("Price    : %.2f%n", price);
                System.out.println("Quantity : " + quantity);
                System.out.printf("Total    : %.2f%n", total);
                System.out.println("==========================");
            }

        } catch (SQLException e) {
            System.out.println("Error generating bill.");
            e.printStackTrace();
        }
    }

    static void viewBills() {
        String sql = """
                SELECT b.bill_id, p.product_name, p.price,
                       b.quantity, b.total, b.bill_date
                FROM bills b
                JOIN products p ON b.product_id = p.product_id
                ORDER BY b.bill_id
                """;

        try (Connection con = DBconnections.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            System.out.println("\n----------------------------------------------------------------");
            System.out.printf("%-6s %-20s %-10s %-10s %-12s%n",
                    "Bill", "Product", "Price", "Quantity", "Total");
            System.out.println("----------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-6d %-20s %-10.2f %-10d %-12.2f%n",
                        rs.getInt("bill_id"),
                        rs.getString("product_name"),
                        rs.getDouble("price"),
                        rs.getInt("quantity"),
                        rs.getDouble("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error viewing bills.");
            e.printStackTrace();
        }
    }
}