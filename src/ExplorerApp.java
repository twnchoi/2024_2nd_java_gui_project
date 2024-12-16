import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.text.TableView.TableRow;

import myPkg.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Vector;

public class ExplorerApp {
    
    public static void main(String[] args) {
        RootFrame rf = new RootFrame();
    }

}

class RootFrame extends JFrame {

    ExpPanel exPnl = null;
    DiagPanel dgPnl = null;
    JTabbedPane jtab = null;
    JTextField pathField = null;
    File rootDir = null;
    FileTable ftb = null;


    public RootFrame() {
        setTitle("DirDiag");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 800);
        JToolBar toolBar = new JToolBar();
        JButton setDirButton = new JButton("Select Directory");
        JButton refreshButton = new JButton("Refresh");
        pathField = new JTextField();
        toolBar.add(setDirButton);
        toolBar.add(refreshButton);
        toolBar.add(pathField);
        toolBar.setFloatable(false);

        jtab = new JTabbedPane();
        jtab.addTab("Welcome", new InitPanel());
        
        setDirButton.addActionListener(new SetDirActionListener());
        refreshButton.addActionListener(new RefreshActionListener());

        Container contentPane = getContentPane();
        contentPane.add(toolBar,BorderLayout.NORTH);
        contentPane.add(jtab, BorderLayout.CENTER);
        setVisible(true);
    }

    private class SetDirActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int result = fileChooser.showOpenDialog(getParent());
            if(result == JFileChooser.APPROVE_OPTION) {
                File selDir = fileChooser.getSelectedFile();
                ftb = new FileTable(selDir);
                exPnl = new ExpPanel(ftb);
                dgPnl = new DiagPanel(ftb);
                pathField.setText(selDir.getAbsolutePath());
                rootDir = selDir;

                if(jtab.getTabCount() != 1) {
                    jtab.removeTabAt(1);
                    jtab.removeTabAt(1);
                }
                jtab.addTab("Explorer", exPnl);
                jtab.addTab("Diagnose", dgPnl);
                jtab.setSelectedIndex(1);
            }
        }
    }
    private class RefreshActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if(ftb != null) {
                ftb = new FileTable(rootDir);
                exPnl = new ExpPanel(ftb);
                dgPnl = new DiagPanel(ftb);

                if(jtab.getTabCount() != 1) {
                    jtab.removeTabAt(1);
                    jtab.removeTabAt(1);
                }
                jtab.addTab("Explorer", exPnl);
                jtab.addTab("Diagnose", dgPnl);
                jtab.setSelectedIndex(1);
            }
            else {
                JOptionPane.showMessageDialog(null, "Set directory first!", "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class toolBar extends JToolBar {
    public toolBar() {
        add(new JButton("Set Directory"));
        add(new JButton("Refresh"));
        setFloatable(false);
    }
}

class InitPanel extends JPanel {
    public InitPanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        String html = "<html>" +
        "<span style='font-family:times new roman;font-size:30px; font-weight:bold;'>Hi</span><br>" +
        "<span style='font-size:15px;'>Thanks for using DirDiag.</span><br>" +
        "<span style='font-size:15px;'>Please set directory at toolbar.</span>" +
        "</html>";
        add(new JLabel(html));
    }
}

class ExpPanel extends JPanel {
    private File rootDir;
    
    public ExpPanel(FileTable ftb) {
        setLayout(new BorderLayout());

        TableModel mdl = ftb.getMdl();
        JTable jtbl = new JTable(mdl);
        jtbl.setDefaultRenderer(Long.class, new CustomRenderer(ftb));
        jtbl.setDefaultRenderer(Object.class, new CustomRenderer(ftb));
        jtbl.getColumn("Name").setPreferredWidth( 300);
        jtbl.getColumn("Type").setPreferredWidth(50);
        jtbl.getColumn("Directory").setPreferredWidth(300);
        jtbl.getColumn("Size").setPreferredWidth(180);
        jtbl.getColumn("Last Modified").setPreferredWidth(90);
        jtbl.setBorder(BorderFactory.createEmptyBorder());
        TableRowSorter sorter = new TableRowSorter<>(mdl);
        jtbl.setRowSorter(sorter);

        jtbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int selRow = jtbl.getSelectedRow();
                    if(selRow != -1) {
                        String path = (String) mdl.getValueAt(selRow, 2);
                        try {Desktop.getDesktop().open(new File(path));
                        }catch (IOException exc) {
                            System.out.println(exc);
                        }
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(jtbl);
        add(scrollPane, BorderLayout.CENTER);

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        String lblStr =
            "Total : " + FileOps.humanReadableByteCountBin(ftb.getRootSize()) +
            " / " + ftb.getFArray().size() + " file(s)";
        topPanel.add(new JLabel(lblStr), BorderLayout.WEST);

        JLabel searchLabel = new JLabel("Search : ");
        searchLabel.setHorizontalAlignment(JLabel.RIGHT);
        JTextField jtxt = new JTextField(15);
        
        jtxt.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateFilter();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                updateFilter();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {
                updateFilter();
            }
            protected void updateFilter() {
                try {
                    sorter.setRowFilter(RowFilter.regexFilter(jtxt.getText()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        topPanel.add(searchLabel, BorderLayout.CENTER);
        topPanel.add(jtxt, BorderLayout.EAST);


        add(topPanel, BorderLayout.NORTH);

    }

    private void searchRow(JTable jtbl, String searchText) {


    }
}

class DiagPanel extends JPanel {
    private File rootDir;

    public DiagPanel(FileTable ftb) {

    }
}




/*
class FileElement {
    private String name;
    private String type;
    private long size;
    private long lastMod;
    private File file;
    private Vector<FileElement> subFileVectors = null;

    public FileElement(File f) {
        this.name = f.getName();
        this.type = FileOps.getFileExtension(f);
        this.lastMod = f.lastModified();
        this.file = f;
    }

    public void addSubFile(File f) {
        if(this.subFileVectors == null) {
            this.subFileVectors = new Vector<>();
        }
        FileElement subFE = new FileElement(f);
        this.subFileVectors.add(subFE);
    }
    public Vector<FileElement> getSubFiles() {
        return this.subFileVectors;
    }
}

class constructFileVector implements FileVisitor<File> {

    private Vector<FileElement> rootVector = new Vector<>();
    private FileElement currentElem;

    public Vector<FileElement> getFileVector() {
        return rootVector;
    }

    @Override
    public FileVisitResult preVisitDirectory(File dir, BasicFileAttributes attrs) throws IOException {
        currentElem = new FileElement(dir);
        rootVector.add(currentElem);
        return FileVisitResult.CONTINUE;   
    }
    @Override
    public FileVisitResult visitFile(File f, BasicFileAttributes attrs) throws IOException {
        currentElem.addSubFile(f);
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult visitFileFailed(File f, IOException exc) throws IOException {
        System.out.println("visitFailed: " + f.toPath());
        return FileVisitResult.CONTINUE;
    }
    @Override
    public FileVisitResult postVisitDirectory(File dir, IOException exc) throws IOException {
        currentElem = currentElem.get
    }
}

*/