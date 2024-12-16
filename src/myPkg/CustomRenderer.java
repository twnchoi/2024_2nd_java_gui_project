package myPkg;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import myPkg.FileOps;

public class CustomRenderer extends DefaultTableCellRenderer{
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private FileTable ftb;
    private String rootCnnPath;

    public CustomRenderer(FileTable ftb) {
        super();
        this.ftb = ftb;
        try {
            rootCnnPath = ftb.getRootFile().getCanonicalPath();
        } catch (IOException e) {}
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        
        String columnName = table.getColumnName(column);
        if("Size".equals(columnName)) {
            long val = (long)value;
            return new BarPanel(ftb.getRootSize(), val);
        }
        else if("Last Modified".equals(columnName)) {
            value = dateFormat.format(value);
        }
        else if("Directory".equals(columnName)) {
            value = ((String)value).replace(rootCnnPath, ".");
        }
        return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
    }
}

class BarPanel extends JPanel {
    private long totalSize;
    private long curSize;

    public BarPanel(long tot, long cur) {
        super();
        totalSize = tot;
        curSize = cur;
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        double ratio = (double) curSize / totalSize;
        int drawWidth = (int)(width * ratio);
        
        String drawStr = FileOps.humanReadableByteCountBin(curSize) + " / " + String.format("%.2f",ratio*100) + "%";


        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);
        g.setColor(Color.blue);
        g.fillRect(0, 0, drawWidth, height);
        g.setColor(Color.white);
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(drawStr);
        int textHeight = fm.getAscent();
        int descent = fm.getDescent();
        int textX = (getWidth() - textWidth) / 2;
        int textY = (getHeight() + textHeight) / 2 - descent;

        g.drawString(drawStr, textX, textY);
    }
}