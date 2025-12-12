import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.*;
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
        setBackground(StyleUtils.BG_COLOR);

        // --- HEADER ---
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(StyleUtils.BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 20, 40));

        JLabel title = new JLabel("Inventory Management");
        title.setFont(StyleUtils.HEADER_FONT);
        title.setForeground(StyleUtils.DARK_TEXT);
        
        topPanel.add(title, BorderLayout.WEST);
        
        JButton addButton = new JButton("+ Add Ingredient");
        styleButton(addButton, StyleUtils.PRIMARY_COLOR);
        addButton.addActionListener(e -> showAddIngredientDialog());
        topPanel.add(addButton, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // --- CONTENT WRAPPER ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        JPanel shadowContainer = new JPanel(new BorderLayout());
        shadowContainer.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));
        shadowContainer.setOpaque(false);

        // --- CONTROLS ---
        JPanel controls = new JPanel(new BorderLayout());
        controls.setBackground(Color.WHITE);
        controls.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JTextField searchField = new JTextField("Search ingredients...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(StyleUtils.NORMAL_FONT);
        searchField.setForeground(Color.GRAY);
        
        searchField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if(searchField.getText().equals("Search ingredients...")) {
                    searchField.setText(""); searchField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if(searchField.getText().isEmpty()) {
                    searchField.setText("Search ingredients..."); searchField.setForeground(Color.GRAY);
                }
            }
        });
        
        controls.add(searchField, BorderLayout.WEST);
        
        totalIngredientsLabel = new JLabel("Total Items: 0");
        totalIngredientsLabel.setFont(StyleUtils.NORMAL_FONT);
        controls.add(totalIngredientsLabel, BorderLayout.EAST);

        // --- TABLE ---
        String[] columnNames = {"Name", "Category", "Qty", "Min", "Unit", "Expiry", "Status", "Actions"};
        model = new DefaultTableModel(new Object[0][0], columnNames) {
            public boolean isCellEditable(int row, int column) { return column == 7; }
        };

        table = new JTable(model);
        StyleUtils.styleTable(table);
        
        // --- ALIGNMENT FIXES ---
        // Center these columns (Header + Content)
        StyleUtils.alignTableColumn(table, 2, SwingConstants.CENTER); // Qty
        StyleUtils.alignTableColumn(table, 3, SwingConstants.CENTER); // Min
        StyleUtils.alignTableColumn(table, 4, SwingConstants.CENTER); // Unit
        StyleUtils.alignTableColumn(table, 5, SwingConstants.CENTER); // Expiry
        
        // Renderers
        table.getColumnModel().getColumn(6).setCellRenderer(new StyleUtils.StatusCellRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ActionEditor(new JCheckBox()));
        
        // Force Actions Header to Center (even if custom renderer isn't used for header)
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);

        // Width adjustments
        table.getColumnModel().getColumn(0).setPreferredWidth(150);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        table.getColumnModel().getColumn(7).setPreferredWidth(160);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        contentPanel.add(controls, BorderLayout.NORTH);
        contentPanel.add(scroll, BorderLayout.CENTER);
        
        shadowContainer.add(contentPanel, BorderLayout.CENTER);
        add(shadowContainer, BorderLayout.CENTER);

        // Logic...
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.equals("Search ingredients...") || text.trim().length() == 0) sorter.setRowFilter(null);
                else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        refreshTableData();
    }
    
    private void styleButton(JButton btn, Color bg) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
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
        
        // ... (Keep existing dialog logic, abbreviated here for length) ...
        JTextField nameField = new JTextField();
        JComboBox<String> catBox = new JComboBox<>(new String[]{"Meat", "Vegetable", "Dairy", "Poultry", "Others"});
        JTextField qtyField = new JTextField();
        JTextField minField = new JTextField();
        JComboBox<String> unitBox = new JComboBox<>(new String[]{"pcs", "kg", "L", "g", "mL"});
        JFormattedTextField dateField = null;
        try { dateField = new JFormattedTextField(new MaskFormatter("##/##/####")); } catch (Exception e) {}

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
                inventory.add(new Ingredient(name, type, qty, min, unit, expDate));
                InventoryDataManager.saveInventory(inventory);
                refreshTableData();
                dialog.dispose();
            } catch (Exception ex) { JOptionPane.showMessageDialog(dialog, "Invalid Input"); }
        });
        dialog.add(new JLabel("")); dialog.add(saveBtn);
        dialog.setVisible(true);
    }

    class ActionRenderer extends JPanel implements TableCellRenderer {
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        public ActionRenderer() {
            setOpaque(true);
            setLayout(new GridBagLayout()); 
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 2, 0, 2);
            styleActionBtn(edit, new Color(241, 196, 15));
            styleActionBtn(del, new Color(231, 76, 60));
            add(edit, gbc);
            add(del, gbc);
        }
        private void styleActionBtn(JButton btn, Color c) {
            btn.setBackground(c); btn.setForeground(Color.WHITE);
            btn.setBorder(null); btn.setFocusPainted(false);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 11));
            btn.setPreferredSize(new Dimension(60, 26));
        }
        public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
            setBackground(s ? t.getSelectionBackground() : Color.WHITE);
            return this;
        }
    }

    class ActionEditor extends AbstractCellEditor implements TableCellEditor {
        JPanel p = new JPanel(new GridBagLayout());
        JButton edit = new JButton("Edit");
        JButton del = new JButton("Delete");
        public ActionEditor(JCheckBox cb) {
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(0, 2, 0, 2);
            edit.setBackground(new Color(241, 196, 15)); edit.setForeground(Color.WHITE);
            edit.setBorder(null); edit.setPreferredSize(new Dimension(60, 26));
            del.setBackground(new Color(231, 76, 60)); del.setForeground(Color.WHITE);
            del.setBorder(null); del.setPreferredSize(new Dimension(60, 26));
            p.add(edit, gbc); p.add(del, gbc);
            
            del.addActionListener(e -> {
                int row = table.getSelectedRow();
                if(row != -1 && JOptionPane.showConfirmDialog(null, "Delete?", "Confirm", JOptionPane.YES_NO_OPTION)==0){
                    inventory.remove(row);
                    InventoryDataManager.saveInventory(inventory);
                    refreshTableData();
                }
                fireEditingStopped();
            });
            edit.addActionListener(e -> {
                 int row = table.getSelectedRow();
                 if (row != -1) {
                    String val = JOptionPane.showInputDialog("New Quantity:", inventory.get(row).getQuantity());
                    if(val!=null) { 
                        try { inventory.get(row).setQuantity(Integer.parseInt(val)); InventoryDataManager.saveInventory(inventory); refreshTableData(); } catch(Exception ex){}
                    }
                    fireEditingStopped();
                 }
            });
        }
        public Component getTableCellEditorComponent(JTable t, Object v, boolean s, int r, int c) { 
            p.setBackground(t.getSelectionBackground()); return p; 
        }
        public Object getCellEditorValue() { return ""; }
    }
}