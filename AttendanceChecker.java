import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * Attendance Tracker Application
 * LAB Work 1 (Enhanced Version)
 */
public class AttendanceTracker {

    private static JTextField timeField;

    public static void main(String[] args) {

        JFrame frame = new JFrame("Attendance Tracker");
        frame.setSize(500, 380);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Optional window icon
        try {
            frame.setIconImage(new ImageIcon("icon.png").getImage());
        } catch (Exception ignored) {}

        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setBackground(new Color(235, 245, 250)); // Pastel blue

        Font font = new Font("SansSerif", Font.PLAIN, 14);

        JLabel nameLabel = new JLabel("Attendance Name:");
        JLabel courseLabel = new JLabel("Course / Year:");
        JLabel timeLabel = new JLabel("Time In:");
        JLabel signatureLabel = new JLabel("E-Signature:");

        nameLabel.setFont(font);
        courseLabel.setFont(font);
        timeLabel.setFont(font);
        signatureLabel.setFont(font);

        JTextField nameField = new JTextField();
        JTextField courseField = new JTextField();
        timeField = new JTextField();
        JTextField signatureField = new JTextField();

        timeField.setEditable(false);
        signatureField.setEditable(false);

        updateTime();

        // Auto-refresh time every 60 seconds
        Timer timer = new Timer(60000, e -> updateTime());
        timer.start();

        signatureField.setText(UUID.randomUUID().toString());

        JButton submitBtn = new JButton("Submit Attendance");
        JButton viewBtn = new JButton("View Records");

        submitBtn.setBackground(new Color(200, 230, 200)); // Pastel green
        viewBtn.setBackground(new Color(230, 210, 250)); // Pastel purple

        submitBtn.addActionListener((ActionEvent e) -> {
            saveAttendance(
                    nameField.getText(),
                    courseField.getText(),
                    timeField.getText(),
                    signatureField.getText()
            );
            JOptionPane.showMessageDialog(frame,
                    "Attendance Submitted Successfully!");

            nameField.setText("");
            courseField.setText("");
            signatureField.setText(UUID.randomUUID().toString());
            updateTime();
        });

        viewBtn.addActionListener(e -> viewRecords());

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(courseLabel);
        panel.add(courseField);
        panel.add(timeLabel);
        panel.add(timeField);
        panel.add(signatureLabel);
        panel.add(signatureField);
        panel.add(submitBtn);
        panel.add(viewBtn);

        frame.add(panel);
        frame.setVisible(true);
    }

    /**
     * Updates the Time In field
     */
    private static void updateTime() {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
        timeField.setText(LocalDateTime.now().format(formatter));
    }

    /**
     * Saves attendance to CSV file
     */
    private static void saveAttendance(String name, String course,
                                       String time, String signature) {
        try (FileWriter writer =
                     new FileWriter("attendance_records.csv", true)) {

            // Add header if file is empty
            File file = new File("attendance_records.csv");
            if (file.length() == 0) {
                writer.write("Name,Course/Year,Time In,E-Signature\n");
            }

            writer.write(name + "," + course + "," + time + "," + signature + "\n");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Displays attendance records in a new window
     */
    private static void viewRecords() {
        JFrame viewFrame = new JFrame("Attendance Records");
        viewFrame.setSize(600, 400);

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        try (BufferedReader reader =
                     new BufferedReader(new FileReader("attendance_records.csv"))) {

            String line;
            while ((line = reader.readLine()) != null) {
                textArea.append(line + "\n");
            }

        } catch (IOException e) {
            textArea.setText("No records found.");
        }

        JScrollPane scrollPane = new JScrollPane(textArea);
        viewFrame.add(scrollPane);
        viewFrame.setVisible(true);
    }
}
