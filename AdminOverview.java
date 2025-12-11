import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AdminOverview extends JPanel {

    private AdminMainFrame adminMainFrame;

    public AdminOverview(AdminMainFrame adminMainFrame, List<Ingredient> inventory) {
        this.adminMainFrame = adminMainFrame;
        setLayout(new BorderLayout());
        setBackground(new Color(248, 249, 255));
        
        // Init with current inventory
        add(createCenterPanel(inventory), BorderLayout.CENTER);
    }

    private JPanel createCenterPanel(List<Ingredient> inventory) {
        // --- CALCULATION LOGIC ---
        int total = inventory.size();
        int low = 0, soon = 0, expired = 0, out = 0;
        
        List<String> outList = new ArrayList<>();
        List<String> expList = new ArrayList<>();
        List<String> soonList = new ArrayList<>();
        List<String> lowList = new ArrayList<>();

        for (Ingredient ing : inventory) {
            if (ing.getQuantity() == 0) { out++; outList.add(ing.getName()); }
            else if (ing.getQuantity() <= ing.getMinLevel()) { low++; lowList.add(ing.getName()); }
            
            if (ing.isExpired()) { expired++; expList.add(ing.getName()); }
            else if (ing.isExpiringSoon()) { soon++; soonList.add(ing.getName()); }
        }

        // --- UI LAYOUT ---
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(new Color(248, 249, 255));
        content.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        // Boxes
        JPanel boxes = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        boxes.setOpaque(false);
        boxes.add(createBox("Total", String.valueOf(total)));
        boxes.add(createBox("Low Stock", String.valueOf(low)));
        boxes.add(createBox("Expiring", String.valueOf(soon)));
        boxes.add(createBox("Expired", String.valueOf(expired)));
        boxes.add(createBox("Out of Stock", String.valueOf(out)));
        content.add(boxes);
        content.add(Box.createVerticalStrut(20));

        // Alerts
        if(!outList.isEmpty()) content.add(createAlert("Out of Stock", outList, new Color(255, 200, 200)));
        if(!expList.isEmpty()) content.add(createAlert("Expired", expList, new Color(255, 200, 200)));
        if(!soonList.isEmpty()) content.add(createAlert("Expiring Soon", soonList, new Color(255, 230, 180)));
        if(!lowList.isEmpty()) content.add(createAlert("Low Stock", lowList, new Color(255, 250, 200)));

        return new JScrollPane(content);
    }

    private JPanel createBox(String title, String val) {
        JPanel p = new JPanel(new BorderLayout());
        p.setPreferredSize(new Dimension(150, 80));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        JLabel t = new JLabel(title, SwingConstants.CENTER);
        JLabel v = new JLabel(val, SwingConstants.CENTER);
        v.setFont(new Font("Arial", Font.BOLD, 20));
        p.add(t, BorderLayout.NORTH);
        p.add(v, BorderLayout.CENTER);
        return p;
    }

    private JPanel createAlert(String title, List<String> items, Color bg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(bg);
        p.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
        p.setMaximumSize(new Dimension(1000, 100 + (items.size()*20)));
        
        JLabel l = new JLabel(title + ": " + String.join(", ", items));
        p.add(l);
        
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(p);
        wrapper.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        return wrapper;
    }

    public void refreshOverview(List<Ingredient> inventory) {
        removeAll();
        add(createCenterPanel(inventory), BorderLayout.CENTER);
        revalidate();
        repaint();
    }
}