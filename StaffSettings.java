import javax.swing.*;
import java.awt.*;

public class StaffSettings extends JPanel {

    private StaffMainFrame staffMainFrame;

    public StaffSettings(StaffMainFrame staffMainFrame) {
        this.staffMainFrame = staffMainFrame;

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
        
        JLabel subLabel = new JLabel("Manage account preferences and system options.");
        subLabel.setFont(StyleUtils.NORMAL_FONT);
        subLabel.setForeground(StyleUtils.LIGHT_TEXT);

        topPanel.add(settingsLabel);
        topPanel.add(Box.createVerticalStrut(5));
        topPanel.add(subLabel);
        add(topPanel, BorderLayout.NORTH);

        // --- CONTENT (Using GridBagLayout for alignment) ---
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding between items
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // --- SECTION 1: Account Info ---
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel sec1 = new JLabel("Account Information");
        sec1.setFont(new Font("Segoe UI", Font.BOLD, 16));
        contentPanel.add(sec1, gbc);

        // Row 1: Username
        gbc.gridwidth = 1; gbc.gridy++; gbc.weightx = 0;
        contentPanel.add(createLabel("Username:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JTextField userField = createField("staff (read-only)", false);
        contentPanel.add(userField, gbc);

        // Row 2: Password
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0;
        contentPanel.add(createLabel("Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        JPasswordField passField = new JPasswordField("password");
        styleField(passField, false);
        contentPanel.add(passField, gbc);

        // --- SECTION 2: Change Password ---
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.insets = new Insets(30, 10, 10, 10); // More top margin
        JLabel sec2 = new JLabel("Change Password");
        sec2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        contentPanel.add(sec2, gbc);

        // Row 3: Current
        gbc.gridy++; gbc.gridwidth = 1; gbc.weightx = 0; gbc.insets = new Insets(10, 10, 10, 10);
        contentPanel.add(createLabel("Current Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        contentPanel.add(createField("", true), gbc);

        // Row 4: New
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0;
        contentPanel.add(createLabel("New Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        contentPanel.add(createField("", true), gbc);

        // Row 5: Confirm
        gbc.gridx = 0; gbc.gridy++; gbc.weightx = 0;
        contentPanel.add(createLabel("Confirm Password:"), gbc);
        
        gbc.gridx = 1; gbc.weightx = 1;
        contentPanel.add(createField("", true), gbc);

        // Button
        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.WEST;
        JButton changeBtn = new JButton("Change Password");
        changeBtn.setBackground(Color.BLACK);
        changeBtn.setForeground(Color.WHITE);
        changeBtn.setFocusPainted(false);
        changeBtn.setFont(StyleUtils.NORMAL_FONT);
        changeBtn.setPreferredSize(new Dimension(180, 35));
        contentPanel.add(changeBtn, gbc);

        // Push everything up
        gbc.gridy++; gbc.weighty = 1;
        contentPanel.add(Box.createGlue(), gbc);

        // Wrapper for margin
        JPanel outerWrapper = new JPanel(new BorderLayout());
        outerWrapper.setOpaque(false);
        outerWrapper.setBorder(BorderFactory.createEmptyBorder(0, 40, 40, 40));
        outerWrapper.add(contentPanel, BorderLayout.CENTER);

        add(outerWrapper, BorderLayout.CENTER);
    }

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(StyleUtils.NORMAL_FONT);
        l.setPreferredSize(new Dimension(140, 30)); // Fixed width for alignment
        return l;
    }

    private JTextField createField(String text, boolean editable) {
        JTextField f = new JTextField(text);
        styleField(f, editable);
        return f;
    }

    private void styleField(JTextField f, boolean editable) {
        f.setFont(StyleUtils.NORMAL_FONT);
        f.setEditable(editable);
        f.setPreferredSize(new Dimension(200, 35));
        if(!editable) {
            f.setBackground(new Color(240, 240, 240));
            f.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        }
    }
}