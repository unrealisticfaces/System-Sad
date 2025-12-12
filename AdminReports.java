import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class AdminReports extends JPanel {

    private AdminMainFrame mainFrame;
    
    // Data Components
    private JLabel totalVal, lowVal, expiringVal, expiredVal, outVal;
    private AccurateBarChart barChart;
    private JTable table;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    // Filters
    private JComboBox<String> catFilter, statusFilter;

    public AdminReports(AdminMainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout());
        setBackground(StyleUtils.BG_COLOR);

        // --- MAIN SCROLL WRAPPER ---
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(StyleUtils.BG_COLOR);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // 1. Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(StyleUtils.BG_COLOR);
        headerPanel.setMaximumSize(new Dimension(2000, 60));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("Reports & Analytics");
        title.setFont(StyleUtils.HEADER_FONT);
        title.setForeground(StyleUtils.DARK_TEXT);
        
        JLabel subTitle = new JLabel("Detailed inventory metrics and visual breakdown.");
        subTitle.setFont(StyleUtils.NORMAL_FONT);
        subTitle.setForeground(StyleUtils.LIGHT_TEXT);
        
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subTitle, BorderLayout.SOUTH);
        mainContent.add(headerPanel);
        mainContent.add(Box.createVerticalStrut(30));

        // 2. Top Section (Summary + Chart)
        JPanel topSection = new JPanel(new GridLayout(1, 2, 30, 0));
        topSection.setOpaque(false);
        topSection.setMaximumSize(new Dimension(2000, 320));
        topSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        // --- Left: Summary Card ---
        JPanel summaryCard = createCardPanel();
        summaryCard.setLayout(new BorderLayout());
        
        JLabel summaryTitle = new JLabel("Stock Summary");
        summaryTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        summaryTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        summaryCard.add(summaryTitle, BorderLayout.NORTH);

        JPanel metricsPanel = new JPanel(new GridLayout(5, 1, 0, 10));
        metricsPanel.setOpaque(false);

        totalVal = createMetricRow(metricsPanel, "Total Ingredients");
        lowVal = createMetricRow(metricsPanel, "Low Stock Items");
        expiringVal = createMetricRow(metricsPanel, "Expiring Items");
        expiredVal = createMetricRow(metricsPanel, "Expired Items");
        outVal = createMetricRow(metricsPanel, "Out of Stock");
        
        summaryCard.add(metricsPanel, BorderLayout.CENTER);
        topSection.add(summaryCard);

        // --- Right: Accurate Bar Chart ---
        JPanel chartCard = createCardPanel();
        chartCard.setLayout(new BorderLayout());
        
        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        chartHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel chartTitle = new JLabel("Highest Quantity Items");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel chartSub = new JLabel("Top 5 ingredients by stock level");
        chartSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chartSub.setForeground(Color.GRAY);
        
        chartHeader.add(chartTitle, BorderLayout.NORTH);
        chartHeader.add(chartSub, BorderLayout.SOUTH);
        chartCard.add(chartHeader, BorderLayout.NORTH);

        barChart = new AccurateBarChart();
        chartCard.add(barChart, BorderLayout.CENTER);
        
        topSection.add(chartCard);
        
        mainContent.add(topSection);
        mainContent.add(Box.createVerticalStrut(30));

        // 3. Bottom: Filterable Table
        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());
        
        // Table Header & Filters
        JPanel tableTop = new JPanel(new BorderLayout());
        tableTop.setOpaque(false);
        tableTop.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JPanel titleGroup = new JPanel(new GridLayout(2, 1));
        titleGroup.setOpaque(false);
        JLabel tableTitle = new JLabel("Detailed Inventory Status");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        JLabel tableSub = new JLabel("Filterable list of all ingredients");
        tableSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableSub.setForeground(Color.GRAY);
        titleGroup.add(tableTitle);
        titleGroup.add(tableSub);
        
        // Filters
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setOpaque(false);
        catFilter = new JComboBox<>(new String[]{"All Categories", "Meat", "Vegetable", "Dairy", "Poultry", "Others"});
        statusFilter = new JComboBox<>(new String[]{"All Status", "Good Stock", "Low Stock", "Expiring Soon", "Expired", "Out of Stock"});
        styleCombo(catFilter);
        styleCombo(statusFilter);
        filterPanel.add(catFilter);
        filterPanel.add(statusFilter);

        tableTop.add(titleGroup, BorderLayout.WEST);
        tableTop.add(filterPanel, BorderLayout.EAST);
        tableCard.add(tableTop, BorderLayout.NORTH);

        // Table
        String[] cols = {"Name", "Category", "Qty", "Min", "Unit", "Expiry", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };
        
        table = new JTable(tableModel);
        StyleUtils.styleTable(table);
        table.getColumnModel().getColumn(6).setCellRenderer(new StyleUtils.StatusCellRenderer());

        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);

        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setBorder(BorderFactory.createEmptyBorder());
        tableScroll.getViewport().setBackground(Color.WHITE);
        tableCard.add(tableScroll, BorderLayout.CENTER);

        mainContent.add(tableCard);

        JScrollPane pageScroll = new JScrollPane(mainContent);
        pageScroll.setBorder(null);
        pageScroll.getVerticalScrollBar().setUnitIncrement(16);
        add(pageScroll, BorderLayout.CENTER);

        // Listeners
        catFilter.addItemListener(e -> { if(e.getStateChange() == ItemEvent.SELECTED) applyFilters(); });
        statusFilter.addItemListener(e -> { if(e.getStateChange() == ItemEvent.SELECTED) applyFilters(); });

        refreshData();
    }

    // --- COMPATIBILITY METHODS ---
    // These are required because AdminMainFrame calls them!
    public void refreshTable() {
        refreshData();
    }
    
    public void refreshSummary() {
        refreshData();
    }

    // --- MAIN REFRESH LOGIC ---
    public void refreshData() {
        List<Ingredient> inv = mainFrame.inventory;

        // 1. Summary
        totalVal.setText(String.valueOf(inv.size()));
        long low = inv.stream().filter(i -> i.getQuantity() <= i.getMinLevel() && i.getQuantity() > 0).count();
        lowVal.setText(String.valueOf(low));
        lowVal.setForeground(low > 0 ? new Color(255, 152, 0) : StyleUtils.DARK_TEXT);
        
        long soon = inv.stream().filter(Ingredient::isExpiringSoon).count();
        expiringVal.setText(String.valueOf(soon));
        expiringVal.setForeground(soon > 0 ? new Color(243, 156, 18) : StyleUtils.DARK_TEXT);
        
        long exp = inv.stream().filter(Ingredient::isExpired).count();
        expiredVal.setText(String.valueOf(exp));
        expiredVal.setForeground(exp > 0 ? new Color(231, 76, 60) : StyleUtils.DARK_TEXT);
        
        long out = inv.stream().filter(i -> i.getQuantity() == 0).count();
        outVal.setText(String.valueOf(out));
        outVal.setForeground(out > 0 ? Color.RED : StyleUtils.DARK_TEXT);

        // 2. Chart (Top 5 by Qty)
        List<Ingredient> topItems = inv.stream()
            .sorted((a, b) -> b.getQuantity() - a.getQuantity())
            .limit(5)
            .collect(Collectors.toList());
        barChart.setData(topItems);

        // 3. Table
        tableModel.setRowCount(0);
        for(Ingredient i : inv) {
            tableModel.addRow(new Object[]{i.getName(), i.getType(), i.getQuantity(), i.getMinLevel(), i.getUnit(), i.getFormattedDate(), i.getStatus()});
        }
    }

    private void applyFilters() {
        String cat = (String) catFilter.getSelectedItem();
        String stat = (String) statusFilter.getSelectedItem();
        
        RowFilter<DefaultTableModel, Object> rf = new RowFilter<DefaultTableModel, Object>() {
            public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                String rowCat = entry.getStringValue(1);
                String rowStat = entry.getStringValue(6);
                boolean catMatch = cat.equals("All Categories") || rowCat.equals(cat);
                boolean statMatch = stat.equals("All Status") || rowStat.equals(stat);
                return catMatch && statMatch;
            }
        };
        sorter.setRowFilter(rf);
    }

    // --- HELPER METHOODS ---
    private JPanel createCardPanel() {
        JPanel p = new JPanel();
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        return p;
    }

    private JLabel createMetricRow(JPanel parent, String title) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        t.setForeground(StyleUtils.DARK_TEXT);
        JLabel v = new JLabel("0");
        v.setFont(new Font("Segoe UI", Font.BOLD, 14));
        v.setForeground(StyleUtils.DARK_TEXT);
        row.add(t, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        parent.add(row);
        return v;
    }

    private void styleCombo(JComboBox box) {
        box.setBackground(Color.WHITE);
        box.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        box.setPreferredSize(new Dimension(140, 30));
    }

    // ==========================================
    //   ACCURATE CHART COMPONENT
    // ==========================================
    class AccurateBarChart extends JPanel {
        private List<Ingredient> data = new ArrayList<>();
        private final int LEFT_MARGIN = 40;
        private final int BOTTOM_MARGIN = 30;
        private final int TOP_MARGIN = 20;
        
        public AccurateBarChart() {
            setOpaque(false);
        }

        public void setData(List<Ingredient> data) {
            this.data = data;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            int w = getWidth();
            int h = getHeight();
            
            // 1. Calculate Scale
            int maxVal = data.stream().mapToInt(Ingredient::getQuantity).max().orElse(0);
            int scaleMax = (maxVal == 0) ? 10 : (int) (Math.ceil(maxVal / 10.0) * 10);
            
            // 2. Draw Grid & Y-Axis Labels
            int gridCount = 5; 
            int chartH = h - BOTTOM_MARGIN - TOP_MARGIN;
            int chartW = w - LEFT_MARGIN;
            
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            
            for (int i = 0; i <= gridCount; i++) {
                int y = TOP_MARGIN + chartH - (i * chartH / gridCount);
                int val = i * scaleMax / gridCount;
                
                // Grid Line
                g2.setColor(new Color(240, 240, 240));
                g2.drawLine(LEFT_MARGIN, y, w, y);
                
                // Label
                g2.setColor(Color.GRAY);
                String s = String.valueOf(val);
                int strW = g2.getFontMetrics().stringWidth(s);
                g2.drawString(s, LEFT_MARGIN - strW - 5, y + 4);
            }

            // 3. Draw Bars
            if (data.isEmpty()) return;
            
            int barCount = data.size();
            int slotWidth = chartW / barCount;
            int barWidth = Math.min(60, slotWidth - 20); 
            int spacing = (slotWidth - barWidth) / 2;

            for (int i = 0; i < barCount; i++) {
                Ingredient ing = data.get(i);
                
                double ratio = (double) ing.getQuantity() / scaleMax;
                int barH = (int) (ratio * chartH);
                
                int x = LEFT_MARGIN + (i * slotWidth) + spacing;
                int y = TOP_MARGIN + chartH - barH;

                // Color (Orange-Red & Grey alternating)
                if (i % 2 == 0) g2.setColor(new Color(255, 87, 34)); 
                else g2.setColor(new Color(189, 195, 199)); 

                g2.fillRoundRect(x, y, barWidth, barH, 6, 6);
                
                // Draw Value on Top
                g2.setColor(Color.BLACK);
                String valStr = String.valueOf(ing.getQuantity());
                int vW = g2.getFontMetrics().stringWidth(valStr);
                g2.drawString(valStr, x + (barWidth - vW) / 2, y - 5);
                
                // Draw Name below
                g2.setColor(Color.DARK_GRAY);
                String name = ing.getName();
                if(name.length() > 6) name = name.substring(0, 5) + "..";
                int nW = g2.getFontMetrics().stringWidth(name);
                g2.drawString(name, x + (barWidth - nW) / 2, h - 10);
            }
        }
    }
}