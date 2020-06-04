package com.study.gftp.server;

import com.study.gftp.GFTPUser;
import com.study.gftp.helper.GFTPHelper;
import com.study.gftp.server.GFTPServerFrame;

import javax.swing.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GFTPServerUserFrame {

    private JFrame frame;
    private JTextField usernameField;
    private JTextField passwordField;
    private JTextField powerField;
    private JButton addBtn;
    public GFTPServerFrame serverFrame;

    /**
     * Create the application.
     */
    public GFTPServerUserFrame(GFTPServerFrame serverFrame) {
        this.serverFrame = serverFrame;
        initialize();
    }

    @SuppressWarnings("all")
    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setTitle("添加用户界面");
        frame.setBounds(100, 100, 410, 401);
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel titleLabel = new JLabel("\u6DFB\u52A0\u8FD0\u884C\u65F6\u7528\u6237");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("Microsoft JhengHei", Font.ITALIC, 25));
        titleLabel.setBounds(37, 33, 300, 41);
        frame.getContentPane().add(titleLabel);

        JLabel usernameLabel = new JLabel("\u7528\u6237\u540D");
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        usernameLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        usernameLabel.setBounds(48, 101, 114, 32);
        frame.getContentPane().add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        usernameField.setBounds(175, 100, 162, 33);
        frame.getContentPane().add(usernameField);
        usernameField.setColumns(10);

        JLabel passwordLabel = new JLabel("\u5BC6\u7801");
        passwordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        passwordLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        passwordLabel.setBounds(48, 156, 114, 32);
        frame.getContentPane().add(passwordLabel);

        passwordField = new JTextField();
        passwordField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        passwordField.setColumns(10);
        passwordField.setBounds(175, 155, 162, 33);
        frame.getContentPane().add(passwordField);

        JLabel powerLabel = new JLabel("\u6743\u9650(r,rw,w)");
        powerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        powerLabel.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        powerLabel.setBounds(48, 218, 114, 32);
        frame.getContentPane().add(powerLabel);

        powerField = new JTextField();
        powerField.setFont(new Font("微软雅黑", Font.PLAIN, 15));
        powerField.setColumns(10);
        powerField.setBounds(175, 217, 162, 33);
        frame.getContentPane().add(powerField);

        addBtn = new JButton("\u6DFB\u52A0\u7528\u6237");
        addBtn.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        addBtn.setBounds(122, 283, 145, 41);
        frame.getContentPane().add(addBtn);

        frame.setVisible(true);

        addEvent();
    }

    private void addEvent() {
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = usernameField.getText();
                String password = passwordField.getText();
                String power = powerField.getText();
                GFTPUser user = GFTPHelper.createUser(username, password, power);
                int ok = JOptionPane.showConfirmDialog(frame, "确定添加该用户吗?" + user, "添加用户", JOptionPane.OK_CANCEL_OPTION);
                if (ok == JOptionPane.OK_OPTION) {
                	serverFrame.addUser(user);
                	JOptionPane.showMessageDialog(frame,"添加成功");
                }else{
					JOptionPane.showMessageDialog(frame,"添加失败");
				}
            }
        });
    }

    public JFrame getFrame() {
        return frame;
    }
}
