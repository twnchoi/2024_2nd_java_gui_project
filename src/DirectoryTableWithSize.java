import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

public class DirectoryTableWithSize {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Directory Table with File Sizes");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(600, 400);

            JPanel panel = new JPanel(new BorderLayout());

            JButton selectDirButton = new JButton("Select Directory");
            String[] columnNames = {"File Name", "Extension", "Size"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            JTable table = new JTable(tableModel);

            selectDirButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int result = fileChooser.showOpenDialog(frame);

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedDir = fileChooser.getSelectedFile();
                    tableModel.setRowCount(0); // Clear existing rows
                    populateTableWithFiles(selectedDir, tableModel);
                }
            });

            panel.add(selectDirButton, BorderLayout.NORTH);
            panel.add(new JScrollPane(table), BorderLayout.CENTER);

            frame.add(panel);
            frame.setVisible(true);
        });
    }

    private static void populateTableWithFiles(File dir, DefaultTableModel tableModel) {
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    populateTableWithFiles(file, tableModel);
                } else {
                    String fileName = file.getName();
                    String extension = getFileExtension(file);
                    String size = formatSize(file);
                    tableModel.addRow(new Object[]{fileName, extension, size});
                }
            }
        }
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndex = name.lastIndexOf('.');
        return (lastIndex == -1) ? "" : name.substring(lastIndex + 1);
    }

    private static String formatSize(File file) {
        long size = file.isDirectory() ? calculateDirectorySize(file) : file.length();
        return humanReadableByteCountBin(size);
    }

    private static long calculateDirectorySize(File dir) {
        long size = 0;
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                size += file.isDirectory() ? calculateDirectorySize(file) : file.length();
            }
        }
        return size;
    }

    private static String humanReadableByteCountBin(long bytes) {
        int unit = 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = ("KMGTPE").charAt(exp - 1) + "i";
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
