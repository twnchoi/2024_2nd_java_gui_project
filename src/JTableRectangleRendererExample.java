import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class JTableRectangleRendererExample {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // 데이터와 컬럼 생성
            Object[][] data = {
                {"Alice", 30},
                {"Bob", 70},
                {"Charlie", 50},
                {"Dave", 90}
            };
            String[] columnNames = {"Name", "Progress"};

            // JTable 모델 생성
            DefaultTableModel model = new DefaultTableModel(data, columnNames);
            JTable table = new JTable(model);

            // 커스텀 렌더러 생성
            TableCellRenderer rectangleRenderer = new DefaultTableCellRenderer() {
                @Override
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    // 패널로 그래픽을 그리기 위해 새로운 컴포넌트를 반환
                    return new JPanel() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);

                            // 직사각형 그래픽 렌더링
                            if (value instanceof Integer) {
                                int progress = (Integer) value;

                                // 셀의 크기에 맞는 직사각형 그리기
                                int width = (int) (getWidth() * (progress / 100.0));
                                int height = getHeight();
                                

                                // 배경색 설정
                                g.setColor(Color.LIGHT_GRAY);
                                g.fillRect(0, 0, getWidth(), height);

                                // 직사각형 색상 설정 (파란색)
                                g.setColor(Color.BLUE);
                                g.fillRect(0, 0, width, height);

                                // 경계선 그리기
                                g.setColor(Color.BLACK);
                                g.drawRect(0, 0, getWidth() - 1, height - 1);
                            }
                        }
                    };
                }
            };

            // 특정 열에 커스텀 렌더러 설정
            table.getColumnModel().getColumn(1).setCellRenderer(rectangleRenderer);

            // JFrame 설정
            JFrame frame = new JFrame("Rectangle Renderer JTable Example");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new BorderLayout());
            frame.add(new JScrollPane(table), BorderLayout.CENTER);
            frame.setSize(400, 200);
            frame.setVisible(true);
        });
    }
}
