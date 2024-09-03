package ElevatorDSA;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class ElevatorAnimation extends JFrame {

    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 600;
    private static final int ELEVATOR_WIDTH = 50;
    private static final int ELEVATOR_HEIGHT = 50;
    private static final int FLOOR_HEIGHT = 100; // Distance between floors
    private static final int ELEVATOR_SPEED = 2; // Pixels per tick
    private static final int STOP_DELAY = 3000; // 3 seconds
    private static final int MAX_CAPACITY = 5; // Maximum passengers
    private static final int PASSENGERS_TO_REMOVE = 1; // Number of passengers to remove when button is clicked

    private int currentFloor = 1; // Start at the 1st floor
    private int currentY = WINDOW_HEIGHT - ELEVATOR_HEIGHT; // Starting at the bottom floor
    private boolean movingUp = true; // Elevator initially moves up
    private boolean paused = false;
    private int passengers = 0; // Current number of passengers
    private String message = ""; // Message to display
    private String credits = "Developed by Laurence Lesmoras, Mc Curvin Royeras";
    private Map<Integer, Integer> passengersByFloor = new HashMap<>(); // Passengers by destination floor

    public ElevatorAnimation() {
        setTitle("Elevator Animation");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ElevatorPanel panel = new ElevatorPanel();
        add(panel, BorderLayout.CENTER);

        JPanel controls = new JPanel();
        controls.setLayout(new GridLayout(2, 2)); // 2 rows, 2 columns for buttons
        JButton upButton = new JButton("Go Up");
        JButton downButton = new JButton("Go Down");
        JButton addPassengerButton = new JButton("Add Passenger");
        JButton removePassengerButton = new JButton("Remove Passenger");

        upButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goUp();
            }
        });

        downButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goDown();
            }
        });

        addPassengerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addPassenger(1, getRandomDestination(currentFloor)); // Adding 1 passenger for demonstration
            }
        });

        removePassengerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePassenger(PASSENGERS_TO_REMOVE); // Removing a fixed number of passengers
            }
        });

        controls.add(upButton);
        controls.add(downButton);
        controls.add(addPassengerButton);
        controls.add(removePassengerButton);
        add(controls, BorderLayout.SOUTH);

        javax.swing.Timer timer = new javax.swing.Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!paused) {
                    moveElevator();
                    panel.repaint();
                }
            }
        });

        timer.start();
    }

    private void moveElevator() {
        int targetY = getYForFloor(currentFloor);

        if (movingUp) {
            currentY -= ELEVATOR_SPEED;
            if (currentY <= targetY) {
                currentY = targetY;
                handleFloorArrival();
                if (currentFloor < 5) {
                    currentFloor++;
                } else {
                    movingUp = false;
                    currentFloor--;
                }
            }
        } else {
            currentY += ELEVATOR_SPEED;
            if (currentY >= targetY) {
                currentY = targetY;
                handleFloorArrival();
                if (currentFloor > 1) {
                    currentFloor--;
                } else {
                    movingUp = true;
                    currentFloor++;
                }
            }
        }
    }

    private void handleFloorArrival() {
        paused = true;
        managePassengers();
        if (currentFloor == 5 || currentFloor == 1) {
            if (Math.random() > 0.8) {
                movingUp = !movingUp;
                currentFloor = getRandomFloor();
            }
        }
        javax.swing.Timer pauseTimer = new javax.swing.Timer(STOP_DELAY, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                paused = false;
                message = ""; // Clear message after pause
            }
        });

        pauseTimer.setRepeats(false);
        pauseTimer.start();
    }

    private void managePassengers() {
        int floor = currentFloor;
        int exitingPassengers = passengersByFloor.getOrDefault(floor, 0);
        removePassenger(exitingPassengers);
        passengersByFloor.put(floor, 0); // Ensure no passengers left in the elevator for this floor
        message = "Passengers exiting at floor " + floor + ": " + exitingPassengers;

        if (passengers < MAX_CAPACITY) {
            int enteringPassengers = Math.min((int) (Math.random() * (MAX_CAPACITY - passengers + 1)), MAX_CAPACITY - passengers);
            addPassenger(enteringPassengers, getRandomDestination(floor));
            message += " | Passengers entering: " + enteringPassengers;
        }
    }

    public void addPassenger(int count, int destination) {
        if (passengers + count <= MAX_CAPACITY) {
            passengers += count;
            passengersByFloor.put(destination, passengersByFloor.getOrDefault(destination, 0) + count);
        } else {
            message = "Cannot add passengers. Elevator at full capacity.";
        }
    }

    public void removePassenger(int count) {
        int actualRemoval = Math.min(count, passengers);
        passengers -= actualRemoval;
        message = "Removed " + actualRemoval + " passengers.";
    }

    public void goUp() {
        movingUp = true;
        message = "Going up";
    }

    public void goDown() {
        movingUp = false;
        message = "Going down";
    }

    private int getRandomDestination(int currentFloor) {
        int destination;
        do {
            destination = 1 + (int) (Math.random() * 5);
        } while (destination == currentFloor);
        return destination;
    }

    private int getRandomFloor() {
        return 1 + (int) (Math.random() * 5);
    }

    private int getYForFloor(int floor) {
        return WINDOW_HEIGHT - floor * FLOOR_HEIGHT - ELEVATOR_HEIGHT;
    }

    private class ElevatorPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(Color.LIGHT_GRAY);
            for (int i = 1; i <= 5; i++) {
                int y = getYForFloor(i) + ELEVATOR_HEIGHT;
                g.drawLine(0, y, WINDOW_WIDTH, y);
                g.drawString("Floor " + i, 10, y - 10);
            }

            g.setColor(Color.BLACK);
            g.fillRect(WINDOW_WIDTH / 2 - ELEVATOR_WIDTH / 2, currentY, ELEVATOR_WIDTH, ELEVATOR_HEIGHT);

            g.setColor(Color.MAGENTA);
            g.drawString(credits, 10, 20);
            g.setColor(Color.RED);
            g.drawString("Passengers: " + passengers + "/" + MAX_CAPACITY, 10, 40);
            g.drawString(message, 10, 60);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ElevatorAnimation().setVisible(true));
    }
}
