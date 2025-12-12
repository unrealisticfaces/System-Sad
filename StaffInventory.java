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
    private TableRowSorter<DefaultTableModel> sorter;

    public StaffInventory(StaffMainFrame staffMainFrame, List<Ingredient> inventory) {
        this.staffMainFrame = staffMainFrame;
        this.inventory = inventory;

        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 255));

        // --- TOP HEADER ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(new Color(248, 249, 255));
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        JLabel overviewLabel = new JLabel("Ingredient Inventory");
        overviewLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        overviewLabel.setForeground(new Color(50, 50, 60));
        
        JLabel subtitleLabel = new JLabel("Manage and track stock levels.");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(Color.GRAY);

        topPanel.add(overviewLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subtitleLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- TABLE SECTION ---
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);

        String[] columnNames = {"Name", "Category", "Quantity", "Min. Level", "Unit", "Expiry Date", "Status"};
        
        model = new DefaultTableModel(new Object[0][0], columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent) ((JComponent) c).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return c;
            }
        };

        // Modern Table Styling
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setRowHeight(40); // Taller rows for better readability
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(new Color(236, 249, 255));
        table.setSelectionForeground(Color.BLACK);
        table.setShowVerticalLines(false);
        
        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(Color.WHITE);
        header.setForeground(new Color(100, 100, 100));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        // Column Widths
        table.getColumnModel().getColumn(0).setPreferredWidth(180); // Name
        table.getColumnModel().getColumn(6).setPreferredWidth(130); // Status

        // --- CUSTOM STATUS RENDERER (Softer Colors) ---
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if(!isSelected) c.setBackground(Color.WHITE);
                
                // Status Column Logic
                if (column == 6) {
                    JLabel lbl = (JLabel) c;
                    String status = (String) value;
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                    lbl.setOpaque(true); // Needed to show background color
                    
                    // -- SOFT PASTEL PALETTE --
                    switch (status) {
                        case "Out of Stock":
                            lbl.setBackground(new Color(255, 235, 238)); // Very light red
                            lbl.setForeground(new Color(198, 40, 40));   // Darker red text
                            break;
                        case "Low Stock":
                            lbl.setBackground(new Color(255, 248, 225)); // Very light amber
                            lbl.setForeground(new Color(245, 127, 23));  // Dark amber text
                            break;
                        case "Expiring Soon":
                            lbl.setBackground(new Color(255, 243, 224)); // Light orange
                            lbl.setForeground(new Color(230, 81, 0));    // Dark orange text
                            break;
                        case "Expired":
                            lbl.setBackground(new Color(245, 245, 245)); // Light grey
                            lbl.setForeground(new Color(97, 97, 97));    // Dark grey text
                            break;
                        default: // Good Stock
                            lbl.setBackground(new Color(232, 245, 233)); // Very light green
                            lbl.setForeground(new Color(46, 125, 50));   // Dark green text
                            break;
                    }
                } else {
                    setForeground(Color.BLACK);
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        });

        // Setup Sorter
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // --- SEARCH BAR & FILTER ---
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.setBackground(Color.WHITE);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 10, 30));

        // Search Field
        IconTextField searchField = new IconTextField();
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                String text = searchField.getText();
                if (text.equals("Search ingredient...") || text.trim().isEmpty()) {
                    sorter.setRowFilter(null);
                } else {
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        // Filter Combo
        String[] opts = { "Alphabetical (A - Z)", "Alphabetical (Z - A)", "Quantity (Low - High)", "Quantity (High - Low)" };
        JComboBox<String> comboBox = new JComboBox<>(opts);
        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboBox.setBackground(Color.WHITE);
        comboBox.setPreferredSize(new Dimension(200, 35));
        
        comboBox.addActionListener(e -> {
            String selected = (String) comboBox.getSelectedItem();
            if(selected == null) return;
            switch(selected) {
                case "Alphabetical (A - Z)": inventory.sort(Comparator.comparing(Ingredient::getName, String.CASE_INSENSITIVE_ORDER)); break;
                case "Alphabetical (Z - A)": inventory.sort(Comparator.comparing(Ingredient::getName, String.CASE_INSENSITIVE_ORDER).reversed()); break;
                case "Quantity (Low - High)": inventory.sort(Comparator.comparingInt(Ingredient::getQuantity)); break;
                case "Quantity (High - Low)": inventory.sort(Comparator.comparingInt(Ingredient::getQuantity).reversed()); break;
            }
            refreshTableData();
        });

        JPanel toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolBar.setOpaque(false);
        toolBar.add(searchField);
        toolBar.add(comboBox);
        controlsPanel.add(toolBar, BorderLayout.WEST);

        // Header Wrapper
        totalIngredientsLabel = new JLabel("Total Items: 0");
        totalIngredientsLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalIngredientsLabel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 0));

        JPanel headerWrapper = new JPanel(new BorderLayout());
        headerWrapper.setBackground(Color.WHITE);
        headerWrapper.add(controlsPanel, BorderLayout.NORTH);
        headerWrapper.add(totalIngredientsLabel, BorderLayout.SOUTH);

        contentPanel.add(headerWrapper, BorderLayout.NORTH);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 30, 20, 30));
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        // Final Wrapper
        JPanel outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.setOpaque(false);
        outerWrapper.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        outerWrapper.add(contentPanel, BorderLayout.CENTER);

        add(outerWrapper, BorderLayout.CENTER);
        
        refreshTableData();
    }

    private void refreshTableData() {
        model.setRowCount(0);
        for (Ingredient ing : inventory) {
            model.addRow(new Object[]{ ing.getName(), ing.getType(), ing.getQuantity(), ing.getMinLevel(), ing.getUnit(), ing.getFormattedDate(), ing.getStatus() });
        }
        totalIngredientsLabel.setText("Total Items: " + inventory.size());
    }

    // Custom Round Text Field
    class IconTextField extends JTextField {
        private int cornerRadius = 15;
        private String placeholder = "Search ingredient...";
        public IconTextField() {
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(5, 12, 5, 5));
            setText(placeholder);
            setForeground(Color.GRAY);
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            addFocusListener(new FocusAdapter() {
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) { setText(""); setForeground(Color.BLACK); }
                }
                public void focusLost(FocusEvent e) {
                    if (getText().trim().isEmpty()) { setText(placeholder); setForeground(Color.GRAY); }
                }
            });
        }
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g);
            g2.dispose();
        }
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(200, 200, 200));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            g2.dispose();
        }
    }
}