import javax.swing.*;
import java.awt.*;
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

        // ===================== LOAD FROM DB =====================
        inventory = InventoryDataManager.loadInventory(); 

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
        // Refresh data whenever switching panels to ensure sync
        if (panel instanceof AdminOverview) ((AdminOverview)panel).refreshOverview(inventory);
        if (panel instanceof AdminReports) {
            ((AdminReports)panel).refreshTable();
            ((AdminReports)panel).refreshSummary();
        }
        
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AdminMainFrame::new);
    }
}