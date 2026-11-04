import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Lab 09 - Java Data Streams.
 * Loads a text file, then uses a Stream with a lambda filter to show
 * only the lines that contain the search string.
 */
public class DataStreamsFrame extends JFrame
{
    private final JTextArea originalArea = new JTextArea();
    private final JTextArea filteredArea = new JTextArea();
    private final JTextField searchField = new JTextField(20);
    private final JButton loadButton = new JButton("Load File");
    private final JButton searchButton = new JButton("Search");
    private final JButton quitButton = new JButton("Quit");

    private Path loadedFile = null;

    public DataStreamsFrame()
    {
        super("Data Streams File Searcher");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildCenterPanel(), BorderLayout.CENTER);
        add(buildButtonPanel(), BorderLayout.SOUTH);

        searchButton.setEnabled(false); // only after a file is loaded
        wireActions();

        setSize(1000, 600);
        setLocationRelativeTo(null);
    }

    private JPanel buildTopPanel()
    {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(new JLabel("Search string:"));
        p.add(searchField);
        return p;
    }

    private JPanel buildCenterPanel()
    {
        originalArea.setEditable(false);
        filteredArea.setEditable(false);
        originalArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        filteredArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        JScrollPane leftScroll = new JScrollPane(originalArea);
        JScrollPane rightScroll = new JScrollPane(filteredArea);
        leftScroll.setBorder(BorderFactory.createTitledBorder("Original File"));
        rightScroll.setBorder(BorderFactory.createTitledBorder("Filtered Lines"));

        // Parallel scrolling: both panes share one vertical scroll model
        rightScroll.getVerticalScrollBar().setModel(leftScroll.getVerticalScrollBar().getModel());

        JPanel p = new JPanel(new GridLayout(1, 2, 8, 0));
        p.add(leftScroll);
        p.add(rightScroll);
        return p;
    }

    private JPanel buildButtonPanel()
    {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 6));
        p.add(loadButton);
        p.add(searchButton);
        p.add(quitButton);
        return p;
    }

    private void wireActions()
    {
        loadButton.addActionListener(e -> loadFile());
        searchButton.addActionListener(e -> searchFile());
        searchField.addActionListener(e -> {
            if (searchButton.isEnabled())
            {
                searchFile();
            }
        });
        quitButton.addActionListener(e -> System.exit(0));
    }

    /** Lets the user pick a file with JFileChooser and shows it on the left. */
    private void loadFile()
    {
        JFileChooser chooser = new JFileChooser(new File(System.getProperty("user.dir")));
        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        Path path = Paths.get(chooser.getSelectedFile().getAbsolutePath());

        try (Stream<String> lines = Files.lines(path))
        {
            String text = lines.collect(Collectors.joining("\n"));
            originalArea.setText(text);
            originalArea.setCaretPosition(0);
            filteredArea.setText("");
            loadedFile = path;
            searchButton.setEnabled(true);
            setTitle("Data Streams File Searcher - " + path.getFileName());
        }
        catch (IOException | UncheckedIOException ex)
        {
            showError("Could not read the file:\n" + ex.getMessage());
        }
    }

    /** Uses Stream + a lambda filter to keep only lines containing the search string. */
    private void searchFile()
    {
        String search = searchField.getText().trim();
        if (loadedFile == null)
        {
            showError("Please load a file first.");
            return;
        }
        if (search.isEmpty())
        {
            showError("Please enter a search string.");
            return;
        }

        String needle = search.toLowerCase();

        try (Stream<String> lines = Files.lines(loadedFile))
        {
            String result = lines
                    .filter(line -> line.toLowerCase().contains(needle)) // lambda filter
                    .collect(Collectors.joining("\n"));

            filteredArea.setText(result);
            filteredArea.setCaretPosition(0);
        }
        catch (IOException | UncheckedIOException ex)
        {
            showError("Could not read the file:\n" + ex.getMessage());
        }
    }

    private void showError(String message)
    {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
