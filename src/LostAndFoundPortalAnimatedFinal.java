import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

/**
 * LostAndFoundPortalAnimatedFinal
 * - Pure Swing, animated (fade) transitions
 * - Sign up / Sign in with persisted users.txt
 * - Items persisted in items.txt (newest shown on top)
 * - Profile dialog with Change Password and Logout
 * - Aligned JList for items, animated UI controls
 */
public class LostAndFoundPortalAnimatedFinal {
    private static java.util.List<String> items = new java.util.ArrayList<>();
    private static java.util.Map<String, String> users = new java.util.HashMap<>();

    private static DefaultListModel<String> listModel = new DefaultListModel<>();
    private static JList<String> itemsList;
    private static CardLayout cardLayout;
    private static JPanel containerPanel;

    private static final String USERS_FILE = "users.txt";
    private static final String ITEMS_FILE = "items.txt";

    private static JLabel mainHeaderLabel;
    private static String currentUser = null;

    public static void main(String[] args) {
        loadUsers();
        loadItems();
        SwingUtilities.invokeLater(LostAndFoundPortalAnimatedFinal::createUI);
    }

    private static void createUI() {
        JFrame frame = new JFrame("Campus Lost & Found Portal — Animated");
        frame.setSize(920, 640);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        containerPanel = new JPanel();
        cardLayout = new CardLayout();
        containerPanel.setLayout(cardLayout);

        FadePanel loginCard = new FadePanel(createLoginPage(frame));
        FadePanel signupCard = new FadePanel(createSignUpPage(frame));
        FadePanel mainCard   = new FadePanel(createMainPage(frame));

        containerPanel.add(loginCard, "login");
        containerPanel.add(signupCard, "signup");
        containerPanel.add(mainCard, "main");

        frame.setContentPane(containerPanel);

        cardLayout.show(containerPanel, "login");
        loginCard.setAlpha(1f);

        frame.setVisible(true);
    }

    private static JPanel createLoginPage(JFrame frame) {
        JPanel root = new GradientPanel(new Color(236,243,255), new Color(246,236,255));
        root.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Campus Lost & Found Portal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(new Color(20, 45, 100));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(24,24,18,24);
        root.add(title, gbc);

        gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.WEST;
        JLabel userL = new JLabel("Username:");
        userL.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx=0; gbc.gridy=1; gbc.insets = new Insets(8,24,8,12);
        root.add(userL, gbc);

        JTextField userField = new JTextField(20);
        gbc.gridx=1; gbc.gridy=1; gbc.insets = new Insets(8,12,8,24);
        root.add(userField, gbc);

        JLabel passL = new JLabel("Password:");
        passL.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx=0; gbc.gridy=2;
        root.add(passL, gbc);

        JPasswordField passField = new JPasswordField(20);
        gbc.gridx=1; gbc.gridy=2;
        root.add(passField, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        JButton signIn = new JButton("Sign In");
        JButton signUp = new JButton("Sign Up");
        animatedStyle(signIn);
        animatedStyle(signUp);
        btnRow.add(signIn); btnRow.add(signUp);

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; gbc.insets = new Insets(18,24,10,24);
        root.add(btnRow, gbc);

        JLabel hint = new JLabel("Tip: create a new account via Sign Up");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(new Color(80,80,110));
        gbc.gridy=4; gbc.insets = new Insets(8,24,24,24);
        root.add(hint, gbc);

        signIn.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter both username and password.", "Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!users.containsKey(user)) {
                JOptionPane.showMessageDialog(frame, "User not found. Please sign up first.", "Not found", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!users.get(user).equals(pass)) {
                JOptionPane.showMessageDialog(frame, "Incorrect password.", "Auth failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentUser = user;
            updateMainHeader();
            fadeTo("main");
        });

        signUp.addActionListener(e -> fadeTo("signup"));

        return root;
    }

    private static JPanel createSignUpPage(JFrame frame) {
        JPanel root = new GradientPanel(new Color(236,243,255), new Color(246,236,255));
        root.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Create New Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(20, 45, 100));
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; gbc.insets=new Insets(18,18,18,18);
        root.add(title, gbc);

        gbc.gridwidth=1; gbc.anchor=GridBagConstraints.WEST;
        JLabel userL = new JLabel("Choose username:");
        userL.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx=0; gbc.gridy=1; gbc.insets=new Insets(8,24,8,12);
        root.add(userL, gbc);

        JTextField userField = new JTextField(20);
        gbc.gridx=1; gbc.gridy=1; gbc.insets=new Insets(8,12,8,24);
        root.add(userField, gbc);

        JLabel passL = new JLabel("Choose password:");
        passL.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx=0; gbc.gridy=2;
        root.add(passL, gbc);

        JPasswordField passField = new JPasswordField(20);
        gbc.gridx=1; gbc.gridy=2;
        root.add(passField, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        JButton register = new JButton("Register");
        JButton back = new JButton("Back");
        animatedStyle(register);
        animatedStyle(back);
        btnRow.add(register); btnRow.add(back);

        gbc.gridx=0; gbc.gridy=3; gbc.gridwidth=2; gbc.insets=new Insets(16,24,12,24);
        root.add(btnRow, gbc);

        register.addActionListener(e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill both fields.", "Missing", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (users.containsKey(user)) {
                JOptionPane.showMessageDialog(frame, "Username already exists. Choose another.", "Exists", JOptionPane.WARNING_MESSAGE);
                return;
            }
            users.put(user, pass);
            saveUsers();
            JOptionPane.showMessageDialog(frame, "Account created. Please sign in.", "Success", JOptionPane.INFORMATION_MESSAGE);
            fadeTo("login");
        });

