import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StaffOverview extends JPanel {

    private StaffMainFrame staffMainFrame;

    public StaffOverview(StaffMainFrame staffMainFrame, List<Ingredient> inventory) {
        this.staffMainFrame = staffMainFrame;
        setLayout(new BorderLayout());
        setBackground(StyleUtils.BG_COLOR);
        refreshOverview(inventory);
    }

    public void refreshOverview(List<Ingredient> inventory) {
        removeAll();
        
        int total = inventory.size();
        int low = 0, soon = 0, expired = 0, out = 0;
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

        // --- UI ---
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(StyleUtils.BG_COLOR);
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // Header
        JLabel head = new JLabel("Pantry Dashboard");
        head.setFont(StyleUtils.HEADER_FONT);
        head.setForeground(StyleUtils.DARK_TEXT);
        head.setAlignmentX(0f);
        content.add(head);
        
        JLabel sub = new JLabel("Welcome back, Staff.");
        sub.setFont(StyleUtils.NORMAL_FONT);
        sub.setForeground(StyleUtils.LIGHT_TEXT);
        sub.setAlignmentX(0f);
        content.add(sub);
        
        content.add(Box.createVerticalStrut(20));

        // Stats Grid
        JPanel grid = new JPanel(new GridLayout(1, 4, 20, 0));
        grid.setOpaque(false);
        grid.setAlignmentX(0f);
        grid.setMaximumSize(new Dimension(2000, 140));

        grid.add(new DashboardCard("Total Items", total, new Color(100, 149, 237), "/img/inventory.png"));
        grid.add(new DashboardCard("Low Stock", low, new Color(255, 179, 71), "/img/alert.png"));
        grid.add(new DashboardCard("Expiring", soon, new Color(255, 107, 107), "/img/expiring.png"));
        grid.add(new DashboardCard("Out of Stock", out, new Color(97, 97, 97), "/img/delete.png"));

        content.add(grid);
        content.add(Box.createVerticalStrut(30));

        // Alerts List
        JLabel alertHead = new JLabel("Notifications");
        alertHead.setFont(new Font("Segoe UI", Font.BOLD, 18));
        alertHead.setForeground(StyleUtils.DARK_TEXT);
        alertHead.setAlignmentX(0f);
        content.add(alertHead);
        content.add(Box.createVerticalStrut(15));

        if (alerts.isEmpty()) {
            JLabel ok = new JLabel("Everything is good!");
            ok.setForeground(new Color(46, 204, 113));
            ok.setFont(StyleUtils.NORMAL_FONT);
            content.add(ok);
        } else {
            for (AlertData alert : alerts) {
                content.add(createAlertCard(alert));
                content.add(Box.createVerticalStrut(10));
            }
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);
        
        revalidate();
        repaint();
    }

    private JPanel createAlertCard(AlertData data) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setMaximumSize(new Dimension(2000, 60));
        card.setAlignmentX(0f);
        
        JPanel strip = new JPanel();
        strip.setPreferredSize(new Dimension(6, 60));
        strip.setBackground(data.accentColor);
        
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 8));
        textPanel.setOpaque(false);
        
        JLabel typeLbl = new JLabel(data.type);
        typeLbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        typeLbl.setOpaque(true);
        typeLbl.setBackground(data.bgColor);
        typeLbl.setForeground(data.accentColor);
        typeLbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        
        JLabel itemLbl = new JLabel(data.itemName);
        itemLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemLbl.setForeground(StyleUtils.DARK_TEXT);
        
        textPanel.add(typeLbl);
        textPanel.add(itemLbl);
        
        card.add(strip, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);
        card.setBorder(BorderFactory.createLineBorder(new Color(240, 240, 240)));
        return card;
    }
    
    class AlertData {
        String type; String itemName; Color bgColor; Color accentColor;
        public AlertData(String t, String i, Color b, Color a) { type=t; itemName=i; bgColor=b; accentColor=a; }
    }

    class DashboardCard extends JPanel {
        public DashboardCard(String title, int value, Color accent, String iconPath) {
            setLayout(new BorderLayout());
            setOpaque(false);
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JPanel inner = new JPanel(new BorderLayout());
            inner.setBackground(Color.WHITE);
            inner.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
            
            JLabel tLbl = new JLabel(title);
            tLbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            tLbl.setForeground(new Color(150, 150, 150));
            
            JLabel vLbl = new JLabel(String.valueOf(value));
            vLbl.setFont(new Font("Segoe UI", Font.BOLD, 36));
            vLbl.setForeground(accent);

            JLabel iconLbl = new JLabel();
            try {
                java.net.URL url = getClass().getResource(iconPath);
                if (url != null) iconLbl.setIcon(new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH)));
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