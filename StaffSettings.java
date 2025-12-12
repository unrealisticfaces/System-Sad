import javax.swing.*;
import java.awt.*;

public class StaffSettings extends JPanel {

    private StaffMainFrame staffMainFrame;

    public StaffSettings(StaffMainFrame staffMainFrame) {
        this.staffMainFrame = staffMainFrame;
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
        
        JLabel subLabel = new JLabel("Manage account preferences.");
        subLabel.setFont(StyleUtils.NORMAL_FONT);
        subLabel.setForeground(StyleUtils.LIGHT_TEXT);

        topPanel.add(settingsLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- CONTENT FORM ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // --- SECT 1 ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel sec1 = new JLabel("Account Information");
        sec1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        contentPanel.add(sec1, gbc);

        // Row 1
        gbc.gridy++; gbc.gridwidth = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(createLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(createField("staff (read-only)", false), gbc);

        // Row 2
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(createLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(createPassField("password", false), gbc);

        // --- SECT 2 ---
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(30, 15, 10, 15);
        JLabel sec2 = new JLabel("Change Password");
        sec2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        contentPanel.add(sec2, gbc);

        // Row 3
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.gridy++; gbc.gridwidth = 1; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(createLabel("Current Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(createPassField("", true), gbc);

        // Row 4
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(createLabel("New Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(createPassField("", true), gbc);

        // Row 5
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0; gbc.anchor = GridBagConstraints.EAST;
        contentPanel.add(createLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1; gbc.anchor = GridBagConstraints.WEST;
        contentPanel.add(createPassField("", true), gbc);

        // Button
        gbc.gridx = 1; gbc.gridy++; gbc.anchor = GridBagConstraints.WEST;
        JButton btn = new JButton("Change Password");
        btn.setBackground(Color.BLACK); btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false); btn.setPreferredSize(new Dimension(160, 35));
        contentPanel.add(btn, gbc);

        // Push to top
        gbc.gridy++; gbc.weighty = 1;
        contentPanel.add(Box.createGlue(), gbc);

        // Wrapper
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));
        wrapper.add(contentPanel, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private JLabel createLabel(String t) {
        JLabel l = new JLabel(t);
        l.setFont(StyleUtils.NORMAL_FONT);
        l.setHorizontalAlignment(SwingConstants.RIGHT);
        return l;
    }
    private JTextField createField(String t, boolean e) {
        JTextField f = new JTextField(t);
        styleField(f, e); return f;
    }
    private JPasswordField createPassField(String t, boolean e) {
        JPasswordField f = new JPasswordField(t);
        styleField(f, e); if(!e) f.setEchoChar('\u2022'); return f;
    }
    private void styleField(JTextField f, boolean e) {
        f.setFont(StyleUtils.NORMAL_FONT);
        f.setEditable(e);
        f.setPreferredSize(new Dimension(250, 35));
        if(!e) { f.setBackground(new Color(245, 245, 245)); f.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY)); }
    }
}