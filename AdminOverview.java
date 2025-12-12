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
        setBackground(new Color(248, 249, 255));
        
        // Init with current inventory
        add(createCenterPanel(inventory), BorderLayout.CENTER);
    }

    // Returns JComponent to allow JScrollPane
    private JComponent createCenterPanel(List<Ingredient> inventory) {
        // --- CALCULATION LOGIC ---
        int total = inventory.size();
        int low = 0, soon = 0, expired = 0, out = 0;
        
        List<String> outList = new ArrayList<>();
        List<String> expList = new ArrayList<>();
        List<String> soonList = new ArrayList<>();
        List<String> lowList = new ArrayList<>();

        LocalDate today = LocalDate.now();

        for (Ingredient ing : inventory) {
            if (ing.getQuantity() == 0) { 
                out++; 
                outList.add(ing.getName()); 
            } else if (ing.getQuantity() <= ing.getMinLevel()) { 
                low++; 
                lowList.add(ing.getName()); 
            }
            
            if (ing.isExpired()) { 
                expired++; 
                expList.add(ing.getName()); 
            } else if (ing.isExpiringSoon()) { 
                soon++; 
                soonList.add(ing.getName()); 
            }
        }

        // --- UI LAYOUT ---
        // Main container for scrolling
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(248, 249, 255));
        content.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));

        // 1. Header
        JLabel header = new JLabel("Dashboard Overview");
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        header.setForeground(new Color(50, 50, 50));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);
        
        JLabel subHeader = new JLabel("Key metrics and critical inventory alerts.");
        subHeader.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subHeader.setForeground(Color.GRAY);
        subHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subHeader);
        content.add(Box.createVerticalStrut(25));

        // 2. Stats Boxes (Grid Layout)
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 20, 0)); // 1 row, 5 cols, gap 20
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(2000, 120)); // Limit height
        
        // Add nice cards
        statsPanel.add(createStatCard("Total Items", String.valueOf(total), new Color(41, 128, 185))); // Blue
        statsPanel.add(createStatCard("Low Stock", String.valueOf(low), new Color(243, 156, 18))); // Orange
        statsPanel.add(createStatCard("Expiring", String.valueOf(soon), new Color(230, 126, 34))); // Dark Orange
        statsPanel.add(createStatCard("Expired", String.valueOf(expired), new Color(192, 57, 43))); // Red
        statsPanel.add(createStatCard("Out of Stock", String.valueOf(out), new Color(149, 165, 166))); // Grey

        content.add(statsPanel);
        content.add(Box.createVerticalStrut(30));

        // 3. Alerts Section Header
        JLabel alertHeader = new JLabel("Active Alerts");
        alertHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        alertHeader.setForeground(new Color(50, 50, 50));
        alertHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(alertHeader);
        content.add(Box.createVerticalStrut(15));

        // 4. Alerts List
        if(!outList.isEmpty()) 
            content.add(createAlertCard("Out of Stock", "Immediate restock required.", outList, new Color(255, 235, 238), new Color(211, 47, 47)));
        
        if(!expList.isEmpty()) 
            content.add(createAlertCard("Expired Items", "Remove from inventory immediately.", expList, new Color(255, 235, 238), new Color(198, 40, 40)));
        
        if(!soonList.isEmpty()) 
            content.add(createAlertCard("Expiring Soon", "Plan to use or discount within 7 days.", soonList, new Color(255, 243, 224), new Color(239, 108, 0)));
        
        if(!lowList.isEmpty()) 
            content.add(createAlertCard("Low Stock Warning", "Quantity below minimum threshold.", lowList, new Color(255, 248, 225), new Color(251, 192, 45)));

        if (outList.isEmpty() && expList.isEmpty() && soonList.isEmpty() && lowList.isEmpty()) {
            JPanel noAlerts = new JPanel(new FlowLayout(FlowLayout.LEFT));
            noAlerts.setOpaque(false);
            JLabel good = new JLabel("All clear! No critical issues found.");
            good.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            good.setForeground(new Color(39, 174, 96));
            noAlerts.add(good);
            content.add(noAlerts);
        }
        
        // Wrap in scroll pane
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // --- HELPER: BEAUTIFUL STAT CARD ---
    private JPanel createStatCard(String title, String val, Color accentColor) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 3, 0, accentColor), // Bottom colored line
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        t.setForeground(Color.GRAY);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 28));
        v.setForeground(Color.BLACK);
        v.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(t);
        card.add(Box.createVerticalStrut(10));
        card.add(v);
        
        return card;
    }

    // --- HELPER: ALERT CARD ---
    private JPanel createAlertCard(String title, String sub, List<String> items, Color bg, Color textC) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bg.darker(), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(2000, 500)); // Allow width to stretch
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(textC);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel s = new JLabel(sub);
        s.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        s.setForeground(textC.darker());
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(t);
        card.add(s);
        card.add(Box.createVerticalStrut(10));
        
        // List items horizontally if possible, or vertical if many
        StringBuilder sb = new StringBuilder("<html>");
        for(String i : items) sb.append("• ").append(i).append("&nbsp;&nbsp; ");
        sb.append("</html>");
        
        JLabel content = new JLabel(sb.toString());
        content.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        content.setForeground(Color.BLACK);
        content.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(content);
        
        // Wrapper for margin bottom
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card);
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return wrap;
    }

    public void refreshOverview(List<Ingredient> inventory) {
        removeAll();
        add(createCenterPanel(inventory), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}