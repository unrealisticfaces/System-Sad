import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StaffMainFrame extends JFrame {

    public StaffOverview staffOverview;
    public StaffInventory staffInventory;
    public StaffSettings staffSettings;

    public List<Ingredient> inventory; // Shared inventory
    private JPanel mainPanel;

    public StaffMainFrame() {
        setTitle("Ingredient Inventory Management System - Staff");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // ===================== LOAD FROM DB =====================
        inventory = InventoryDataManager.loadInventory();

        // ===================== Top Navigation =====================
        StaffTopNavBar staffTopNavBar = new StaffTopNavBar(this);
        add(staffTopNavBar, BorderLayout.NORTH);

        // ===================== Main Panels =====================
        mainPanel = new JPanel(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);

        
        staffOverview = new StaffOverview(this, inventory);
        staffInventory = new StaffInventory(this, inventory);
        staffSettings = new StaffSettings(this);

        // ===================== Show Default Panel =====================
        showPanel(staffOverview);

        setVisible(true);
    }

    // ===================== Panel Switcher =====================
    public void showPanel(JPanel panel) {
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StaffMainFrame::new);
    }
}