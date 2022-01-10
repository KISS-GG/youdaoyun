package top.zymnb.swings;

import org.apache.commons.lang3.StringUtils;
import top.zymnb.util.DownloadUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class Main {
    private JButton 下载文件Button;
    private JTextField textField1;
    private JTextField textField2;
    private JPanel Main;
    private JTextField textField3;
    private JButton 选择路径Button;

    public Main() {
        下载文件Button.addActionListener(e -> {

            // 验证参数
            String cookies = textField1.getText();
            String cstk = textField2.getText();
            String filePath = textField3.getText();
            if(StringUtils.isBlank(cookies)){
                JOptionPane.showMessageDialog(Main,"请输入cookies！");
                return;
            }

            if(StringUtils.isBlank(cstk)){
                JOptionPane.showMessageDialog(Main,"请输入cstk！");
                return;
            }
            if(StringUtils.isBlank(filePath)){
                JOptionPane.showMessageDialog(Main,"请选择文件保存路径！");
                return;
            }


            下载文件Button.setEnabled(false);
            下载文件Button.setText("正在下载中...");
            DownloadUtil.COOKIES = cookies;
            DownloadUtil.requestProperty.put("Cookie", cookies);
            DownloadUtil.CSTK = cstk;
            DownloadUtil.BASE_DIR = filePath;
            DownloadUtil.downloadWord();
            下载文件Button.setText("下载文件");
            下载文件Button.setEnabled(true);
        });

        选择路径Button.addActionListener(e -> {
            JFileChooser jFileChooser = new JFileChooser();
            jFileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jFileChooser.showDialog(Main,"选择");

            String filePath = jFileChooser.getSelectedFile().getAbsolutePath();
            textField3.setText(filePath);
        });
        textField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                //插入字符
                autoSetCstk();
            }
            @Override
            public void removeUpdate(DocumentEvent e) {
                //删除字符
                autoSetCstk();
            }
            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

    }

    public static void main(String[] args) {
        Main main = new Main();
        JPanel jPanel = main.Main;
        JFrame frame = new JFrame("Main");
        frame.setContentPane(jPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(400,400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        jPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        main.textField3.setEnabled(false);

        frame.setVisible(true);
    }

    public void autoSetCstk(){
        //字符改变
        String cookies = textField1.getText();
        String[] cookieArr = cookies.replace(" ","").split(";");
        for (String s : cookieArr) {
            String[] ss =  s.split("=");
            if ("YNOTE_CSTK".equals(ss[0])){
                textField2.setText(ss[1]);
            }
        }
    }
}
