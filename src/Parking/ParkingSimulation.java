package Parking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class ParkingSimulation extends JFrame {

    private int vehicleCount = 0;
    private boolean[] parkingSpots = new boolean[10];
    private JButton[] spotButtons = new JButton[10];
    private JLabel[] vehicles;
    private int selectedVehicle = -1;
    private Timer timer;
    private JLabel timerLabel, statusLabel;
    private long startTime = 0;
    private boolean timerRunning = false;
    private ArrayList<Boolean> parkedVehicles;
    private ImageIcon carImage;

    public ParkingSimulation() {
        setTitle("Interactive Parking Simulation");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLayout(null);
        setBackground(new Color(240, 240, 240));

        carImage = new ImageIcon("path/to/car.png");

        initUIComponents();
        addKeyListener(new CarKeyListener());
        setFocusable(true);
        requestFocusInWindow();
        setVisible(true);
    }

    private void initUIComponents() {
        JLabel header = new JLabel("Interactive Parking Simulation", SwingConstants.CENTER);
        header.setFont(new Font("Arial", Font.BOLD, 28));
        header.setBounds(200, 10, 500, 30);
        add(header);

        JPanel parkingGrid = new JPanel(new GridLayout(2, 5, 10, 10));
        parkingGrid.setBounds(150, 60, 600, 200);
        for (int i = 0; i < spotButtons.length; i++) {
            spotButtons[i] = new JButton("Spot " + (i + 1));
            spotButtons[i].setBackground(Color.GREEN);
            spotButtons[i].setFont(new Font("Arial", Font.BOLD, 16));
            spotButtons[i].setFocusable(false);
            parkingGrid.add(spotButtons[i]);
            final int spotIndex = i;
            spotButtons[i].addActionListener(e -> parkVehicle(spotIndex));
        }
        add(parkingGrid);

        timerLabel = new JLabel("Time: 0s", SwingConstants.CENTER);
        timerLabel.setBounds(50, 400, 150, 30);
        timerLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        add(timerLabel);

        statusLabel = new JLabel("Welcome! Start the simulation.", SwingConstants.CENTER);
        statusLabel.setBounds(50, 450, 800, 30);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        add(statusLabel);

        JLabel vehicleCountLabel = new JLabel("Enter Vehicles (0-10):");
        vehicleCountLabel.setBounds(50, 550, 200, 30);
        add(vehicleCountLabel);

        JTextField vehicleInputField = new JTextField();
        vehicleInputField.setBounds(250, 550, 50, 30);
        add(vehicleInputField);

        JButton startButton = new JButton("Start");
        startButton.setBounds(320, 550, 80, 30);
        startButton.addActionListener(e -> {
            try {
                int count = Integer.parseInt(vehicleInputField.getText());
                if (count < 0 || count > 10) {
                    statusLabel.setText("Enter a number between 0 and 10.");
                    statusLabel.setForeground(Color.RED);
                } else {
                    startSimulation(count);
                }
            } catch (NumberFormatException ex) {
                statusLabel.setText("Invalid input. Enter a number.");
                statusLabel.setForeground(Color.RED);
            }
        });
        add(startButton);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(410, 550, 80, 30);
        resetButton.addActionListener(e -> resetSimulation());
        add(resetButton);
    }

    private void startSimulation(int count) {
        vehicleCount = count;
        parkedVehicles = new ArrayList<>();
        vehicles = new JLabel[vehicleCount];
        for (int i = 0; i < vehicleCount; i++) {
            final int vehicleIndex = i;
            vehicles[vehicleIndex] = new JLabel(carImage);
            vehicles[vehicleIndex].setBounds(50 + vehicleIndex * 60, 500, 60, 30);
            vehicles[vehicleIndex].setOpaque(true);
            vehicles[vehicleIndex].setBackground(Color.BLUE);
            vehicles[vehicleIndex].addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    selectedVehicle = vehicleIndex;
                    statusLabel.setText("Vehicle " + (selectedVehicle + 1) + " selected.");
                }
            });
            add(vehicles[vehicleIndex]);
            parkedVehicles.add(false);
        }
        repaint();
        statusLabel.setText("Move the vehicles to their spots.");
        resetTimer();
        requestFocusInWindow();
    }

    private void resetTimer() {
        if (timer != null) {
            timer.stop();
        }
        timerRunning = false;
        startTime = System.currentTimeMillis();
        timerLabel.setText("Time: 0s");
        startTimer();
    }

    private void startTimer() {
        if (!timerRunning) {
            timerRunning = true;
            timer = new Timer(100, e -> updateTimer());
            timer.start();
        }
    }

    private void updateTimer() {
        long elapsed = System.currentTimeMillis() - startTime;
        timerLabel.setText("Time: " + (elapsed / 1000.0) + "s");
    }

    private void resetSimulation() {
        if (timer != null) timer.stop();
        timerRunning = false;
        startTime = 0;
        timerLabel.setText("Time: 0s");
        statusLabel.setText("Simulation reset.");
        for (int i = 0; i < parkingSpots.length; i++) {
            parkingSpots[i] = false;
            spotButtons[i].setBackground(Color.GREEN);
            spotButtons[i].setText("Spot " + (i + 1));
        }
        if (vehicles != null) {
            for (JLabel vehicle : vehicles) {
                remove(vehicle);
            }
        }
        selectedVehicle = -1;
        repaint();
    }

    private void parkVehicle(int spotIndex) {
        if (parkingSpots[spotIndex]) {
            statusLabel.setText("Spot " + (spotIndex + 1) + " is already occupied.");
            statusLabel.setForeground(Color.RED);
        } else if (selectedVehicle != -1 && !parkedVehicles.get(selectedVehicle)) {
            vehicles[selectedVehicle].setBounds(150 + (spotIndex % 5) * 100, (spotIndex < 5) ? 60 : 160, 60, 30);
            parkingSpots[spotIndex] = true;
            spotButtons[spotIndex].setBackground(Color.RED);
            spotButtons[spotIndex].setText("Occupied");
            parkedVehicles.set(selectedVehicle, true);
            vehicles[selectedVehicle].setBackground(Color.GREEN);
            statusLabel.setText("Vehicle " + (selectedVehicle + 1) + " parked in spot " + (spotIndex + 1) + ".");
            timer.stop();
            checkAllParked();
        }
    }

    private void checkAllParked() {
        if (!parkedVehicles.contains(false)) {
            timer.stop();
            statusLabel.setText("All vehicles parked! Total Time: " + (System.currentTimeMillis() - startTime) / 1000.0 + "s");
            statusLabel.setForeground(Color.GREEN);
        }
    }

    private class CarKeyListener extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (selectedVehicle != -1) {
                int step = 5;
                Rectangle bounds = vehicles[selectedVehicle].getBounds();
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP -> {
                        if (bounds.y - step >= 60) {
                            bounds.y -= step;
                        }
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (bounds.y + step <= 300) {
                            bounds.y += step;
                        }
                    }
                    case KeyEvent.VK_LEFT -> {
                        if (bounds.x - step >= 0) {
                            bounds.x -= step;
                        }
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (bounds.x + step <= getWidth() - 60) {
                            bounds.x += step;
                        }
                    }
                }
                vehicles[selectedVehicle].setBounds(bounds);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ParkingSimulation::new);
    }
}