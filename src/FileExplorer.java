import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;

public class FileExplorer {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Java File Explorer");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            // Panel to display current directory and navigate
            JPanel topPanel = new JPanel(new BorderLayout());
            JTextField pathField = new JTextField(System.getProperty("user.home"));
            JButton goButton = new JButton("Go");
            JButton chooseButton = new JButton("Choose Directory");

            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.add(goButton, BorderLayout.EAST);
            buttonPanel.add(chooseButton, BorderLayout.WEST);

            topPanel.add(pathField, BorderLayout.CENTER);
            topPanel.add(buttonPanel, BorderLayout.EAST);

            // Table to display file information
            String[] columnNames = {"Name", "Type", "Size", "Last Modified"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            JTable fileTable = new JTable(tableModel);
            JScrollPane scrollPane = new JScrollPane(fileTable);

            // Populate table with files in the given directory
            goButton.addActionListener(e -> updateTable(pathField.getText(), tableModel));
            chooseButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    pathField.setText(selectedFile.getAbsolutePath());
                    updateTable(selectedFile.getAbsolutePath(), tableModel);
                }
            });

            // Add double-click functionality to open subdirectory or show file details
            fileTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int selectedRow = fileTable.getSelectedRow();
                        if (selectedRow != -1) {
                            String fileName = (String) tableModel.getValueAt(selectedRow, 0);
                            String fileType = (String) tableModel.getValueAt(selectedRow, 1);
                            String currentPath = pathField.getText();
                            File selectedFile = new File(currentPath, fileName);

                            if (fileType.equals("Folder")) {
                                addSubdirectoryContents(selectedFile, tableModel, selectedRow);
                            } else {
                                JOptionPane.showMessageDialog(null, "File: " + selectedFile.getAbsolutePath(), "File Info", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }
                }
            });

            updateTable(pathField.getText(), tableModel);

            // Add components to frame
            frame.add(topPanel, BorderLayout.NORTH);
            frame.add(scrollPane, BorderLayout.CENTER);

            frame.setVisible(true);
        });
    }

    private static void updateTable(String directoryPath, DefaultTableModel tableModel) {
        File dir = new File(directoryPath);
        if (!dir.isDirectory()) {
            JOptionPane.showMessageDialog(null, "Invalid directory path!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        tableModel.setRowCount(0); // Clear existing rows

        File[] files = dir.listFiles();
        if (files != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            for (File file : files) {
                String name = file.getName();
                String type = file.isDirectory() ? "Folder" : "File";
                String size = file.isFile() ? file.length() + " bytes" : "-";
                String lastModified = dateFormat.format(file.lastModified());

                tableModel.addRow(new Object[]{name, type, size, lastModified});
            }
        }
    }

    private static void addSubdirectoryContents(File directory, DefaultTableModel tableModel, int parentRow) {
        File[] files = directory.listFiles();
        if (files != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            int insertPosition = parentRow + 1;
            for (File file : files) {
                String name = file.getName();
                String type = file.isDirectory() ? "Folder" : "File";
                String size = file.isFile() ? file.length() + " bytes" : "-";
                String lastModified = dateFormat.format(file.lastModified());

                tableModel.insertRow(insertPosition, new Object[]{name, type, size, lastModified});
                insertPosition++;
            }
        }
    }
}