        back.addActionListener(e -> fadeTo("login"));
        return root;
    }

    private static JPanel createMainPage(JFrame frame) {
        JPanel root = new GradientPanel(new Color(255,255,255), new Color(240,250,255));
        root.setLayout(new BorderLayout(12,12));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        mainHeaderLabel = new JLabel("Welcome", SwingConstants.CENTER);
        mainHeaderLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        mainHeaderLabel.setBorder(BorderFactory.createEmptyBorder(18,10,18,10));
        mainHeaderLabel.setForeground(new Color(22,50,110));
        headerPanel.add(mainHeaderLabel, BorderLayout.CENTER);

        JButton profileBtn = new JButton("Profile ▾");
        animatedStyle(profileBtn);
        profileBtn.addActionListener(e -> showProfileDialog(frame));
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightTop.setOpaque(false);
        rightTop.add(profileBtn);
        headerPanel.add(rightTop, BorderLayout.EAST);

        root.add(headerPanel, BorderLayout.NORTH);

        JPanel controlCardHolder = new JPanel(new GridBagLayout());
        controlCardHolder.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        JPanel controlCard = new RoundedPanel();
        controlCard.setPreferredSize(new Dimension(820, 150));
        controlCard.setLayout(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();

        JButton lostBtn = new JButton("Report Lost Item");
        JButton foundBtn = new JButton("Report Found Item");
        JButton claimBtn = new JButton("Mark as Claimed");

        animatedStyle(lostBtn);
        animatedStyle(foundBtn);
        animatedStyle(claimBtn);

        c2.insets = new Insets(10,12,8,12);
        c2.gridx=0; c2.gridy=0; controlCard.add(lostBtn, c2);
        c2.gridx=1; controlCard.add(foundBtn, c2);
        c2.gridx=2; controlCard.add(claimBtn, c2);

        c2.gridx=0; c2.gridy=1; c2.gridwidth=2; c2.anchor=GridBagConstraints.WEST;
        JTextField searchField = new JTextField(36);
        JButton searchBtn = new JButton("Search Items");
        animatedStyle(searchBtn);
        controlCard.add(searchField, c2);
        c2.gridx=2; c2.gridwidth=1; c2.anchor=GridBagConstraints.CENTER;
        controlCard.add(searchBtn, c2);

        gbc.gridx=0; gbc.gridy=0;
        controlCardHolder.add(controlCard, gbc);
        root.add(controlCardHolder, BorderLayout.CENTER);

        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(6,80,20,80));
        itemsPanel.setOpaque(false);

        RoundedPanel itemsInside = new RoundedPanel();
        itemsInside.setLayout(new BorderLayout());
        itemsInside.setPreferredSize(new Dimension(820, 300));
        itemsInside.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));

        JLabel listLabel = new JLabel("Recent Lost & Found Items");
        listLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        itemsInside.add(listLabel, BorderLayout.NORTH);

        listModel = new DefaultListModel<>();
        itemsList = new JList<>(listModel);
        itemsList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        itemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane sc = new JScrollPane(itemsList);
        itemsInside.add(sc, BorderLayout.CENTER);

        itemsPanel.add(itemsInside, BorderLayout.CENTER);
        root.add(itemsPanel, BorderLayout.SOUTH);

        lostBtn.addActionListener(e -> reportItemDialog(frame, "Lost"));
        foundBtn.addActionListener(e -> reportItemDialog(frame, "Found"));
        claimBtn.addActionListener(e -> {
            String sel = itemsList.getSelectedValue();
            if (sel == null) {
                String q = JOptionPane.showInputDialog(frame, "Enter item name to mark as claimed:");
                if (q == null || q.trim().isEmpty()) return;
                markAsClaimedByName(q.trim());
            } else {
                int r = JOptionPane.showConfirmDialog(frame, "Mark selected item as claimed?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (r == JOptionPane.YES_OPTION) markAsClaimedByExact(sel);
            }
        });

        searchBtn.addActionListener(e -> {
            String q = searchField.getText().trim().toLowerCase();
            if (q.isEmpty()) { refreshItemsList(); return; }
            DefaultListModel<String> tmp = new DefaultListModel<>();
            for (String it : items) if (it.toLowerCase().contains(q)) tmp.addElement(it);
            listModel = tmp;
            itemsList.setModel(listModel);
        });

        refreshItemsList();

        return root;
    }

    private static void showProfileDialog(Window owner) {
        JDialog dlg = new JDialog(owner, "Profile", Dialog.ModalityType.APPLICATION_MODAL);
        dlg.setSize(380, 260);
        dlg.setLocationRelativeTo(owner);

        JPanel root = new GradientPanel(new Color(255,255,255), new Color(245,250,255));
        root.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel head = new JLabel("Profile");
        head.setFont(new Font("Segoe UI", Font.BOLD, 18));
        gbc.gridx=0; gbc.gridy=0; gbc.gridwidth=2; gbc.insets=new Insets(12,12,12,12);
        root.add(head, gbc);

        gbc.gridwidth=1; gbc.anchor = GridBagConstraints.WEST;
        JLabel userL = new JLabel("Username:");
        userL.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx=0; gbc.gridy=1; gbc.insets=new Insets(8,12,8,8);
        root.add(userL, gbc);

        JLabel userVal = new JLabel(currentUser == null ? "—" : currentUser);
        userVal.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridx=1; gbc.gridy=1; gbc.insets=new Insets(8,8,8,12);
        root.add(userVal, gbc);

        JButton changePass = new JButton("Change Password");
        JButton logout = new JButton("Logout");
        animatedStyle(changePass); animatedStyle(logout);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.add(changePass); btnRow.add(logout);

        gbc.gridx=0; gbc.gridy=2; gbc.gridwidth=2; gbc.insets=new Insets(18,12,12,12);
        root.add(btnRow, gbc);

        changePass.addActionListener(e -> {
            JPanel p = new JPanel(new GridLayout(3,2,6,6));
            JPasswordField oldP = new JPasswordField();
            JPasswordField newP = new JPasswordField();
            JPasswordField confP = new JPasswordField();
            p.add(new JLabel("Old password:")); p.add(oldP);
            p.add(new JLabel("New password:")); p.add(newP);
            p.add(new JLabel("Confirm new:")); p.add(confP);
            int res = JOptionPane.showConfirmDialog(dlg, p, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (res != JOptionPane.OK_OPTION) return;
            if (currentUser == null) { JOptionPane.showMessageDialog(dlg, "No user signed in."); return; }
            String oldS = new String(oldP.getPassword()), newS = new String(newP.getPassword()), confS = new String(confP.getPassword());
            if (oldS.isEmpty() || newS.isEmpty()) { JOptionPane.showMessageDialog(dlg, "Fill all fields."); return; }
            if (!users.getOrDefault(currentUser, "").equals(oldS)) { JOptionPane.showMessageDialog(dlg, "Old password incorrect."); return; }
            if (!newS.equals(confS)) { JOptionPane.showMessageDialog(dlg, "New passwords do not match."); return; }
            users.put(currentUser, newS);
            saveUsers();
            JOptionPane.showMessageDialog(dlg, "Password changed successfully.");
        });

        logout.addActionListener(e -> {
            currentUser = null;
            updateMainHeader();
            dlg.dispose();
            fadeTo("login");
        });

        dlg.setContentPane(root);
        try { dlg.setOpacity(0f); } catch (Exception ignored) {}
        dlg.setVisible(true);
    }

    // ---------------- Report Lost/Found Items (with Mobile) ----------------
    private static void reportItemDialog(Component parent, String type) {
        JTextField nameF = new JTextField();
        JTextField colorF = new JTextField();
        JTextField locF = new JTextField();
        JTextField mobileF = new JTextField(); // Mobile Number Field

        Object[] objs = {
            "Item name:", nameF,
            "Color:", colorF,
            (type.equals("Lost") ? "Location lost:" : "Location found:"), locF,
            "Mobile Number:", mobileF
        };

        int res = JOptionPane.showConfirmDialog(parent, objs, "Report " + type + " Item", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String name = nameF.getText().trim();
        String color = colorF.getText().trim();
        String loc = locF.getText().trim();
        String mobile = mobileF.getText().trim();

        if (name.isEmpty() || color.isEmpty() || loc.isEmpty() || mobile.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "All fields required.");
            return;
        }

        String entry = type + ": " + name 
            + " | Color: " + color 
            + " | Location: " + loc 
            + " | Mobile: " + mobile
            + (currentUser != null ? " | Reported by: " + currentUser : "");

        items.add(0, entry);
        saveItems();
        animateNewItem(entry);
    }

    private static void animateNewItem(String entry) {
        listModel.add(0, entry);
        itemsList.setModel(listModel);
        itemsList.setSelectedIndex(0);
        Color original = itemsList.getSelectionBackground();
        itemsList.setSelectionBackground(new Color(180,230,255));
        javax.swing.Timer t = new javax.swing.Timer(140, null);
        final int[] cnt = {0};
        t.addActionListener(ev -> {
            cnt[0]++; 
            if (cnt[0] > 4) { itemsList.setSelectionBackground(original); t.stop(); }
        });
        t.start();
    }

    private static void markAsClaimedByName(String name) {
        for (int i=0;i<items.size();i++) {
            if (items.get(i).toLowerCase().contains(name.toLowerCase())) {
                items.set(i, items.get(i) + " | CLAIMED");
                saveItems();
                refreshItemsList();
                JOptionPane.showMessageDialog(null, "Marked as claimed.");
                return;
            }
        }
        JOptionPane.showMessageDialog(null, "Item not found.");
    }

    private static void markAsClaimedByExact(String sel) {
        for (int i=0;i<items.size();i++) {
            if (items.get(i).equals(sel)) {
                items.set(i, items.get(i) + " | CLAIMED");
                saveItems();
                refreshItemsList();
                JOptionPane.showMessageDialog(null, "Marked as claimed.");
                return;
            }
        }
    }

    private static void refreshItemsList() {
        listModel = new DefaultListModel<>();
        for (String s : items) listModel.addElement(s);
        itemsList.setModel(listModel);
    }

    private static void fadeTo(String card) {
        cardLayout.show(containerPanel, card);
    }

    private static void updateMainHeader() {
        mainHeaderLabel.setText(currentUser == null ? "Welcome" : "Welcome, " + currentUser);
    }

    private static void animatedStyle(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(100,180,240));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
    }

    private static void loadUsers() {
        File f = new File(USERS_FILE);
        if (!f.exists()) return;
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(":",2);
                if (parts.length==2) users.put(parts[0], parts[1]);
            }
        } catch (Exception e){ e.printStackTrace(); }
    }

    private static void saveUsers() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(USERS_FILE))) {
            for (var ent : users.entrySet()) pw.println(ent.getKey() + ":" + ent.getValue());
        } catch (Exception e){ e.printStackTrace(); }
    }

    private static void loadItems() {
        File f = new File(ITEMS_FILE);
        if (!f.exists()) return;
        try (Scanner sc = new Scanner(f)) {
            while (sc.hasNextLine()) items.add(sc.nextLine());
        } catch (Exception e){ e.printStackTrace(); }
    }

    private static void saveItems() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ITEMS_FILE))) {
            for (String s: items) pw.println(s);
        } catch (Exception e){ e.printStackTrace(); }
    }

    // ----------------- Panels --------------------
    static class FadePanel extends JPanel {
        private float alpha = 1f;
        public FadePanel(JPanel inner) { setLayout(new BorderLayout()); add(inner, BorderLayout.CENTER); setOpaque(false); }
        public void setAlpha(float a) { alpha = a; repaint(); }
        protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g.create(); g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha)); super.paintComponent(g2); g2.dispose(); }
    }

    static class GradientPanel extends JPanel {
        private Color c1,c2;
        public GradientPanel(Color a, Color b) { c1=a; c2=b; setOpaque(false);}
        protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g; g2.setPaint(new GradientPaint(0,0,c1,0,getHeight(),c2)); g2.fillRect(0,0,getWidth(),getHeight()); super.paintComponent(g); }
    }

    static class RoundedPanel extends JPanel {
        public RoundedPanel() { setOpaque(false); setBackground(new Color(255,255,255,200)); }
        protected void paintComponent(Graphics g) { Graphics2D g2 = (Graphics2D) g.create(); g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON); g2.setColor(getBackground()); g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16); super.paintComponent(g2); g2.dispose();}
    }
}
