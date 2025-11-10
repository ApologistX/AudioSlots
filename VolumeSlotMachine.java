import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;

public class VolumeSlotMachine extends JFrame {
    private JLabel[] slots = new JLabel[5];
    private String[] symbols = {"🔇", "🔉", "🔊", "💎", "🎰", "⭐", "7️⃣"};
    private JLabel volumeLabel;
    private JButton spinButton;
    private JButton plusButton;
    private JButton minusButton;
    private int currentVolume = 50;
    private Random random = new Random();
    private Timer spinTimer;
    private int spinCount = 0;
    private String osType;

    public VolumeSlotMachine() {
        setTitle("Black Diamond Volume Control");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(40, 20, 40));

        // Detect OS
        osType = detectOS();

        // Get current system volume
        currentVolume = getSystemVolume();

        // Title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(40, 20, 40));
        JLabel titleLabel = new JLabel("♦ BLACK DIAMOND VOLUME ♦");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 28));
        titleLabel.setForeground(new Color(255, 215, 0));
        JLabel osLabel = new JLabel("Running on: " + osType);
        osLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        osLabel.setForeground(new Color(180, 180, 180));
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        osLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(osLabel);
        add(titlePanel, BorderLayout.NORTH);

        // Slot machine panel
        JPanel slotPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        slotPanel.setBackground(new Color(30, 30, 50));
        slotPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 215, 0), 5),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        for (int i = 0; i < 5; i++) {
            slots[i] = new JLabel(symbols[random.nextInt(symbols.length)], SwingConstants.CENTER);
            slots[i].setFont(new Font("Dialog", Font.PLAIN, 60));
            slots[i].setOpaque(true);
            slots[i].setBackground(new Color(20, 20, 35));
            slots[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(255, 215, 0), 2),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
            slotPanel.add(slots[i]);
        }
        add(slotPanel, BorderLayout.CENTER);

        // Control panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(40, 20, 40));
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Volume display
        volumeLabel = new JLabel("VOLUME: " + currentVolume + "%");
        volumeLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        volumeLabel.setForeground(new Color(255, 215, 0));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        controlPanel.add(volumeLabel, gbc);

        // Minus button
        minusButton = createStyledButton("-");
        minusButton.addActionListener(e -> adjustVolume(-10));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        controlPanel.add(minusButton, gbc);

        // Spin button
        spinButton = createStyledButton("SPIN");
        spinButton.setPreferredSize(new Dimension(150, 60));
        spinButton.addActionListener(e -> spin());
        gbc.gridx = 1;
        gbc.gridy = 1;
        controlPanel.add(spinButton, gbc);

        // Plus button
        plusButton = createStyledButton("+");
        plusButton.addActionListener(e -> adjustVolume(10));
        gbc.gridx = 2;
        gbc.gridy = 1;
        controlPanel.add(plusButton, gbc);

        add(controlPanel, BorderLayout.SOUTH);

        setSize(900, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String detectOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "Windows";
        } else if (os.contains("nux") || os.contains("nix")) {
            return "Linux";
        } else if (os.contains("mac")) {
            return "macOS";
        }
        return "Unknown";
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setBackground(new Color(255, 215, 0));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 160, 0), 3),
                BorderFactory.createEmptyBorder(10, 25, 10, 25)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(255, 235, 50));
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(255, 215, 0));
            }
        });

        return button;
    }

    private void spin() {
        spinButton.setEnabled(false);
        minusButton.setEnabled(false);
        plusButton.setEnabled(false);
        spinCount = 0;

        spinTimer = new Timer(100, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (JLabel slot : slots) {
                    slot.setText(symbols[random.nextInt(symbols.length)]);
                }

                spinCount++;
                if (spinCount >= 20) {
                    spinTimer.stop();
                    calculateResult();
                    spinButton.setEnabled(true);
                    minusButton.setEnabled(true);
                    plusButton.setEnabled(true);
                }
            }
        });
        spinTimer.start();
    }

    private void calculateResult() {
        int change = random.nextInt(41) - 20; // Random change between -20 and +20
        setVolume(currentVolume + change);

        // Flash effect
        Timer flashTimer = new Timer(200, null);
        final int[] flashCount = {0};
        flashTimer.addActionListener(e -> {
            Color color = flashCount[0] % 2 == 0 ? new Color(50, 50, 80) : new Color(20, 20, 35);
            for (JLabel slot : slots) {
                slot.setBackground(color);
            }
            flashCount[0]++;
            if (flashCount[0] >= 6) {
                ((Timer)e.getSource()).stop();
                for (JLabel slot : slots) {
                    slot.setBackground(new Color(20, 20, 35));
                }
            }
        });
        flashTimer.start();
    }

    private void adjustVolume(int change) {
        setVolume(currentVolume + change);
    }

    private void setVolume(int volume) {
        currentVolume = Math.max(0, Math.min(100, volume));
        volumeLabel.setText("VOLUME: " + currentVolume + "%");

        // Set system volume based on OS
        if (osType.equals("Windows")) {
            setWindowsVolume(currentVolume);
        } else if (osType.equals("Linux")) {
            setLinuxVolume(currentVolume);
        } else if (osType.equals("macOS")) {
            setMacVolume(currentVolume);
        }

        // Visual feedback
        volumeLabel.setForeground(currentVolume > 75 ? new Color(255, 100, 100) :
                currentVolume > 25 ? new Color(255, 215, 0) :
                        new Color(150, 150, 255));
    }

    private void setWindowsVolume(int volume) {
        try {
            // PowerShell command using audio device interface
            String command = String.format(
                    "powershell -Command \"" +
                            "$obj = New-Object -ComObject WScript.Shell;" +
                            "1..50 | ForEach-Object { $obj.SendKeys([char]174) };" +
                            "Start-Sleep -Milliseconds 100;" +
                            "1..%d | ForEach-Object { $obj.SendKeys([char]175) }\"",
                    volume / 2
            );

            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Don't wait to avoid UI freezing
            new Thread(() -> {
                try {
                    process.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            System.err.println("Error setting Windows volume: " + e.getMessage());
        }
    }

    private void setLinuxVolume(int volume) {
        try {
            // Try multiple Linux audio systems

            // Try PulseAudio (most common)
            try {
                ProcessBuilder pb = new ProcessBuilder("pactl", "set-sink-volume", "@DEFAULT_SINK@", volume + "%");
                Process process = pb.start();
                process.waitFor();
                return;
            } catch (Exception e) {
                // PulseAudio not available, try ALSA
            }

            // Try ALSA
            try {
                ProcessBuilder pb = new ProcessBuilder("amixer", "-D", "pulse", "sset", "Master", volume + "%");
                Process process = pb.start();
                process.waitFor();
                return;
            } catch (Exception e) {
                // ALSA with pulse not available, try plain ALSA
            }

            // Try plain ALSA
            try {
                ProcessBuilder pb = new ProcessBuilder("amixer", "sset", "Master", volume + "%");
                Process process = pb.start();
                process.waitFor();
            } catch (Exception e) {
                System.err.println("Could not set volume with any Linux audio system");
            }

        } catch (Exception e) {
            System.err.println("Error setting Linux volume: " + e.getMessage());
        }
    }

    private void setMacVolume(int volume) {
        try {
            // macOS uses osascript
            String command = "osascript -e 'set volume output volume " + volume + "'";
            ProcessBuilder pb = new ProcessBuilder("bash", "-c", command);
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            System.err.println("Error setting macOS volume: " + e.getMessage());
        }
    }

    private int getSystemVolume() {
        try {
            if (osType.equals("Linux")) {
                // Try to get PulseAudio volume
                ProcessBuilder pb = new ProcessBuilder("pactl", "get-sink-volume", "@DEFAULT_SINK@");
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();

                if (line != null && line.contains("%")) {
                    // Parse percentage from output like "Volume: front-left: 65536 / 100% / 0.00 dB"
                    int percentIndex = line.indexOf("%");
                    int startIndex = percentIndex - 1;
                    while (startIndex > 0 && Character.isDigit(line.charAt(startIndex - 1))) {
                        startIndex--;
                    }
                    return Integer.parseInt(line.substring(startIndex, percentIndex).trim());
                }
            } else if (osType.equals("macOS")) {
                ProcessBuilder pb = new ProcessBuilder("osascript", "-e", "output volume of (get volume settings)");
                Process process = pb.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = reader.readLine();
                if (line != null) {
                    return Integer.parseInt(line.trim());
                }
            }
        } catch (Exception e) {
            System.err.println("Could not read system volume: " + e.getMessage());
        }

        return 50; // Default to 50%
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VolumeSlotMachine());
    }
}