package myApp;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import myLib.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

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
        pathField.setEditable(false);
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
        "<span style='font-family:times new roman;font-size:30px; font-weight:bold;'>DirDiag</span><br><br>" +
        "<span style='font-size:18px;'>Overview</span><br><br>" +
        "<span style='font-size:12px;'>Diganose and visualize directory composition.</span><br><br><br>" +
        "<span style='font-size:18px;'>How to use</span><br><br>" +
        "<span style='font-size:12px;'>Select a directory at the toolbar.</span><br>" +
        "<span style='font-size:12px;'>Explorer tab shows file sizes.</span><br>" +
        "<span style='font-size:12px;'> &nbsp;&nbsp;- Click at the row to reveal in file explorer.</span><br>" +
        "<span style='font-size:12px;'> &nbsp;&nbsp;- Click at the column name to sort.</span><br>" +
        "<span style='font-size:12px;'>Diagnose tab shows filetype sizes.</span><br>" +
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
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(mdl);
        jtbl.setRowSorter(sorter);

        jtbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(e.getClickCount() == 2) {
                    int selRow = jtbl.getSelectedRow();
                    selRow = jtbl.convertRowIndexToModel(selRow);
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
                    sorter.setRowFilter(RowFilter.regexFilter("(?i)" + jtxt.getText()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        topPanel.add(searchLabel, BorderLayout.CENTER);
        topPanel.add(jtxt, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

    }

}

class DiagPanel extends JPanel {
    private HashMap<String, Long> diagMap;

    public DiagPanel(FileTable ftb) {
        diagMap = ftb.getDiagMap();
        List<String> keySet = new ArrayList<>(diagMap.keySet());
        keySet.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return diagMap.get(o2).compareTo(diagMap.get(o1));
            }
        });
        


        JPanel graphPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                int chartWidth = getWidth() - 100;
                int x = 50;
                int y = 50;
                int barHeight = 50;
        
                int currentX = x;
                long rootSize = ftb.getRootSize();

                g.drawString("Graph View", x, y - 30);
                for (int i = 0; i < keySet.size(); i++) {
                    String fileName = keySet.get(i);
                    long fileSize = diagMap.get(keySet.get(i));
                    
                    //
                    int segmentWidth = (int) ((fileSize / (double) ftb.getRootSize()) * chartWidth);
        
                    g.setColor(getRandomColor(i));
                    g.fillRect(currentX, y, segmentWidth, barHeight);
        
                    String percentage = String.format("%s (%.2f%%)", fileName, FileOps.getRatio(fileSize, rootSize) * 100);
                    FontMetrics fm = g.getFontMetrics();
                    int textWidth = fm.stringWidth(percentage);
                    if(segmentWidth > textWidth) {
                        g.setColor(Color.BLACK);
                        g.drawString(percentage, currentX + segmentWidth / 2 - percentage.length() * 3, y - 10);
                    }

                    if(y + 100 + i * 20 < getHeight()) {
                        g.setColor(Color.BLACK);
                        g.drawString(fileName + " : " + FileOps.humanReadableByteCountBin(fileSize), x, y + 100 + i * 20);
                    }

                    currentX += segmentWidth;
                }

                
            }
        };


        String[] columns = {"Type", "Total Size", "Graph"};
        DefaultTableModel diagMdl = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int i,int c) {
                return false;
            }
        };

        for(String key : keySet) {
            long size = diagMap.get(key);
            diagMdl.addRow(new Object[]{key, FileOps.humanReadableByteCountBin(size), size});
        }
        
        JTable diagTable = new JTable(diagMdl);
        diagTable.getColumn("Type").setPreferredWidth(150);
        diagTable.getColumn("Total Size").setPreferredWidth(200);
        diagTable.getColumn("Graph").setPreferredWidth(450);
        diagTable.setDefaultRenderer(Object.class, new CustomRenderer(ftb));
        JScrollPane scrollPane = new JScrollPane(diagTable);

        setLayout(new BorderLayout(20, 20));
        graphPanel.setPreferredSize(new Dimension(getWidth(), 80));
        add(graphPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    private Color getRandomColor(int i) {
        int r = ((i+1) * 22)% 255;
        int g = ((i+1) * 73)% 255;
        int b = ((i+1) * 64)% 255;
        Color clr = new Color(r,g,b);
        return clr;
    }
}