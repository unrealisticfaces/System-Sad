import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AdminInventory extends JPanel {

    private AdminMainFrame adminMainFrame;
    private List<Ingredient> inventory;
    private JTable table;
    private DefaultTableModel model;
    private JLabel totalIngredientsLabel;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("MM/dd/yyyy");

    public AdminInventory(AdminMainFrame adminMainFrame, List<Ingredient> inventory) {
        this.adminMainFrame = adminMainFrame;
        this.inventory = inventory;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 255));

        // --- TOP PANEL ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(248, 249, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel overviewLabel = new JLabel("Ingredient Inventory");
        overviewLabel.setFont(new Font("Arial", Font.BOLD, 16));
        JLabel subtitleLabel = new JLabel("Monitor critical inventory issues");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        topPanel.add(overviewLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subtitleLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- CONTENT PANEL ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        String[] columnNames = {"Name", "Category", "Quantity", "Min. Level", "Unit", "Expiry Date", "Status", "Actions"};
        
        model = new DefaultTableModel(new Object[0][0], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions editable
            }
        };

        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(Color.WHITE);
        
        // Custom Status Renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);
                
                String status = (String) value;
                if ("Out of Stock".equals(status)) label.setBackground(new Color(255, 102, 102));
                else if ("Expired".equals(status)) label.setBackground(new Color(255, 102, 102));
                else if ("Expiring Soon".equals(status) || "Low Stock".equals(status)) label.setBackground(new Color(255, 204, 153));
                else label.setBackground(Color.WHITE);
                
                return label;
            }
        });

        // Setup Actions
        table.getColumn("Actions").setCellRenderer(new ActionRenderer());
        table.getColumn("Actions").setCellEditor(new ActionEditor(new JCheckBox()));

        // --- SEARCH & CONTROLS ---
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 0, 40));

        JTextField searchField = new JTextField("Search ingredient...");
        searchField.setPreferredSize(new Dimension(300, 30));
        
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if(text.equals("Search ingredient...")) return;
                if (text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JButton addButton = new JButton("+ Add New Ingredient");
        addButton.setBackground(Color.BLACK);
        addButton.setForeground(Color.WHITE);
        addButton.addActionListener(e -> showAddIngredientDialog());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setOpaque(false);
        rightPanel.add(searchField);
        rightPanel.add(addButton);
        
        searchPanel.add(rightPanel, BorderLayout.EAST);

        totalIngredientsLabel = new JLabel("Total Ingredients: 0");
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(Color.WHITE);
        infoPanel.add(searchPanel, BorderLayout.NORTH);
        infoPanel.add(totalIngredientsLabel, BorderLayout.SOUTH);
        totalIngredientsLabel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 0));

        contentPanel.add(infoPanel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.setOpaque(false);
        outerWrapper.setBorder(BorderFactory.createEmptyBorder(30, 20, 40, 20));
        outerWrapper.add(contentPanel, BorderLayout.CENTER);

        add(outerWrapper, BorderLayout.CENTER);
        
        // Initial Refresh
        refreshTableData();
    }
    
    private void refreshTableData() {
        model.setRowCount(0);
        for(Ingredient i : inventory) {
             model.addRow(new Object[]{
                i.getName(), i.getType(), i.getQuantity(), i.getMinLevel(), i.getUnit(), 
                i.getFormattedDate(), i.getStatus(), ""
            });
        }
        totalIngredientsLabel.setText("Total Ingredients: " + model.getRowCount());
    }

    private void showAddIngredientDialog() {
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Ingredient", true);
        dialog.setSize(400, 500);
        dialog.setLayout(new GridLayout(7, 2, 10, 10));
        dialog.setLocationRelativeTo(null);

        JTextField nameField = new JTextField();
        JComboBox<String> catBox = new JComboBox<>(new String[]{"Meat", "Vegetable", "Dairy", "Poultry", "Others"});
        JTextField qtyField = new JTextField();
        JTextField minField = new JTextField();
        JComboBox<String> unitBox = new JComboBox<>(new String[]{"pcs", "kg", "L", "g", "mL"});
        
        JFormattedTextField dateField = null;
        try { dateField = new JFormattedTextField(new MaskFormatter("##/##/####")); } catch (ParseException e) {}

        dialog.add(new JLabel("Name:")); dialog.add(nameField);
        dialog.add(new JLabel("Category:")); dialog.add(catBox);
        dialog.add(new JLabel("Quantity:")); dialog.add(qtyField);
        dialog.add(new JLabel("Min Stock:")); dialog.add(minField);
        dialog.add(new JLabel("Unit:")); dialog.add(unitBox);
        dialog.add(new JLabel("Expiry (MM/DD/YYYY):")); dialog.add(dateField);

        JButton saveBtn = new JButton("Save");
        JFormattedTextField finalDateField = dateField;
        saveBtn.addActionListener(e -> {
            try {
                String name = nameField.getText();
                int qty = Integer.parseInt(qtyField.getText());
                int min = Integer.parseInt(minField.getText());
                String unit = (String) unitBox.getSelectedItem();
                String type = (String) catBox.getSelectedItem();
                LocalDate expDate = LocalDate.parse(finalDateField.getText(), DATE_FMT);

                Ingredient ing = new Ingredient(name, type, qty, min, unit, expDate);
                inventory.add(ing);
                
                // --- SAVE TO DB ---
                InventoryDataManager.saveInventory(inventory);

                // Update UI across all panels
                refreshTableData();
                
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid Input: " + ex.getMessage());
            }
        });

        dialog.add(new JLabel("")); 
        dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    // --- Action Button Classes ---
    class ActionRenderer extends JPanel implements TableCellRenderer {
        public ActionRenderer() { add(new JButton("Edit")); add(new JButton("Delete")); setOpaque(true); setBackground(Color.WHITE); }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) { return this; }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        JPanel p = new JPanel(); JButton edit = new JButton("Edit"), del = new JButton("Delete");
        public ActionEditor(JCheckBox cb) {
            p.add(edit); p.add(del);
            p.setBackground(Color.WHITE);
            
            // --- DELETE ---
            del.addActionListener(e -> {
                int row = table.getSelectedRow();
                if(row != -1) {
                    inventory.remove(row);
                    // Save
                    InventoryDataManager.saveInventory(inventory);
                    
                    refreshTableData();
                    fireEditingStopped();
                }
            });
            
            // --- EDIT ---
            edit.addActionListener(e -> {
                 int row = table.getSelectedRow();
                 if (row != -1) {
                    String val = JOptionPane.showInputDialog("New Quantity:", inventory.get(row).getQuantity());
                    if(val != null) {
                        try {
                            inventory.get(row).setQuantity(Integer.parseInt(val));
                            // Save
                            InventoryDataManager.saveInventory(inventory);
                            
                            refreshTableData();
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(null, "Invalid number");
                        }
                        fireEditingStopped();
                    }
                 }
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) { return p; }
        public Object getCellEditorValue() { return ""; }
    }
}