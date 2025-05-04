import javax.swing.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    /**
    Using Java's built-in GUI library, Swing.
     Swing will use one dedicated thread -> EDT Event Dispatch Thread for all GUI work
    * */
    public static void main(String[] args) {
        Runnable r = () -> {
            JFrame f = new JFrame("Alien World");
            f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            f.setResizable(false);
            f.add(new MiniWorld());
            f.pack();
            f.setLocationRelativeTo(null);
            f.setVisible(true);};

        SwingUtilities.invokeLater(r);
    }
}

