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
        setBackground(new Color(248, 249, 255)); // Soft background

        // Use JScrollPane for content
        add(createCenterPanel(inventory), BorderLayout.CENTER);
    }

    private JScrollPane createCenterPanel(List<Ingredient> inventory) {
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
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(248, 249, 255));
        content.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        // 1. Header
        JLabel header = new JLabel("Hello, Staff");
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(new Color(50, 50, 60));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(header);

        JLabel subHeader = new JLabel("Here is what's happening in the pantry today.");
        subHeader.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subHeader.setForeground(new Color(120, 120, 130));
        subHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(subHeader);
        content.add(Box.createVerticalStrut(30));

        // 2. Stats Cards (Grid)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 25, 0)); // 1 row, 4 cols, 25px gap
        statsPanel.setOpaque(false);
        statsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        statsPanel.setMaximumSize(new Dimension(2000, 130));

        // Softer Colors for Cards
        statsPanel.add(createStatCard("Total Items", String.valueOf(total), new Color(100, 149, 237), "/img/inventory.png")); // Cornflower Blue
        statsPanel.add(createStatCard("Low Stock", String.valueOf(low), new Color(255, 179, 71), "/img/alert.png"));     // Pastel Orange
        statsPanel.add(createStatCard("Expiring", String.valueOf(soon), new Color(255, 107, 107), "/img/expiring.png"));   // Soft Red
        statsPanel.add(createStatCard("Out of Stock", String.valueOf(out), new Color(97, 97, 97), "/img/delete.png"));    // Grey

        content.add(statsPanel);
        content.add(Box.createVerticalStrut(40));

        // 3. Alerts Section
        JLabel alertHeader = new JLabel("Notifications");
        alertHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        alertHeader.setForeground(new Color(60, 60, 70));
        alertHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(alertHeader);
        content.add(Box.createVerticalStrut(15));

        // 4. Alert Cards (Softer Look)
        if (!outList.isEmpty())
            content.add(createAlertCard("Out of Stock", "Restock needed:", outList, new Color(255, 235, 238), new Color(183, 28, 28))); // Soft Red Bg

        if (!expList.isEmpty())
            content.add(createAlertCard("Expired Items", "Discard items:", expList, new Color(255, 235, 238), new Color(183, 28, 28)));

        if (!soonList.isEmpty())
            content.add(createAlertCard("Expiring Soon", "Use efficiently:", soonList, new Color(255, 248, 225), new Color(245, 127, 23))); // Soft Amber Bg

        if (!lowList.isEmpty())
            content.add(createAlertCard("Running Low", "Monitor levels:", lowList, new Color(255, 248, 225), new Color(245, 127, 23)));

        if (outList.isEmpty() && expList.isEmpty() && soonList.isEmpty() && lowList.isEmpty()) {
            JPanel cleanState = new JPanel(new FlowLayout(FlowLayout.LEFT));
            cleanState.setOpaque(false);
            JLabel msg = new JLabel("Everything looks good! No alerts.");
            msg.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            msg.setForeground(new Color(39, 174, 96));
            cleanState.add(msg);
            content.add(cleanState);
        }

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        return scroll;
    }

    // --- CARD HELPER ---
    private JPanel createStatCard(String title, String val, Color color, String iconPath) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        // Left Colored Border Line
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, color),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Text Panel
        JPanel textP = new JPanel(new GridLayout(2, 1));
        textP.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(Color.GRAY);
        
        JLabel v = new JLabel(val);
        v.setFont(new Font("Segoe UI", Font.BOLD, 32));
        v.setForeground(Color.DARK_GRAY);
        
        textP.add(t);
        textP.add(v);

        card.add(textP, BorderLayout.CENTER);
        return card;
    }

    // --- ALERT CARD HELPER ---
    private JPanel createAlertCard(String title, String sub, List<String> items, Color bg, Color accent) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(bg);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, accent),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(2000, 300));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 15));
        t.setForeground(accent);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s = new JLabel(sub + " " + String.join(", ", items));
        s.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        s.setForeground(new Color(60, 60, 60));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(t);
        card.add(Box.createVerticalStrut(5));
        card.add(s);

        // Wrapper for margin
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.add(card);
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        return wrap;
    }
}