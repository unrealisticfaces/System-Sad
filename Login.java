import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class Login extends JFrame {

    // --- Colors & Fonts ---
    private final Color PRIMARY_COLOR = new Color(46, 204, 113); // Emerald Green
    private final Color BACKGROUND_COLOR = new Color(240, 242, 245); // Light Grey
    private final Color TEXT_COLOR = new Color(51, 51, 51);
    private final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);

    // --- Logic Data ---
    private Map<String, String> staffCredentials = new HashMap<>();
    private Map<String, String> adminCredentials = new HashMap<>();
    private boolean isAdmin = false;

    public Login() {
        // Init Mock Data
        staffCredentials.put("1", "1");
        adminCredentials.put("2", "2");

        // Frame Settings
        setTitle("Ingredient Inventory System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 1. Background Panel
        JPanel mainBackground = new JPanel();
        mainBackground.setLayout(new GridBagLayout()); // Centers the card
        mainBackground.setBackground(BACKGROUND_COLOR);
        add(mainBackground, BorderLayout.CENTER);

        // 2. The Floating Card (Custom Rounded Panel)
        JPanel loginCard = new RoundedPanel(20, Color.WHITE);
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
        loginCard.setBorder(new EmptyBorder(40, 50, 40, 50)); 
        
        // --- Logo ---
        ImageIcon logoIcon = null;
        try {
            java.net.URL imgURL = getClass().getResource("/img/logo.png");
            if (imgURL != null) {
                logoIcon = new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) { /* Ignore */ }
        
        JLabel logoLabel = new JLabel(logoIcon);
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // --- Title ---
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subTitleLabel = new JLabel("Login to manage inventory");
        subTitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitleLabel.setForeground(Color.GRAY);
        subTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Toggle Switch ---
        JPanel togglePanel = createToggle();
        togglePanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // --- Inputs ---
        JTextField userField = createStyledTextField("Username");
        JPasswordField passField = createStyledPasswordField("Password");

        // --- Login Button ---
        JButton loginBtn = new JButton("LOGIN");
        styleButton(loginBtn);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(300, 45));

        // Logic
        loginBtn.addActionListener(e -> performLogin(userField, passField));

        // --- Assembly ---
        loginCard.add(logoLabel);
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(titleLabel);
        loginCard.add(subTitleLabel);
        loginCard.add(Box.createVerticalStrut(20));
        loginCard.add(togglePanel);
        loginCard.add(Box.createVerticalStrut(25));
        
        // Input Wrappers for alignment
        loginCard.add(wrapComponent(userField));
        loginCard.add(Box.createVerticalStrut(15));
        loginCard.add(wrapComponent(passField));
        loginCard.add(Box.createVerticalStrut(30));
        
        loginCard.add(loginBtn);

        mainBackground.add(loginCard); // Add card to background

        setVisible(true);
        SwingUtilities.invokeLater(() -> loginCard.requestFocusInWindow());
    }

    // --- Helper: Clean Login Logic ---
    private void performLogin(JTextField userField, JPasswordField passField) {
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword()).trim();
        
        if(user.equals("Username")) user = "";
        if(pass.equals("Password")) pass = "";

        Map<String, String> targetMap = isAdmin ? adminCredentials : staffCredentials;

        if (targetMap.containsKey(user) && targetMap.get(user).equals(pass)) {
            dispose();
            if (isAdmin) {
               try { new AdminMainFrame().setVisible(true); } catch(Exception ex) { ex.printStackTrace(); }
            } else {
               try { new StaffMainFrame().setVisible(true); } catch(Exception ex) { ex.printStackTrace(); }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- Helper: Create Custom Toggle ---
    private JPanel createToggle() {
        int w = 240; int h = 36;
        JPanel container = new JPanel(null);
        container.setPreferredSize(new Dimension(w, h));
        container.setMaximumSize(new Dimension(w, h));
        container.setOpaque(false);

        // Background (Grey Pill)
        RoundedPanel bg = new RoundedPanel(h, new Color(230, 230, 230));
        bg.setBounds(0, 0, w, h);

        // Slider (White Pill)
        JPanel slider = new RoundedPanel(h - 4, Color.WHITE);
        slider.setBounds(2, 2, (w/2)-2, h-4);
        
        // Labels
        JLabel lblStaff = new JLabel("Staff", SwingConstants.CENTER);
        lblStaff.setFont(MAIN_FONT);
        lblStaff.setBounds(0,0, w/2, h);
        lblStaff.setForeground(Color.BLACK);
        
        JLabel lblAdmin = new JLabel("Admin", SwingConstants.CENTER);
        lblAdmin.setFont(MAIN_FONT);
        lblAdmin.setBounds(w/2,0, w/2, h);
        lblAdmin.setForeground(Color.GRAY);

        // Click Logic
        container.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                isAdmin = !isAdmin;
                // Animate Slider
                Timer t = new Timer(10, new ActionListener() {
                    int targetX = isAdmin ? (w/2) + 2 : 2;
                    public void actionPerformed(ActionEvent ae) {
                        int curX = slider.getX();
                        if(Math.abs(curX - targetX) < 5) {
                            slider.setLocation(targetX, 2);
                            ((Timer)ae.getSource()).stop();
                            // Update Text Colors
                            lblStaff.setForeground(isAdmin ? Color.GRAY : Color.BLACK);
                            lblAdmin.setForeground(isAdmin ? Color.BLACK : Color.GRAY);
                        } else {
                            int dir = targetX > curX ? 5 : -5;
                            slider.setLocation(curX + dir, 2);
                        }
                    }
                });
                t.start();
            }
        });

        container.add(lblStaff);
        container.add(lblAdmin);
        container.add(slider); 
        container.setComponentZOrder(lblStaff, 0);
        container.setComponentZOrder(lblAdmin, 0);
        container.setComponentZOrder(slider, 1);
        container.add(bg);
        container.setComponentZOrder(bg, 2);
        
        return container;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(isFocusOwner()) {
                    g.setColor(PRIMARY_COLOR);
                    g.fillRect(0, getHeight()-2, getWidth(), 2);
                }
            }
        };
        styleFieldCommon(field, placeholder);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField(placeholder) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(isFocusOwner()) {
                    g.setColor(PRIMARY_COLOR);
                    g.fillRect(0, getHeight()-2, getWidth(), 2);
                }
            }
        };
        field.setEchoChar((char)0); 
        styleFieldCommon(field, placeholder);
        return field;
    }

    private void styleFieldCommon(JTextField field, String placeholder) {
        field.setFont(MAIN_FONT);
        field.setForeground(Color.GRAY);
        field.setBackground(new Color(245, 246, 250)); 
        field.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15)); 
        field.setPreferredSize(new Dimension(300, 45));
        field.setMaximumSize(new Dimension(300, 45));
        
        field.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(Color.BLACK);
                    if(field instanceof JPasswordField) ((JPasswordField)field).setEchoChar('\u2022');
                }
                field.setBackground(Color.WHITE); 
            }
            public void focusLost(FocusEvent e) {
                if (field.getText().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(Color.GRAY);
                    field.setBackground(new Color(245, 246, 250));
                    if(field instanceof JPasswordField) ((JPasswordField)field).setEchoChar((char)0);
                }
            }
        });
    }

    private void styleButton(JButton btn) {
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setBackground(PRIMARY_COLOR);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btn.setBackground(PRIMARY_COLOR.darker()); }
            public void mouseExited(MouseEvent e) { btn.setBackground(PRIMARY_COLOR); }
        });
    }

    private JPanel wrapComponent(JComponent c) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.add(c);
        return p;
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false); 
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }
    }
}