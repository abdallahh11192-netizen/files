import javax.swing.SwingUtilities;

public class Main
{
    public static void main(String[] args)
    {
        // Build the GUI on the Swing event thread. No IOException is thrown here;
        // all file errors are handled with try/catch inside DataStreamsFrame.
        SwingUtilities.invokeLater(() -> {
            DataStreamsFrame frame = new DataStreamsFrame();
            frame.setVisible(true);
        });
    }
}
