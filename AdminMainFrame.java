import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminMainFrame extends JFrame {

    // Panels
    public AdminOverview adminOverview;
    public AdminInventory adminInventory;
    public AdminReports adminReports;
    public AdminSettings adminSettings;

    public List<Ingredient> inventory; // Shared inventory
    private JPanel mainPanel;

    public AdminMainFrame() {
        setTitle("Ingredient Inventory Management System - Admin");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===================== Initialize Inventory =====================
        inventory = new ArrayList<>();
        
        inventory.add(new Ingredient("Eggs", "Poultry", 6, 10, "pcs", LocalDate.of(2025, 12, 1)));
        inventory.add(new Ingredient("Milk", "Dairy", 12, 10, "L", LocalDate.of(2025, 12, 5)));
        inventory.add(new Ingredient("Tomatoes", "Vegetable", 5, 10, "kg", LocalDate.of(2025, 11, 25)));
        inventory.add(new Ingredient("Fresh Chicken", "Meat", 3, 5, "pcs", LocalDate.of(2025, 11, 23)));
        inventory.add(new Ingredient("Fresh Pork", "Meat", 0, 5, "pcs", LocalDate.of(2025, 11, 20)));
        inventory.add(new Ingredient("Lettuce", "Vegetable", 15, 5, "kg", LocalDate.of(2025, 11, 30)));

        // ===================== Top Navigation =====================
        AdminTopNavBar adminTopNavBar = new AdminTopNavBar(this);
        add(adminTopNavBar, BorderLayout.NORTH);

        // ===================== Main Panel =====================
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        // ===================== Initialize Panels =====================
        adminOverview = new AdminOverview(this, inventory);
        adminInventory = new AdminInventory(this, inventory);
        adminReports = new AdminReports(this);
        adminSettings = new AdminSettings(this);

        // Show default panel
        showPanel(adminOverview);

        setVisible(true);
    }

    // Method to switch panels
    public void showPanel(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminMainFrame::new);
    }
}