import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class AdminReports extends JPanel {

    private AdminMainFrame mainFrame;
    private DefaultTableModel tableModel;
    private JTable table;
    
    // Labels for Summary
    private JLabel totalLbl, lowLbl, expSoonLbl, expiredLbl;

    public AdminReports(AdminMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 255));

        // --- Summary Panel ---
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        summaryPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        summaryPanel.setBackground(getBackground());
        
        totalLbl = new JLabel("Total: 0");
        lowLbl = new JLabel("Low: 0");
        expSoonLbl = new JLabel("Exp. Soon: 0");
        expiredLbl = new JLabel("Expired: 0");
        
        summaryPanel.add(createCard(totalLbl));
        summaryPanel.add(createCard(lowLbl));
        summaryPanel.add(createCard(expSoonLbl));
        summaryPanel.add(createCard(expiredLbl));
        
        add(summaryPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        String[] cols = {"Name", "Category", "Qty", "Unit", "Expiry", "Status"};
        tableModel = new DefaultTableModel(cols, 0);
        table = new JTable(tableModel);
        
        add(new JScrollPane(table), BorderLayout.CENTER);
        
        // Initial Refresh
        refreshTable();
        refreshSummary();
    }
    
    private JPanel createCard(JLabel lbl) {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.add(lbl);
        return p;
    }

    public void refreshSummary() {
        List<Ingredient> inv = mainFrame.inventory;
        totalLbl.setText("Total: " + inv.size());
        lowLbl.setText("Low: " + inv.stream().filter(i -> i.getQuantity() <= i.getMinLevel()).count());
        expSoonLbl.setText("Exp Soon: " + inv.stream().filter(Ingredient::isExpiringSoon).count());
        expiredLbl.setText("Expired: " + inv.stream().filter(Ingredient::isExpired).count());
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for(Ingredient i : mainFrame.inventory) {
            tableModel.addRow(new Object[]{
                i.getName(), i.getType(), i.getQuantity(), i.getUnit(), 
                i.getFormattedDate(), i.getStatus()
            });
        }
    }
}