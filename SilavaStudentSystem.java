import java.awt.*;
import java.io.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.*;

public class SilavaStudentSystem extends JFrame {
    private final JTable table;
    private final DefaultTableModel model;
    private final JTextField txtID, txtFirstName, txtLastName, txtLab1, txtLab2, txtLab3, txtPrelim, txtAttendance;

    // Modern Dark Palette
    private final Color BG_BODY      = new Color(18, 18, 22);
    private final Color CARD_BG      = new Color(28, 28, 33);
    private final Color ACCENT_PURPLE = new Color(129, 140, 248);
    private final Color TEXT_MAIN    = new Color(243, 244, 246);
    private final Color TEXT_DIM     = new Color(156, 163, 175);
    private final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private final Color DANGER_RED   = new Color(239, 68, 68);

    public SilavaStudentSystem() {
        setTitle("Student Management Dashboard - Fixed");
        setSize(1150, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG_BODY);
        setLayout(new BorderLayout());

        // --- SIDEBAR ---
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(CARD_BG);
        sidebar.setPreferredSize(new Dimension(300, 0));
        sidebar.setBorder(new EmptyBorder(30, 20, 30, 20));

        JPanel pnlFields = new JPanel(new GridLayout(17, 1, 0, 5));
        pnlFields.setOpaque(false);
        
        txtID = addModernField(pnlFields, "Student ID");
        txtFirstName = addModernField(pnlFields, "First Name");
        txtLastName = addModernField(pnlFields, "Last Name");
        txtLab1 = addModernField(pnlFields, "Lab Work 1");
        txtLab2 = addModernField(pnlFields, "Lab Work 2");
        txtLab3 = addModernField(pnlFields, "Lab Work 3");
        txtPrelim = addModernField(pnlFields, "Prelim Exam");
        txtAttendance = addModernField(pnlFields, "Attendance");

        JButton btnAdd = createModernButton("Add Student", SUCCESS_GREEN);
        sidebar.add(pnlFields, BorderLayout.CENTER);
        sidebar.add(btnAdd, BorderLayout.SOUTH);
        add(sidebar, BorderLayout.WEST);

        // --- MAIN TABLE AREA ---
        JPanel mainContent = new JPanel(new BorderLayout(0, 20));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);
        JLabel lblHeader = new JLabel("Student Academic Records");
        lblHeader.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblHeader.setForeground(TEXT_MAIN);
        
        JPanel topActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topActions.setOpaque(false);
        JButton btnSave = createModernButton("Save Changes", ACCENT_PURPLE);
        JButton btnDelete = createModernButton("Delete", DANGER_RED);
        topActions.add(btnSave);
        topActions.add(btnDelete);
        
        topBar.add(lblHeader, BorderLayout.WEST);
        topBar.add(topActions, BorderLayout.EAST);

        String[] cols = {"ID", "First Name", "Last Name", "L1", "L2", "L3", "Pre", "Att", "Final"};
        model = new DefaultTableModel(cols, 0);
        table = new JTable(model);
        styleTable();
        
        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD_BG);
        scroll.setBorder(new LineBorder(new Color(45, 45, 50)));
        
        mainContent.add(topBar, BorderLayout.NORTH);
        mainContent.add(scroll, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Load CSV Data immediately
        loadCSV("MOCK_DATA.csv");

        // Action Listeners
        btnAdd.addActionListener(e -> addRecord());
        btnSave.addActionListener(e -> saveToFile("MOCK_DATA.csv"));
        btnDelete.addActionListener(e -> deleteRecord());
    }

    private void addRecord() {
        try {
            double l1 = Double.parseDouble(txtLab1.getText());
            double l2 = Double.parseDouble(txtLab2.getText());
            double l3 = Double.parseDouble(txtLab3.getText());
            double pre = Double.parseDouble(txtPrelim.getText());
            double att = Double.parseDouble(txtAttendance.getText());
            double avg = (l1 + l2 + l3 + pre + att) / 5.0;

            model.addRow(new Object[]{
                txtID.getText(), txtFirstName.getText(), txtLastName.getText(),
                l1, l2, l3, pre, att, String.format("%.2f", avg)
            });
            clearInputs();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please check numeric values.");
        }
    }

    private void saveToFile(String fName) {
        if (table.isEditing()) table.getCellEditor().stopCellEditing();
        try (PrintWriter pw = new PrintWriter(new FileWriter(fName))) {
            for (int i = 0; i < model.getRowCount(); i++) {
                String row = "";
                for (int j = 0; j < model.getColumnCount(); j++) {
                    row += model.getValueAt(i, j) + (j < model.getColumnCount() - 1 ? "," : "");
                }
                pw.println(row);
            }
            JOptionPane.showMessageDialog(this, "CSV Saved Successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Save Error: " + e.getMessage());
        }
    }

    private void loadCSV(String fName) {
        File file = new File(fName);
        if (!file.exists()) return;

        model.setRowCount(0); // Clear table
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] data = line.split(",");
                // Dynamically handle rows with 3 or 9 columns
                if (data.length >= 3) {
                    model.addRow(data);
                }
            }
        } catch (Exception e) {
            System.err.println("Load error: " + e.getMessage());
        }
    }

    private JTextField addModernField(JPanel pnl, String label) {
        JLabel l = new JLabel(label);
        l.setForeground(TEXT_DIM);
        l.setFont(new Font("SansSerif", Font.BOLD, 11));
        pnl.add(l);
        JTextField f = new JTextField();
        f.setBackground(BG_BODY);
        f.setForeground(TEXT_MAIN);
        f.setCaretColor(TEXT_MAIN);
        f.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(60, 60, 65)), new EmptyBorder(5, 10, 5, 10)));
        pnl.add(f);
        return f;
    }

    private JButton createModernButton(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(10, 20, 10, 20));
        return b;
    }

    private void styleTable() {
        table.setBackground(CARD_BG);
        table.setForeground(TEXT_MAIN);
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setSelectionBackground(new Color(129, 140, 248, 50));
        table.setSelectionForeground(ACCENT_PURPLE);
        table.getTableHeader().setBackground(CARD_BG);
        table.getTableHeader().setForeground(TEXT_DIM);
        table.getTableHeader().setPreferredSize(new Dimension(0, 40));
    }

    private void deleteRecord() {
        int r = table.getSelectedRow();
        if (r != -1) model.removeRow(r);
    }

    private void clearInputs() {
        txtID.setText(""); txtFirstName.setText(""); txtLastName.setText("");
        txtLab1.setText(""); txtLab2.setText(""); txtLab3.setText("");
        txtPrelim.setText(""); txtAttendance.setText("");
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | UnsupportedLookAndFeelException e) {}
        SwingUtilities.invokeLater(() -> new SilavaStudentSystem().setVisible(true));
    }
}