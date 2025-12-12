import javax.swing.*;
import java.awt.*;

public class AdminSettings extends JPanel {

    private AdminMainFrame adminMainFrame;

    public AdminSettings(AdminMainFrame adminMainFrame) {
        this.adminMainFrame = adminMainFrame;

        setLayout(new BorderLayout());
        setBackground(StyleUtils.BG_COLOR);

        // Header
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(StyleUtils.BG_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(25, 40, 20, 40));

        JLabel settingsLabel = new JLabel("Settings");
        settingsLabel.setFont(StyleUtils.HEADER_FONT);
        settingsLabel.setForeground(StyleUtils.DARK_TEXT);
        
        JLabel subLabel = new JLabel("Manage system and account preferences.");
        subLabel.setFont(StyleUtils.NORMAL_FONT);
        subLabel.setForeground(StyleUtils.LIGHT_TEXT);

        topPanel.add(settingsLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- SPLIT PANEL ---
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 40, 0)); // 2 Cols, 40px gap
        contentPanel.setBackground(StyleUtils.BG_COLOR);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));

        // LEFT CARD (Admin Info)
        JPanel leftCard = createCardPanel("Admin Account");
        addFormRow(leftCard, "Username:", createField("admin (read-only)", false));
        addFormRow(leftCard, "Password:", createPassField("password", false));
        
        addSectionHeader(leftCard, "Change Password");
        addFormRow(leftCard, "Current:", createField("", true));
        addFormRow(leftCard, "New:", createField("", true));
        addFormRow(leftCard, "Confirm:", createField("", true));
        
        JButton changeBtn = new JButton("Change Password");
        styleBlackBtn(changeBtn);
        addControl(leftCard, changeBtn);

        // RIGHT CARD (Staff Info & Backup)
        JPanel rightCard = createCardPanel("Staff Account Management");
        addFormRow(rightCard, "Username:", createField("staff (read-only)", false));
        addFormRow(rightCard, "Password:", createPassField("password", false));
        
        JButton resetBtn = new JButton("Reset Password");
        styleBlackBtn(resetBtn);
        addControl(rightCard, resetBtn);

        addFormRow(rightCard, "Temp Pass:", createField("staff123", false));

        addSectionHeader(rightCard, "System Backup");
        JLabel lastBackup = new JLabel("Last Backup: 12/12/2025 10:00");
        lastBackup.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lastBackup.setForeground(Color.GRAY);
        
        JPanel backupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 10));
        backupPanel.setOpaque(false);
        backupPanel.add(lastBackup);
        
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        btnPanel.setOpaque(false);
        JButton backupBtn = new JButton("Backup");
        styleBlackBtn(backupBtn);
        JButton restoreBtn = new JButton("Restore");
        styleBlackBtn(restoreBtn);
        btnPanel.add(backupBtn);
        btnPanel.add(Box.createHorizontalStrut(10));
        btnPanel.add(restoreBtn);

        addControl(rightCard, backupPanel);
        addControl(rightCard, btnPanel);


        contentPanel.add(leftCard);
        contentPanel.add(rightCard);

        add(contentPanel, BorderLayout.CENTER);
    }

    // --- HELPERS FOR ALIGNMENT ---
    
    private JPanel createCardPanel(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 18));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(t);
        p.add(Box.createVerticalStrut(20));
        return p;
    }

    private void addFormRow(JPanel p, String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(1000, 35));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel l = new JLabel(label);
        l.setPreferredSize(new Dimension(80, 35)); // Fixed Label Width enforces alignment
        l.setFont(StyleUtils.NORMAL_FONT);
        
        row.add(l, BorderLayout.WEST);
        row.add(field, BorderLayout.CENTER);
        
        p.add(row);
        p.add(Box.createVerticalStrut(10));
    }

    private void addSectionHeader(JPanel p, String text) {
        p.add(Box.createVerticalStrut(20));
        JLabel l = new JLabel(text);
        l.setFont(new Font("Segoe UI", Font.BOLD, 16));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(Box.createVerticalStrut(10));
    }

    private void addControl(JPanel p, JComponent c) {
        JPanel wrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        wrap.setOpaque(false);
        wrap.setMaximumSize(new Dimension(1000, 40));
        wrap.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrap.add(c);
        p.add(wrap);
        p.add(Box.createVerticalStrut(10));
    }

    private JTextField createField(String text, boolean editable) {
        JTextField f = new JTextField(text);
        styleField(f, editable);
        return f;
    }

    private JPasswordField createPassField(String text, boolean editable) {
        JPasswordField f = new JPasswordField(text);
        styleField(f, editable);
        if(!editable) f.setEchoChar('\u2022');
        return f;
    }

    private void styleField(JTextField f, boolean editable) {
        f.setFont(StyleUtils.NORMAL_FONT);
        f.setEditable(editable);
        if(!editable) {
            f.setBackground(new Color(245, 245, 245));
            f.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
    }

    private void styleBlackBtn(JButton b) {
        b.setBackground(Color.BLACK);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setFont(StyleUtils.NORMAL_FONT);
        b.setPreferredSize(new Dimension(140, 35));
    }
}