package ElevatorDSA;
import javax.swing.SwingUtilities;

public class MainElevator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ElevatorAnimation animation = new ElevatorAnimation();
            animation.setVisible(true);
        });
    }
}
