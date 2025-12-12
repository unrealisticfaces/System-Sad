import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Comparator;
import java.util.List;

public class StaffInventory extends JPanel {

    private StaffMainFrame staffMainFrame;
    private JTable table;
    private DefaultTableModel model;
    private JLabel totalIngredientsLabel;
    private List<Ingredient> inventory;

    public StaffInventory(StaffMainFrame staffMainFrame, List<Ingredient> inventory) {
        this.staffMainFrame = staffMainFrame;
        this.inventory = inventory;

        setLayout(new BorderLayout());
        setBackground(StyleUtils.BG_COLOR);

        // --- HEADER ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(StyleUtils.BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 20, 40));

        JLabel title = new JLabel("Pantry Inventory");
        title.setFont(StyleUtils.HEADER_FONT);
        title.setForeground(StyleUtils.DARK_TEXT);
        topPanel.add(title, BorderLayout.NORTH);
        
        JLabel sub = new JLabel("View current stock levels");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        sub.setForeground(StyleUtils.LIGHT_TEXT);
        topPanel.add(sub, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.NORTH);

        // --- WRAPPER ---
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        // --- TOOLBAR ---
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JTextField searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)), 
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        // Placeholder Logic
        searchField.setText("Search...");
        searchField.setForeground(Color.GRAY);
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(searchField.getText().equals("Search...")) {
                    searchField.setText("");
                    searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if(searchField.getText().isEmpty()) {
                    searchField.setText("Search...");
                    searchField.setForeground(Color.GRAY);
                }
            }
        });

        // Filter Dropdown
        String[] opts = { "Sort: Name (A-Z)", "Sort: Quantity (Low-High)", "Sort: Quantity (High-Low)" };
        JComboBox<String> sortBox = new JComboBox<>(opts);
        sortBox.setBackground(Color.WHITE);
        
        JPanel leftTools = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftTools.setOpaque(false);
        leftTools.add(searchField);
        leftTools.add(sortBox);
        
        toolbar.add(leftTools, BorderLayout.WEST);

        // --- TABLE ---
        String[] cols = {"Name", "Category", "Qty", "Min", "Unit", "Expiry", "Status"};
        model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(model);
        StyleUtils.styleTable(table);
        table.getColumnModel().getColumn(6).setCellRenderer(new StyleUtils.StatusCellRenderer());

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(toolbar, BorderLayout.NORTH);
        tablePanel.add(scroll, BorderLayout.CENTER);
        
        wrapper.add(tablePanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        // --- LOGIC ---
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.equals("Search...") || text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        sortBox.addActionListener(e -> {
            String sel = (String) sortBox.getSelectedItem();
            if(sel.contains("Name")) inventory.sort(Comparator.comparing(Ingredient::getName));
            else if(sel.contains("Low-High")) inventory.sort(Comparator.comparingInt(Ingredient::getQuantity));
            else if(sel.contains("High-Low")) inventory.sort((a,b) -> b.getQuantity() - a.getQuantity());
            refreshTableData();
        });

        refreshTableData();
    }

    private void refreshTableData() {
        model.setRowCount(0);
        for (Ingredient ing : inventory) {
            model.addRow(new Object[]{ ing.getName(), ing.getType(), ing.getQuantity(), ing.getMinLevel(), ing.getUnit(), ing.getFormattedDate(), ing.getStatus() });
        }
    }
}