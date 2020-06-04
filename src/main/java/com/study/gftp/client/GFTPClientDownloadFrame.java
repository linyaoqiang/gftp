package com.study.gftp.client;

import com.study.gftp.FileDocumentation;
import com.study.gftp.exception.AuthenticationException;

import javax.swing.*;
import java.awt.*;

/**
 * 下载窗口
 */
public class GFTPClientDownloadFrame {

    private JFrame frame;
    private JLabel showNameLabel;
    private JLabel showAddressLabel;
    private JLabel showLocationLabel;
    private JLabel showProgressLabel;
    private int alignment = SwingConstants.CENTER;
    private Font font = new Font("微软雅黑", Font.PLAIN, 20);

    /**
     * Create the application.
     */
    public GFTPClientDownloadFrame() {
        initialize();
        initUI();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle("下载");
        frame.setResizable(false);
        frame.setBounds(100, 100, 749, 321);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel nameLabel = new JLabel("下载文件名");
        nameLabel.setBounds(47, 13, 162, 45);
        frame.getContentPane().add(nameLabel);

        showNameLabel = new JLabel("");
        showNameLabel.setBounds(211, 13, 451, 45);
        frame.getContentPane().add(showNameLabel);

        JLabel addressLabel = new JLabel("服务器地址");
        addressLabel.setBounds(47, 71, 162, 45);
        frame.getContentPane().add(addressLabel);

        showAddressLabel = new JLabel("");
        showAddressLabel.setBounds(211, 71, 451, 45);
        frame.getContentPane().add(showAddressLabel);

        JLabel locationLabel = new JLabel("下载位置");
        locationLabel.setBounds(50, 131, 162, 45);
        frame.getContentPane().add(locationLabel);

        showLocationLabel = new JLabel("");
        showLocationLabel.setBounds(214, 131, 451, 45);
        frame.getContentPane().add(showLocationLabel);

        JLabel progressLabel = new JLabel("进度");
        progressLabel.setBounds(47, 196, 162, 45);
        frame.getContentPane().add(progressLabel);

        showProgressLabel = new JLabel("");
        showProgressLabel.setBounds(211, 196, 451, 45);
        frame.getContentPane().add(showProgressLabel);
    }

    /**
     * 初始化UI
     */
    private void initUI() {
        Component[] components = frame.getRootPane().getContentPane().getComponents();
        for (Component component : components) {
            component.setFont(font);
            ((JLabel) component).setHorizontalAlignment(alignment);
        }
    }

    /**
     * 进行下载
     *
     * @param ip            服务器地址
     * @param port          服务器端口
     * @param destPath      下载地址
     * @param documentation 文件文档对象
     * @param client        进行下载核心对象，Gftp客户端
     */
    public void download(String ip, int port, String destPath, FileDocumentation documentation, GFTPClient client) {
        try {
            showNameLabel.setText(documentation.getFileName());
            showAddressLabel.setText(ip + ":" + port + documentation.getCurrentParent());
            showLocationLabel.setText(destPath);
            showProgressLabel.setText("连接中");
            client.init(ip, port);
            showProgressLabel.setText("下载中");
            client.downloadSync(documentation.getCurrentParent(), documentation.getFileName(), destPath);
            JOptionPane.showMessageDialog(frame, "下载成功");
        } catch (InterruptedException | AuthenticationException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, e.getMessage());
        } finally {
            /**
             * 展示和释放资源
             */

            client.close();
            frame.setVisible(false);
        }
    }

    public JFrame getFrame() {
        return frame;
    }
}
