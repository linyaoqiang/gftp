package com.study.gftp.server;

import com.study.gftp.GFTPUser;
import com.study.gftp.helper.GFTPHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GFTPServerFrame {

    private JFrame frame;
    private JTextField rootPathField;
    private JTextField portsField;
    private JButton startBtn;
    private JButton stopBtn;
    private JButton addUserBtn;
    private static final String TITLE = "GFtp服务器";
    private GFTPServer server;
    private List<GFTPUser> users;
    private GFTPServerUserFrame serverUserFrame;

    /**
     * Create the application.
     */
    public GFTPServerFrame() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setResizable(false);
        frame.setBounds(100, 100, 613, 385);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);
        frame.setTitle(TITLE);
        JLabel titleLabel = new JLabel(TITLE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.ITALIC, 25));
        titleLabel.setBounds(133, 34, 332, 39);
        frame.getContentPane().add(titleLabel);

        JLabel rootPathLabel = new JLabel("\u670D\u52A1\u5668\u6839\u76EE\u5F55");
        rootPathLabel.setFont(new Font("方正舒体", Font.PLAIN, 20));
        rootPathLabel.setBounds(83, 100, 127, 31);
        frame.getContentPane().add(rootPathLabel);

        rootPathField = new JTextField();
        rootPathField.setFont(new Font("宋体", Font.PLAIN, 20));
        rootPathField.setBounds(236, 100, 297, 31);
        frame.getContentPane().add(rootPathField);
        rootPathField.setColumns(10);

        JLabel portsLabel = new JLabel("\u76D1\u542C\u7AEF\u53E3(\u591A\u4E2A\u7AEF\u53E3,\u9694\u5F00)");
        portsLabel.setFont(new Font("方正舒体", Font.PLAIN, 20));
        portsLabel.setBounds(83, 182, 236, 31);
        frame.getContentPane().add(portsLabel);

        portsField = new JTextField();
        portsField.setFont(new Font("宋体", Font.PLAIN, 20));
        portsField.setColumns(10);
        portsField.setBounds(328, 182, 205, 31);
        frame.getContentPane().add(portsField);

        startBtn = new JButton("\u5F00\u542F\u670D\u52A1\u5668");
        startBtn.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        startBtn.setBounds(45, 270, 165, 39);
        frame.getContentPane().add(startBtn);

        stopBtn = new JButton("\u5173\u95ED\u670D\u52A1\u5668");
        stopBtn.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        stopBtn.setBounds(220, 270, 165, 39);
        frame.getContentPane().add(stopBtn);

        addUserBtn = new JButton("\u6DFB\u52A0\u7528\u6237");
        addUserBtn.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        addUserBtn.setBounds(399, 270, 165, 39);
        frame.getContentPane().add(addUserBtn);

        frame.setVisible(true);

        addEvent();
    }

    public void addEvent() {
        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String rootPath = rootPathField.getText();
                String[] portStrings = portsField.getText().split(",");
                List<Integer> ports = new ArrayList<>();
                for (String port : portStrings) {
                    ports.add(Integer.parseInt(port));
                }
                File rootFile = new File((rootPath));
                if (!rootFile.exists() || !rootFile.isDirectory()) {
                    JOptionPane.showMessageDialog(frame,"不是一个有效的文件夹");
                    return;
                }

                try {
                    server = new GFTPServer();
                    GFTPServerConfiguration configuration = GFTPHelper.createConfiguration(GFTPServer.DEFAULT_MAX_OBJECT_SIZE, rootPath, ports);
                    configuration.setUsers(users);
                    if (serverUserFrame != null) {
                        serverUserFrame.getFrame().setVisible(false);
                    }
                    server.init(configuration);
                    JOptionPane.showMessageDialog(frame,"服务器已经启动");
                    frame.setTitle(TITLE + "---服务开启中");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame,ex.getMessage());
                }
            }
        });
        stopBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (server != null) {
                    server.release();
                    frame.setTitle(TITLE);
                    JOptionPane.showMessageDialog(frame,"服务器已关闭");
                }

            }
        });
        addUserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (serverUserFrame == null) {
                    serverUserFrame = new GFTPServerUserFrame(GFTPServerFrame.this);
                }
                serverUserFrame.getFrame().setVisible(true);
            }
        });
    }

    public void addUser(GFTPUser user) {
        if (users == null) {
            users = new ArrayList<>();
        }
        users.add(user);
    }

    private void resetUsers() {
        this.users = null;
    }
}
