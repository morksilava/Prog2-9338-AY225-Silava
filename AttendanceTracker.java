import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class AttendanceTracker {

    private static JTextField timeField;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Attendance Tracker");
        frame.setSize(650, 450);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);

        // Gradient background
        GradientPanel root = new GradientPanel();
        root.setLayout(new GridBagLayout());

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(500, 360));
        card.setBackground(Color.WHITE);
        card.setBorder(new LineBorder(new Color(220, 220, 220), 1, true));
        card.setLayout(new BorderLayout(10, 10));
        card.setOpaque(true);

        // Header
        JPanel header = new JPanel(new GridLayout(2, 1));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(15, 15, 5, 15));

        JLabel title = new JLabel("Attendance System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subtitle = new JLabel("Laboratory Attendance Tracker");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(Color.GRAY);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);

        header.add(title);
        header.add(subtitle);

        // Form panel
        JPanel form = new JPanel(new GridLayout(4, 2, 10, 15));
        form.setBorder(new EmptyBorder(10, 20, 10, 20));
        form.setOpaque(false);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 13);

        JLabel nameLabel = new JLabel("Full Name:");
        JLabel courseLabel = new JLabel("Course / Year:");
        JLabel timeLabel = new JLabel("Time In:");
        JLabel signatureLabel = new JLabel("E-Signature:");

        nameLabel.setFont(labelFont);
        courseLabel.setFont(labelFont);
        timeLabel.setFont(labelFont);
        signatureLabel.setFont(labelFont);

        JTextField nameField = new JTextField();
        
        // DROPDOWN SECTION
        String[] courses = {"-- Select Course/Year --", "BSIT - GD 1", "BSIT - GD 2", "BSIT - GD 3", "BSIT - GD 4"};
        JComboBox<String> courseDropdown = new JComboBox<>(courses);
        courseDropdown.setBackground(Color.WHITE);
        courseDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        timeField = new JTextField();
        JTextField signatureField = new JTextField();

        timeField.setEditable(false);
        signatureField.setEditable(false);

        updateTime();
        signatureField.setText(UUID.randomUUID().toString());

        form.add(nameLabel);
        form.add(nameField);
        form.add(courseLabel);
        form.add(courseDropdown);
        form.add(timeLabel);
        form.add(timeField);
        form.add(signatureLabel);
        form.add(signatureField);

        // Buttons
        JPanel buttons = new JPanel(new GridLayout(1, 2, 15, 0));
        buttons.setBorder(new EmptyBorder(5, 20, 15, 20));
        buttons.setOpaque(false);

        JButton submitBtn = new RoundedButton("Submit Attendance", new Color(72, 149, 239));
        JButton viewBtn = new RoundedButton("View Records", new Color(123, 97, 255));

        submitBtn.addActionListener((ActionEvent e) -> {
            String name = nameField.getText().trim();
            String course = (String) courseDropdown.getSelectedItem();

            // ERROR VALIDATION
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter your Full Name.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (courseDropdown.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(frame, "Please select a Course/Year Level.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            saveAttendance(
                    name,
                    course,
                    timeField.getText(),
                    signatureField.getText()
            );

            JOptionPane.showMessageDialog(frame,
                    "Attendance recorded successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);

            // Clear fields
            nameField.setText("");
            courseDropdown.setSelectedIndex(0);
            signatureField.setText(UUID.randomUUID().toString());
            updateTime();
        });

        viewBtn.addActionListener(e -> viewRecords());

        buttons.add(submitBtn);
        buttons.add(viewBtn);

        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(buttons, BorderLayout.SOUTH);

        root.add(card);
        frame.setContentPane(root);
        frame.setVisible(true);

        // Auto-refresh time
        new Timer(60000, e -> updateTime()).start();
    }

    private static void updateTime() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MMMM dd, yyyy  hh:mm a");
        timeField.setText(LocalDateTime.now().format(formatter));
    }

    private static void saveAttendance(String name, String course,
                                       String time, String signature) {
        try (FileWriter writer = new FileWriter("attendance_records.csv", true)) {
            File file = new File("attendance_records.csv");
            if (file.length() == 0) {
                writer.write("Name,Course/Year,Time In,E-Signature\n");
            }
            // Using a semicolon for CSV values if commas are likely in the timestamp
            writer.write(name + "," + course + "," + time + "," + signature + "\n");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static void viewRecords() {
        JFrame viewFrame = new JFrame("Attendance Records");
        viewFrame.setSize(750, 420);
        viewFrame.setLocationRelativeTo(null);

        JTextArea textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        textArea.setEditable(false);

        try (BufferedReader reader =
                     new BufferedReader(new FileReader("attendance_records.csv"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                textArea.append(line.replace(",", "   |   ") + "\n");
            }
        } catch (IOException e) {
            textArea.setText("No attendance records found.");
        }

        viewFrame.add(new JScrollPane(textArea));
        viewFrame.setVisible(true);
    }

    // ===== Custom UI Components =====

    static class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setPaint(new GradientPaint(0, 0,
                    new Color(224, 242, 254),
                    0, getHeight(),
                    new Color(186, 230, 253)));
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    static class RoundedButton extends JButton {
        private final Color color;

        RoundedButton(String text, Color color) {
            super(text);
            this.color = color;
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false); // Fix for some OS button rendering
            setFont(new Font("Segoe UI", Font.BOLD, 13));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            super.paintComponent(g);
        }
    }
}
