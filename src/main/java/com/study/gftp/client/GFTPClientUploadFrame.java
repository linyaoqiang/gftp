package com.study.gftp.client;

import com.study.gftp.FileDocumentation;

import java.awt.*;

import javax.swing.*;
import java.io.File;

/**
 * 上传窗口
 */
public class GFTPClientUploadFrame {

    private JFrame frame;
    private JLabel showNameLabel;
    private JLabel showAddressLabel;
    private JLabel showStatusLabel;
    private int alignment = SwingConstants.CENTER;
    private Font font = new Font("微软雅黑", Font.PLAIN, 20);

    public GFTPClientUploadFrame() {
        initialize();
        initUI();
    }

    private void initUI() {
        Component[] components = frame.getRootPane().getContentPane().getComponents();
        for (Component component : components) {
            component.setFont(font);
            ((JLabel) component).setHorizontalAlignment(alignment);
        }
    }

    /**
     * 进行下载
     * @param ip
     * @param port
     * @param path
     * @param file
     * @param client
     */
    public void upload(String ip, int port, String path, File file, GFTPClient client) {
        try {
            showNameLabel.setText(file.getName());
            showAddressLabel.setText(ip + ":" + port + path);
            showStatusLabel.setText("连接中");
            client.init(ip, port);
            showStatusLabel.setText("上传中");
            String message = client.uploadSync(file.getAbsolutePath(), path, file.getName());
            System.out.println(message);
            JOptionPane.showMessageDialog(frame, "上传成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, ex.getMessage());
        } finally {
            client.close();
            frame.setVisible(false);
        }
    }


    private void initialize() {
        frame = new JFrame();
        frame.setTitle("上传");
        frame.setResizable(false);
        frame.setBounds(100, 100, 679, 241);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLayout(null);

        JLabel nameLabel = new JLabel("上传文件名");
        nameLabel.setBounds(14, 27, 172, 45);
        frame.add(nameLabel);

        showNameLabel = new JLabel("");
        showNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        showNameLabel.setFont(new Font("Dialog", Font.PLAIN, 20));
        showNameLabel.setBounds(178, 27, 451, 45);
        frame.add(showNameLabel);

        JLabel statusLabel = new JLabel("上传状态");
        statusLabel.setBounds(14, 136, 172, 45);
        frame.add(statusLabel);

        showStatusLabel = new JLabel("");
        showStatusLabel.setBounds(178, 136, 451, 45);
        frame.add(showStatusLabel);

        showAddressLabel = new JLabel("");
        showAddressLabel.setBounds(178, 78, 451, 45);
        frame.add(showAddressLabel);

        JLabel addressLabel = new JLabel("服务器地址");
        addressLabel.setBounds(14, 78, 172, 45);
        frame.add(addressLabel);
    }

    public JFrame getFrame() {
        return frame;
    }
}
