package ElevatorDSA;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    private int currentFloor = 1; // Start at the 1st floor
    private int currentY = WINDOW_HEIGHT - ELEVATOR_HEIGHT; // Starting at the bottom floor
    private boolean movingUp = true; // Elevator initially moves up
    private boolean paused = false;
    private int passengers = 0; // Current number of passengers
    private String message = ""; // Message to display
    private String credits = "Developed by Laurence Lesmoras, Mc Curvin Royeras";
    private Map<Integer, Integer> passengersByFloor = new HashMap<>(); // Passengers by destination floor
    private List<Integer> stops = Arrays.asList(1, 2, 3, 4, 5); // Default stop sequence

    public ElevatorAnimation() {
        setTitle("Elevator Animation");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        ElevatorPanel panel = new ElevatorPanel();
        add(panel);

        // Use javax.swing.Timer explicitly
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

        // Handle passengers exiting and entering
        managePassengers();

        if (currentFloor == 5 || currentFloor == 1) {
            // Random chance to reverse direction or stop at a random floor
            if (Math.random() > 0.8) {
                movingUp = !movingUp;
                currentFloor = getRandomFloor();
            }
        }

        // Use javax.swing.Timer explicitly
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

        // Passengers exiting
        int exitingPassengers = passengersByFloor.getOrDefault(floor, 0);
        passengers -= exitingPassengers;
        passengersByFloor.put(floor, 0);
        message = "Passengers exiting at floor " + floor + ": " + exitingPassengers;

        // Passengers entering
        if (passengers < MAX_CAPACITY) {
            int enteringPassengers = Math.min((int) (Math.random() * (MAX_CAPACITY - passengers + 1)), MAX_CAPACITY - passengers);
            passengersByFloor.put(getRandomDestination(floor), enteringPassengers);
            passengers += enteringPassengers;
            message += " | Passengers entering: " + enteringPassengers;
        }
    }

    private int getRandomDestination(int currentFloor) {
        // Ensure the destination is different from the current floor
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

            

            // Draw the floors
            g.setColor(Color.LIGHT_GRAY);
            for (int i = 1; i <= 5; i++) {
                int y = getYForFloor(i) + ELEVATOR_HEIGHT;
                g.drawLine(0, y, WINDOW_WIDTH, y);
                g.drawString("Floor " + i, 10, y - 10);
            }


            // Draw the elevator
            g.setColor(Color.BLACK);
            g.fillRect(WINDOW_WIDTH / 2 - ELEVATOR_WIDTH / 2, currentY, ELEVATOR_WIDTH, ELEVATOR_HEIGHT);
            

            // Draw the credits, capacity, and message
            g.setColor(Color.MAGENTA);
            g.drawString(credits, 10, 20);
            g.setColor(Color.RED);
            g.drawString("Passengers: " + passengers + "/" + MAX_CAPACITY, 10, 40);
            g.drawString(message, 10, 60);
        }
    }

}
