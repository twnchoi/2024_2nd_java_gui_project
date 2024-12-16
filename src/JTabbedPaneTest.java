import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


class JPanel011 extends JPanel {      //  1번 패널 
    private JButton jButton1;
    private JScrollPane jScrollPane1;
    private JTextArea jTextArea1;
    
    public JPanel011(){        // 1번째 패널 생성자
        setLayout(null);
        
        jButton1 = new JButton("버튼");
        jButton1.setSize(70,20);        
        jButton1.setLocation(10, 10);
        add(jButton1);
        
        jTextArea1 = new JTextArea();

        jScrollPane1 = new JScrollPane(jTextArea1);   // 스크롤 보이게!!
        jScrollPane1.setSize(200,150);
        jScrollPane1.setLocation(10,40);
        add(jScrollPane1);
    }
}


class JPanel022 extends JPanel{        // 2번째 패널
    private JTextField textField;
    private JPasswordField passwordField;
    
    public JPanel022() {        // 2번째 패널 생성자
        
        setLayout(null);
        
        JLabel lblLbl = new JLabel("아이디:");
        lblLbl.setBounds(31, 40, 67, 15);
        add(lblLbl);
        
        textField = new JTextField();
        textField.setBounds(123, 40, 116, 21);
        add(textField);
        textField.setColumns(10);
        
        JLabel lblLbl_1 = new JLabel("암호:");
        lblLbl_1.setBounds(31, 84, 67, 15);
        add(lblLbl_1);
        
        passwordField = new JPasswordField();
        passwordField.setBounds(123, 84, 116, 21);
        add(passwordField);
        
        JButton btn = new JButton("버튼");
        btn.setSize(70,20);
        btn.setLocation(10,10);
        add(btn);
    }
}


class JPanel033 extends JPanel{        // 3번째 패널

    // 클래스 멤버 필드 설정
    private    JLabel labelName;
    private    JLabel labelSex;
    private    JLabel labelHobby;
    private    JLabel labelNation;
    private    JLabel labelIntroduce;
            
    private    JTextField tfName;
        
    private    JRadioButton radioBoy;
    private    JRadioButton radioGirl;
    private    JRadioButton  [] sex= new JRadioButton[2];
    private    String sexx = "";
            
    private    JCheckBox  [] checkbox= new JCheckBox[3];
    private    JCheckBox checkMovie;
    private    JCheckBox checkReding;
    private    JCheckBox checkCicle;
    private    String [] check = {"","",""};
            
    private    JComboBox comboxNation;       
    private    String []nationlist = {"한국","영국","미국","중국","일본","독일","브라질","스페인","스웨덴"}; 
        
    private    JTextArea taIntroduce;
    private    JButton buttonSave;

    
    public JPanel033() {         // 3번째 패널 생성자
        
        setLayout(null);
        // 라벨
        labelName = new JLabel("이    름:");
        labelName.setSize(100, 20);   // setBounds가 아니면 setSize와 setLocation을 동시에 사용해야함
        labelName.setLocation(20, 50);
        labelSex = new JLabel("성    별:");
        labelSex.setBounds(20,100,100,20);
        labelHobby = new JLabel("취    미:");
        labelHobby.setBounds(20,150,100,20);
        labelNation = new JLabel("국    가:");
        labelNation.setBounds(20,200,100,20);
        labelIntroduce = new JLabel("소    개:");
        labelIntroduce.setBounds(20,250,100,20);
            
        // 텍스트 필드
        tfName = new JTextField();              // 이름 입력 부분
        tfName.setBounds(100,50,200,20);    //   위지 와 사이즈
            
        // 라디오 버튼
        ButtonGroup g = new ButtonGroup();      // 라디오버튼중에서 한가지만 선택하기 위한 그룹 설정
        sex[0] = new JRadioButton("남");
        sex[0].setBounds(100,100,50,20);
        sex[1] = new JRadioButton("여");
        sex[1].setBounds(200,100,100,20);
        g.add(sex[0]);
        g.add(sex[1]);
                
        // 체크박스
        checkbox[0] = new JCheckBox("영화");
        checkbox[0] .setBounds(100,150,60,20);
        checkbox[1]= new JCheckBox("독서"); 
        checkbox[1].setBounds(160,150,60,20);
        checkbox[2] = new JCheckBox("자전거");
        checkbox[2].setBounds(220,150,80,20);
            
        // 콤보박스
        comboxNation = new JComboBox(nationlist);
        comboxNation.setBounds(100,200,80,20);
        
        // 텍스트 에어리어 , 결과 출력부분
        taIntroduce = new JTextArea();
        taIntroduce.setEditable(false);
        // JTextArea는 JScrollPane를 써야 스크롤바가 생긴다.
        JScrollPane scrollPane = new JScrollPane(taIntroduce);
        scrollPane.setBounds(100,255,200,150);
        
        // 버튼        
        buttonSave = new JButton("저장 ");
        buttonSave.setBounds(150,430,100,20);
        buttonSave.addActionListener(new EventHandlerSave());   // 버튼 리스너 등록
        // 프레임의 컨테이너의 각종 컴포넌트들을 등록
        add(labelName);
        add(labelSex);
        add(labelHobby);
        add(labelNation);
        add(labelIntroduce);
        add(tfName);
        
        add(sex[0]);
        add(sex[1]);
        
        add(checkbox[0]);
        add(checkbox[1]);
        add(checkbox[2]);
        
        add(comboxNation);
    
        add(scrollPane);
        add(buttonSave);
    }
    
    class EventHandlerSave implements ActionListener{     // 
        public void actionPerformed(ActionEvent e){    
            
            // 텍스트 필드값 가져오기
            String a = tfName.getText();        
            
            // 라디오 버튼 값 가져오기
            if(sex[0].isSelected())
                sexx="남";
            else if(sex[1].isSelected())
                 sexx="여";    
            
            // 체크박스 값 가져오기
            if(checkbox[0].isSelected())
                 check[0] = "영화";
            else check[0] = "";
            if(checkbox[1].isSelected())
                 check[1] = "독서";
            else check[1] = "";
            if(checkbox[2].isSelected())
                 check[2] = "자전거";
            else check[2] = "";
             
            // 콤보박스 값 가져오기
            String d = (String) comboxNation.getSelectedItem();
            
            // TextArea 필드에 출력 보이기
            taIntroduce.append("이름 = " + a + "\n");
            taIntroduce.append("성별 = " + sexx+"\n");
            taIntroduce.append("취미 = " + check[0] + check[1]+check[2]+"\n");
            taIntroduce.append("국가 = " +d+"\n");

        }    
    }     
    //  EventHandlerSave
}


public class JTabbedPaneTest extends JFrame{
    
    public JPanel011 jpanel01 = null;
    public JPanel022 jpanel02 = null;
    public JPanel033 jpanel03 = null;
    
    public static void main(String[] args) {
        JTabbedPaneTest win = new JTabbedPaneTest();
        win.setTitle("frame test");
        win.jpanel01 = new JPanel011();
        win.jpanel02 = new JPanel022();
        win.jpanel03 = new JPanel033();
        
        JTabbedPane jtab = new JTabbedPane();   //  JTabbedPane  객체 생성
        
        jtab.addTab("화면1", win.jpanel01 );
        jtab.addTab("화면2", win.jpanel02 );
        jtab.addTab("화면3", win.jpanel03 );
        
        win.add(jtab);
        
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.setSize(500,700);
        win.setVisible(true);    
    }
}