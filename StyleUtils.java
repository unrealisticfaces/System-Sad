import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class StyleUtils {

    // --- PALETTE ---
    public static final Color BG_COLOR = new Color(248, 249, 255);
    public static final Color PRIMARY_COLOR = new Color(52, 152, 219); // Blue
    public static final Color DARK_TEXT = new Color(44, 62, 80);
    public static final Color LIGHT_TEXT = new Color(127, 140, 141);
    
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font TABLE_HEADER_FONT = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font NORMAL_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    // --- STYLE TABLE ---
    public static void styleTable(JTable table) {
        table.setRowHeight(45);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(NORMAL_FONT);
        table.setSelectionBackground(new Color(232, 246, 253));
        table.setSelectionForeground(Color.BLACK);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel l = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                l.setBackground(Color.WHITE);
                l.setForeground(DARK_TEXT);
                l.setFont(TABLE_HEADER_FONT);
                l.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(220, 220, 220)));
                l.setPreferredSize(new Dimension(l.getWidth(), 50));
                return l;
            }
        });
    }

    // --- PILL STATUS RENDERER ---
    public static class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = new JLabel((String) value);
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            label.setOpaque(false); 

            String status = (String) value;
            Color bg, fg;

            if (status == null) status = "";

            switch (status) {
                case "Out of Stock":
                case "Expired":
                    bg = new Color(255, 235, 238); fg = new Color(211, 47, 47); break;
                case "Low Stock":
                case "Expiring Soon":
                    bg = new Color(255, 248, 225); fg = new Color(245, 124, 0); break;
                default: // Good
                    bg = new Color(232, 245, 233); fg = new Color(56, 142, 60); break;
            }

            // Return a panel that draws the rounded pill
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    // Draw rounded rectangle
                    int w = getWidth() - 20;
                    int h = getHeight() - 14;
                    g2.fillRoundRect(10, 7, w, h, 15, 15);
                }
            };
            panel.setLayout(new BorderLayout());
            panel.add(label, BorderLayout.CENTER);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            label.setForeground(fg);
            
            return panel;
        }
    }
}