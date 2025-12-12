import javax.swing.*;
import java.awt.*;

public class AdminSettings extends JPanel {

    private AdminMainFrame adminMainFrame;

    public AdminSettings(AdminMainFrame adminMainFrame) {
        this.adminMainFrame = adminMainFrame;
        setLayout(new BorderLayout());
        setBackground(StyleUtils.BG_COLOR);

        // --- HEADER ---
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(StyleUtils.BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 20, 40));

        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setFont(StyleUtils.HEADER_FONT);
        settingsLabel.setForeground(StyleUtils.DARK_TEXT);
        JLabel subLabel = new JLabel("System and account configuration.");
        subLabel.setFont(StyleUtils.NORMAL_FONT);
        subLabel.setForeground(StyleUtils.LIGHT_TEXT);

        topPanel.add(settingsLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- CONTENT SPLIT ---
        // Use GridBagLayout for the main container to ensure cards top-align
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(StyleUtils.BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0; 
        gbc.weightx = 0.5; gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.HORIZONTAL; 
        gbc.anchor = GridBagConstraints.NORTH; // CRITICAL: Top Align
        gbc.insets = new Insets(0, 0, 0, 20); // Gap

        // LEFT CARD
        JPanel leftCard = createCard("Admin Account");
        addFormRow(leftCard, "Username:", createField("admin (read-only)", false));
        addFormRow(leftCard, "Password:", createPassField("password", false));
        addHeader(leftCard, "Change Password");
        addFormRow(leftCard, "Current:", createPassField("", true));
        addFormRow(leftCard, "New:", createPassField("", true));
        addFormRow(leftCard, "Confirm:", createPassField("", true));
        addBtn(leftCard, "Change Password");
        contentPanel.add(leftCard, gbc);

        // RIGHT CARD
        gbc.gridx = 1; gbc.insets = new Insets(0, 20, 0, 0); // Gap
        JPanel rightCard = createCard("Staff Account Management");
        addFormRow(rightCard, "Username:", createField("staff (read-only)", false));
        addFormRow(rightCard, "Password:", createPassField("password", false));
        addBtn(rightCard, "Reset Password");
        
        addHeader(rightCard, "Backup & Restore");
        JLabel last = new JLabel("Last Backup: 12/12/2025");
        last.setFont(new Font("Segoe UI", Font.ITALIC, 12)); last.setForeground(Color.GRAY);
        JPanel lastP = new JPanel(new FlowLayout(FlowLayout.LEFT)); lastP.setOpaque(false); lastP.add(last);
        rightCard.add(lastP);
        
        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); btnP.setOpaque(false);
        JButton b1 = new JButton("Backup"); styleBtn(b1);
        JButton b2 = new JButton("Restore"); styleBtn(b2);
        btnP.add(b1); btnP.add(Box.createHorizontalStrut(10)); btnP.add(b2);
        
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT)); wrap.setOpaque(false); wrap.add(btnP);
        rightCard.add(wrap);
        
        contentPanel.add(rightCard, gbc);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createCard(String t) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230,230,230)),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(15));
        return p;
    }

    private void addFormRow(JPanel p, String l, JComponent f) {
        JPanel row = new JPanel(new BorderLayout(10, 0)); row.setOpaque(false);
        row.setMaximumSize(new Dimension(1000, 35)); row.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(l); lbl.setPreferredSize(new Dimension(90, 35));
        lbl.setFont(StyleUtils.NORMAL_FONT);
        row.add(lbl, BorderLayout.WEST); row.add(f, BorderLayout.CENTER);
        p.add(row); p.add(Box.createVerticalStrut(10));
    }

    private void addHeader(JPanel p, String t) {
        p.add(Box.createVerticalStrut(15));
        JLabel l = new JLabel(t); l.setFont(new Font("Segoe UI", Font.BOLD, 14));
        l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(10));
    }

    private void addBtn(JPanel p, String t) {
        JButton b = new JButton(t); styleBtn(b);
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT)); wrap.setOpaque(false); wrap.add(b);
        wrap.setAlignmentX(LEFT_ALIGNMENT);
        p.add(wrap); p.add(Box.createVerticalStrut(5));
    }

    private void styleBtn(JButton b) {
        b.setBackground(Color.BLACK); b.setForeground(Color.WHITE);
        b.setFocusPainted(false); b.setPreferredSize(new Dimension(140, 35));
    }

    private JTextField createField(String t, boolean e) {
        JTextField f = new JTextField(t); styleF(f, e); return f;
    }
    private JPasswordField createPassField(String t, boolean e) {
        JPasswordField f = new JPasswordField(t); styleF(f, e); if(!e) f.setEchoChar('\u2022'); return f;
    }
    private void styleF(JTextField f, boolean e) {
        f.setFont(StyleUtils.NORMAL_FONT); f.setEditable(e);
        if(!e) { f.setBackground(new Color(245,245,245)); f.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); }
    }
}