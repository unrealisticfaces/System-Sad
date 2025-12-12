import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdminOverview extends JPanel {

    private AdminMainFrame adminMainFrame;

    public AdminOverview(AdminMainFrame adminMainFrame, List<Ingredient> inventory) {
        this.adminMainFrame = adminMainFrame;
        setLayout(new BorderLayout());
        setBackground(StyleUtils.BG_COLOR); 
        refreshOverview(inventory);
    }

    public void refreshOverview(List<Ingredient> inventory) {
        removeAll();
        
        // --- CALCULATION LOGIC ---
        int total = inventory.size();
        int low = 0, soon = 0, expired = 0, out = 0;
        
        // We will store alert objects to generate cards later
        List<AlertData> alerts = new ArrayList<>();

        for (Ingredient ing : inventory) {
            if (ing.getQuantity() == 0) { 
                out++; 
                alerts.add(new AlertData("Out of Stock", ing.getName(), new Color(189, 195, 199), new Color(127, 140, 141))); 
            }
            else if (ing.getQuantity() <= ing.getMinLevel()) { 
                low++; 
                alerts.add(new AlertData("Low Stock", ing.getName(), new Color(255, 243, 224), new Color(255, 152, 0))); 
            }
            
            if (ing.isExpired()) { 
                expired++; 
                alerts.add(new AlertData("Expired", ing.getName(), new Color(255, 235, 238), new Color(231, 76, 60))); 
            }
            else if (ing.isExpiringSoon()) { 
                soon++; 
                alerts.add(new AlertData("Expiring Soon", ing.getName(), new Color(255, 248, 225), new Color(243, 156, 18))); 
            }
        }

        // --- UI LAYOUT ---
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(StyleUtils.BG_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // 1. Header
        JLabel head = new JLabel("Overview Dashboard");
        head.setFont(StyleUtils.HEADER_FONT);
        head.setForeground(StyleUtils.DARK_TEXT);
        head.setAlignmentX(0f);
        content.add(head);
        content.add(Box.createVerticalStrut(20));

        // 2. Stats Grid
        JPanel grid = new JPanel(new GridLayout(1, 4, 20, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(0f);
        grid.setMaximumSize(new Dimension(2000, 140));

        grid.add(new DashboardCard("Total Items", total, new Color(66, 165, 245), "/img/inventory.png"));
        grid.add(new DashboardCard("Low Stock", low, new Color(255, 167, 38), "/img/alert.png"));
        grid.add(new DashboardCard("Expiring", soon, new Color(239, 83, 80), "/img/expiring.png"));
        grid.add(new DashboardCard("Out of Stock", out, new Color(149, 165, 166), "/img/delete.png"));

        content.add(grid);
        content.add(Box.createVerticalStrut(30));

        // 3. Alerts Section Header
        JLabel alertHead = new JLabel("Attention Needed");
        alertHead.setFont(new Font("Segoe UI", Font.BOLD, 18));
        alertHead.setForeground(StyleUtils.DARK_TEXT);
        alertHead.setAlignmentX(0f);
        content.add(alertHead);
        content.add(Box.createVerticalStrut(15));

        // 4. Alerts List
        if (alerts.isEmpty()) {
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            emptyPanel.setOpaque(false);
            emptyPanel.setAlignmentX(0f);
            
            JLabel ok = new JLabel("All clear! Inventory is in good shape.");
            ok.setForeground(new Color(46, 204, 113));
            ok.setFont(StyleUtils.NORMAL_FONT);
            
            emptyPanel.add(ok);
            content.add(emptyPanel);
        } else {
            for (AlertData alert : alerts) {
                content.add(createAlertCard(alert));
                content.add(Box.createVerticalStrut(10)); // Gap between alerts
            }
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }

    // --- NEW: Alert Card Creation ---
    private JPanel createAlertCard(AlertData data) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(2000, 60)); // Fixed height for neatness
        card.setAlignmentX(0f);
        
        // Left Color Strip
        JPanel strip = new JPanel();
        strip.setPreferredSize(new Dimension(6, 60));
        strip.setBackground(data.accentColor);
        
        // Content
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8)); // Horizontal flow
        textPanel.setOpaque(false);
        
        // Badge (Type)
        JLabel typeLbl = new JLabel(data.type);
        typeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typeLbl.setOpaque(true);
        typeLbl.setBackground(data.bgColor); // Light bg
        typeLbl.setForeground(data.accentColor); // Dark text
        typeLbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); // Padding inside badge
        
        // Item Name
        JLabel itemLbl = new JLabel(data.itemName);
        itemLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemLbl.setForeground(StyleUtils.DARK_TEXT);
        
        textPanel.add(typeLbl);
        textPanel.add(itemLbl);
        
        card.add(strip, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        
        // Subtle Border
        card.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));
        
        return card;
    }

    // --- Helper Classes ---
    
    class AlertData {
        String type;
        String itemName;
        Color bgColor;
        Color accentColor;

        public AlertData(String type, String itemName, Color bgColor, Color accentColor) {
            this.type = type;
            this.itemName = itemName;
            this.bgColor = bgColor;
            this.accentColor = accentColor;
        }
    }

    class DashboardCard extends JPanel {
        public DashboardCard(String title, int value, Color accent, String iconPath) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Outer margin for shadow effect

            JPanel inner = new JPanel(new BorderLayout());
            inner.setBackground(Color.WHITE);
            inner.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Inner padding
            
            JLabel tLbl = new JLabel(title);
            tLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            tLbl.setForeground(new Color(150, 150, 150));
            
            JLabel vLbl = new JLabel(String.valueOf(value));
            vLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
            vLbl.setForeground(accent);

            // Icon
            JLabel iconLbl = new JLabel();
            try {
                java.net.URL url = getClass().getResource(iconPath);
                if (url != null) {
                    ImageIcon ic = new ImageIcon(url);
                    iconLbl.setIcon(new ImageIcon(ic.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
                }
            } catch(Exception e){}

            JPanel left = new JPanel(new GridLayout(2, 1));
            left.setOpaque(false);
            left.add(tLbl);
            left.add(vLbl);

            inner.add(left, BorderLayout.CENTER);
            inner.add(iconLbl, BorderLayout.EAST);
            
            add(inner, BorderLayout.CENTER);
        }
    }
}