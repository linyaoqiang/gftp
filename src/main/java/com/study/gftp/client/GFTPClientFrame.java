package com.study.gftp.client;

import com.study.gftp.FileDocumentation;
import com.study.gftp.exception.AuthenticationException;

import java.awt.*;

import javax.swing.*;

import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;


public class GFTPClientFrame {
    /**
     * 组件
     */
    private JFrame frame;
    private JTextField addressField;
    private JTextField fileField;
    private JTextField dirNameField;
    private JButton addressBtn;
    private JButton browseBtn;
    private JButton createDirBtn;
    private JButton uploadBtn;
    private JPanel panel;
    private JScrollPane scrollPane;
    private JPopupMenu popupMenu;
    private JMenuItem downloadItem;
    private JMenuItem deleteItem;
    private JButton backBtn;


    /**
     * 监听
     */
    private ActionListener addressBtnListener;
    private ActionListener browseBtnListener;
    private ActionListener createDirBtnListener;
    private ActionListener uploadBtnListener;
    private MouseListener labelListener;
    private ActionListener downloadItemListener;
    private ActionListener deleteItemListener;
    private ActionListener backBtnListener;
    /**
     * 程序运行时必须的全局变量
     */
    private String rootURL;
    private String ip;
    private Integer port;
    private String path;
    private String username;
    private String password;
    private String address;
    private String lastRootURL;
    private GFTPClient searchClient;
    private static final String TITLE = "Gftp客户端";
    private FileDocumentation selectedDocumentation;

    /**
     * 重复使用的变量
     */
    private Map<JLabel, FileDocumentation> labels = new HashMap<>();
    private JFileChooser chooser = new JFileChooser();
    private List<GFTPClientDownloadFrame> downloadFrames;
    private List<GFTPClientUploadFrame> uploadFrames;
    /**
     * 线程池，提高效率
     */
    private ExecutorService service = Executors.newFixedThreadPool(8);

    /**
     * 正则表达式
     */
    private String loginURLReg = "gftp@[\\w\\W]+:[\\w\\W]+@://[\\w\\W]+:\\d+(/[\\w\\W]*)*";
    private String urlReg = "gftp://[\\w\\W]+:\\d+(/[\\w\\W]*)*";

    /**
     * 修饰，使得程序美观一些，java swing很落后
     */
    private Icon buttonIcon;
    private Icon backIcon;
    private Icon folderIcon;
    private Icon fileIcon;
    private Icon rarIcon;
    private Font font = new Font("微软雅黑", Font.PLAIN, 20);
    private GFTPClientLabelUI labelUI = new GFTPClientLabelUI();
    private Color buttonBgColor = SystemColor.inactiveCaptionBorder;
    private Color buttonForeground = new Color(30, 144, 255);
    private Font fileLabelFont = new Font("Dialog", Font.PLAIN, 15);

    /**
     * Create the application.
     */
    public GFTPClientFrame() {
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        //初始化控件
        initComponents();
        //初始化图标
        initIcon();
        //出事化UI
        initUI();
        //初始化按键图标
        initButtonIcon();
        //初始化两个窗体程序
        initFrame();
        //初始化监听器
        initListener();
        //添加监听器
        addEvent();
        frame.setVisible(true);
        //通知垃圾回收器清理垃圾内存
        System.gc();
    }

    private void initComponents() {
        frame = new JFrame();
        frame.setTitle(TITLE);
        frame.setBounds(100, 100, 886, 591);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);


        JLabel addressLabel = new JLabel("\u5730\u5740");
        addressLabel.setBounds(79, 13, 68, 45);
        frame.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(161, 13, 514, 45);
        frame.add(addressField);
        addressField.setColumns(10);

        addressBtn = new JButton("\u8FDB\u5165");
        addressBtn.setBounds(699, 13, 142, 45);
        frame.add(addressBtn);

        panel = new JPanel();
        panel.setBackground(new Color(221, 221, 221));
        panel.setBounds(24, 69, 698, 335);

        panel.setLayout(null);

        scrollPane = new JScrollPane(panel);
        scrollPane.setBounds(37, 71, 804, 322);
        GFTPClientLabelUI labelUI = new GFTPClientLabelUI();
        frame.add(scrollPane);

        JLabel uploadLabel = new JLabel("上传文件到该目录下");
        uploadLabel.setHorizontalAlignment(SwingConstants.CENTER);
        uploadLabel.setBounds(37, 419, 211, 45);
        frame.add(uploadLabel);

        fileField = new JTextField("");
        fileField.setColumns(10);
        fileField.setBounds(239, 419, 356, 45);
        frame.add(fileField);

        browseBtn = new JButton("浏览");
        browseBtn.setBounds(597, 419, 117, 45);
        frame.getContentPane().add(browseBtn);

        uploadBtn = new JButton("上传");
        uploadBtn.setForeground(new Color(30, 144, 255));
        uploadBtn.setBounds(716, 419, 125, 45);
        frame.add(uploadBtn);

        JLabel createDirLabel = new JLabel("该目录下创建文件夹");
        createDirLabel.setBounds(42, 477, 206, 45);
        frame.add(createDirLabel);

        dirNameField = new JTextField();
        dirNameField.setColumns(10);
        dirNameField.setBounds(239, 477, 356, 45);
        frame.add(dirNameField);

        createDirBtn = new JButton("创建");
        createDirBtn.setBounds(659, 477, 117, 45);
        frame.add(createDirBtn);

        popupMenu = new JPopupMenu();
        popupMenu.setLocation(20, 33);
        popupMenu.add(downloadItem = new JMenuItem("下载"));
        popupMenu.add(deleteItem = new JMenuItem("删除"));
        downloadItem.setFont(font);
        deleteItem.setFont(font);

        backBtn = new JButton("..");
        backBtn.setBounds(14, 13, 73, 45);
        frame.add(backBtn);
    }

    private void initUI() {
        Component[] components = frame.getRootPane().getContentPane().getComponents();
        for (Component component : components) {
            component.setFont(font);
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setHorizontalAlignment(SwingConstants.CENTER);
            } else if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setForeground(buttonForeground);
                button.setBackground(buttonBgColor);
            }
        }
    }

    private void initIcon() {
        File file = new File("images");
        String basePath = file.getAbsolutePath() + File.separator;
        buttonIcon = new ImageIcon(basePath + "button24.png");
        backIcon = new ImageIcon(basePath + "back.png");
        folderIcon = new ImageIcon(basePath + "folder.png");
        fileIcon = new ImageIcon(basePath + "file.png");
        rarIcon = new ImageIcon(basePath + "rar.png");
    }

    private void initButtonIcon() {
        Component[] components = frame.getRootPane().getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setIcon(buttonIcon);
            }
        }
        backBtn.setIcon(backIcon);
    }

    private void initFrame() {
        uploadFrames = new ArrayList<>();
        downloadFrames = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            uploadFrames.add(new GFTPClientUploadFrame());
            downloadFrames.add(new GFTPClientDownloadFrame());
        }
    }

    public List<FileDocumentation> resetPathFile() throws AuthenticationException, InterruptedException {
        if (ip == null || port == null) {
            JOptionPane.showMessageDialog(frame, "请先在地址栏输入地址信息并获取目录信息");
            return null;
        }
        List<FileDocumentation> documentations = searchClient.readPathFile(path);
        return documentations;
    }

    private void initListener() {
        addressBtnListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String address = addressField.getText();
                //判断是否是正确的请求URL
                if (!Pattern.matches(loginURLReg, address) && !Pattern.matches(urlReg, address)) {
                    JOptionPane.showMessageDialog(frame, "请输入正确的地址，" +
                            "如: gftp@username:password@://localhost:port/address\r\n" +
                            "或者是gftp://localhost:port/address");
                    return;
                }
                try {
                    //判断与上一次请求的url是否相同，如果相同则返回，什么都不做
                    if (equalsAddress(address)) {
                        return;
                    }
                    //解析请求的url地址，并存储对应的信息
                    parseAddress(address);

                    //如果上一次的rootURl不一样则说明更换地址.所以进行GFTPClient的替换
                    // 主要是内部connect方法使用从init方法获取到的数据
                    if (!rootURL.equals(lastRootURL)) {
                        if (searchClient != null) {
                            //释放资源
                            searchClient.close();
                        }
                        //创建新的GFTPClient
                        searchClient = new GFTPClient();
                        //创建GFTPUser，方便下一次在获取数据时，携带信息
                        searchClient.login(username, password);
                        //进行初始化
                        searchClient.init(ip, port);
                    }
                    //更新界面
                    updatePanel();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }
        };
        browseBtnListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //选择文件，并将选择的文件名放置在fileField文本框中
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int status = chooser.showOpenDialog(frame);
                if (status == JFileChooser.APPROVE_OPTION) {
                    fileField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        };
        uploadBtnListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ip == null || port == null || port == 0) {
                    JOptionPane.showMessageDialog(frame, "请你先在地址栏输入地址信息并获取目录信息");
                    return;
                }
                File file = new File(fileField.getText());
                /*if (file.length() > 1024 * 1024 * 300) {
                    JOptionPane.showMessageDialog(frame, "该文件太大了，无法下载，该文件大小为 :" + file.getTotalSpace() / 1024 / 1024 + "MB");
                    return;
                }*/
                if (file.exists() && file.isFile()) {
                    GFTPClient client = new GFTPClient();
                    client.login(username, password);
                    service.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (uploadFrames.size() <= 0) {
                                JOptionPane.showMessageDialog(frame, "上传数最多4个");
                                return;
                            }
                            GFTPClientUploadFrame uploadFrame = uploadFrames.remove(0);
                            uploadFrame.getFrame().setVisible(true);
                            uploadFrame.upload(ip, port, path, file, client);
                            try {
                                updatePanel();
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(frame, ex.getMessage());
                            } finally {
                                if (uploadFrame != null) {
                                    uploadFrames.add(uploadFrame);
                                }
                            }
                        }
                    });
                    return;
                }
                JOptionPane.showMessageDialog(frame, "文件不存在或者不是文件");
            }
        };
        labelListener = new MouseAdapter() {

            //点击时，判断是否为文件夹，如果是则更新目录URL等
            @Override
            public void mouseClicked(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                FileDocumentation documentation = labels.get(label);
                if (documentation.isDirectory()) {
                    GFTPClientFrame.this.path = documentation.getCurrentParent() + documentation.getFileName();
                    addressField.setText(rootURL + path);
                    GFTPClientFrame.this.address = addressField.getText();
                    try {
                        updatePanel();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, ex.getMessage());
                    }
                }
            }

            //这两个事件中有一个操作系统弹出菜单的事件，使用其弹出
            public void mousePressed(MouseEvent e) {
                selectDocumentation(e);
            }

            public void mouseReleased(MouseEvent e) {
                selectDocumentation(e);
            }

            private void selectDocumentation(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                FileDocumentation documentation = labels.get(label);
                if (documentation == null) {
                    return;
                }
                selectedDocumentation = documentation;
                if (e.isPopupTrigger()) {
                    showMenu(e);
                }
            }

            private void showMenu(MouseEvent e) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
            }

        };
        downloadItemListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedDocumentation == null) {
                    return;
                }
                if (selectedDocumentation.isDirectory()) {
                    JOptionPane.showMessageDialog(frame, "文件夹无法下载");
                    return;
                }
                /*if (selectedDocumentation.getLength() > 1024 * 1024 * 300) {
                    JOptionPane.showMessageDialog(frame, "文件太大了,请尽量下载小于300MB的文件，无法下载，该文件大小为:" + selectedDocumentation.getLength() / 1024 / 1024 + "MB");
                    return;
                }*/
                int status = JOptionPane.showConfirmDialog(frame, "您确定要下载吗?", "下载", JOptionPane.OK_CANCEL_OPTION);
                if (status == JOptionPane.OK_OPTION) {
                    chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    status = chooser.showOpenDialog(frame);
                    if (status == JFileChooser.APPROVE_OPTION) {
                        File file = chooser.getSelectedFile();
                        String destPath = new File(file, selectedDocumentation.getFileName()).getAbsolutePath();
                        service.execute(new Runnable() {
                            @Override
                            public void run() {
                                if (downloadFrames.size() <= 0) {
                                    JOptionPane.showMessageDialog(frame, "下载数最多4个");
                                }
                                GFTPClientDownloadFrame downloadFrame = downloadFrames.remove(0);
                                GFTPClient client = new GFTPClient();
                                client.login(username, password);
                                downloadFrame.getFrame().setVisible(true);
                                downloadFrame.download(ip, port, destPath, selectedDocumentation, client);
                                downloadFrames.add(downloadFrame);
                            }
                        });
                    }
                }
            }
        };
        deleteItemListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (selectedDocumentation == null) {
                    return;
                }

                int status = JOptionPane.showConfirmDialog(frame, "您确定要删除吗?", "删除", JOptionPane.OK_CANCEL_OPTION);
                if (status == JOptionPane.OK_OPTION) {
                    try {
                        String ok = searchClient.deleteFile(selectedDocumentation);
                        if ("true".equals(ok)) {
                            JOptionPane.showMessageDialog(frame, "删除成功");
                            updatePanel();
                        } else {
                            JOptionPane.showMessageDialog(frame, "删除失败");
                        }
                    } catch (InterruptedException | AuthenticationException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, ex.getMessage());
                    }
                }
            }
        };
        createDirBtnListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String folderName = dirNameField.getText();
                System.out.println(folderName);
                if (!"".equals(folderName)) {
                    try {
                        String ok = searchClient.createFolder(path, folderName);
                        if ("true".equals(ok)) {
                            JOptionPane.showMessageDialog(frame, "创建成功");
                            updatePanel();
                        } else {
                            JOptionPane.showMessageDialog(frame, "创建失败");
                        }
                    } catch (InterruptedException | AuthenticationException ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(frame, ex.getMessage());
                    }
                }
            }
        };
        backBtnListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ip == null || port == null || rootURL == null) {
                    return;
                }
                if (path.equals("") || path.equals("/")) {
                    return;
                }
                if (path.endsWith("/")) {
                    path = path.substring(0, path.lastIndexOf("/"));
                }
                path = path.substring(0, path.lastIndexOf("/"));
                address = rootURL + path;
                addressField.setText(address);
                try {
                    updatePanel();
                } catch (AuthenticationException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, "认证失败信息:" + ex.getMessage());
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }
        };
    }

    /**
     * 更新文件面板的主要方法
     *
     * @throws AuthenticationException
     * @throws InterruptedException
     */
    private void updatePanel() throws AuthenticationException, InterruptedException {
        List<FileDocumentation> documentations = resetPathFile();
        if (documentations == null) {
            return;
        }
        if (documentations.size() == 0) {
            JOptionPane.showMessageDialog(frame, "该目录为空，或者是当前不是一个目录");
        }
        resetPanel(documentations);
        System.gc();
    }

    /**
     * 重置面板,并重新渲染
     *
     * @param fileDocumentations
     */
    public void resetPanel(List<FileDocumentation> fileDocumentations) {
        //重置
        labels.clear();
        panel.removeAll();
        //遍历获取到每一个FileDocumentation
        for (int i = 0; i < fileDocumentations.size(); i++) {
            FileDocumentation documentation = fileDocumentations.get(i);
            JLabel label = new JLabel(documentation.getFileName());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setUI(labelUI);
            //设置图标
            if (documentation.isDirectory()) {
                label.setIcon(folderIcon);
            } else if (isRarFile(documentation.getFileName())) {
                label.setIcon(rarIcon);
            } else {
                label.setIcon(fileIcon);
            }
            label.setFont(fileLabelFont);
            //label.setBounds(15, 15, 115, 50);
            label.setSize(145, 85);
            label.setLocation((i % 5) * (145 + 15), (i / 5) * 100);
            //添加到面板中
            panel.add(label);
            //添加监听
            label.addMouseListener(labelListener);
            //label与documentation的映射
            labels.put(label, documentation);
        }
        int addSize = 0;
        if (fileDocumentations.size() % 5 == 0) {
            addSize = 70;
        }
        //重新设置大小panel大小，根据Label的个数
        panel.setPreferredSize(new Dimension(145 * 5 - 15, 100 * (fileDocumentations.size() / 5) + addSize));
        //更新界面
        panel.updateUI();
    }

    /**
     * 简单判断是不是一个压缩包文件
     *
     * @param fileName
     * @return
     */
    private boolean isRarFile(String fileName) {
        return fileName.equals(".rar") || fileName.equals(".tar.gz") || fileName.equals(".zip");
    }

    /**
     * 添加监听
     */
    private void addEvent() {
        addressBtn.addActionListener(addressBtnListener);
        browseBtn.addActionListener(browseBtnListener);
        uploadBtn.addActionListener(uploadBtnListener);
        downloadItem.addActionListener(downloadItemListener);
        deleteItem.addActionListener(deleteItemListener);
        createDirBtn.addActionListener(createDirBtnListener);
        backBtn.addActionListener(backBtnListener);
    }

    /**
     * 解析URL的方法
     *
     * @param address
     * @return
     */
    private boolean parseAddress(String address) {
        this.lastRootURL = this.rootURL;
        this.address = address;
        String temp = address.substring(4);
        String username = null;
        String password = null;
        if (temp.startsWith("@")) {
            username = temp.substring(1, temp.indexOf(":"));
            temp = temp.substring(temp.indexOf(":"));
            password = temp.substring(1, temp.indexOf("@"));
        }
        temp = temp.substring(temp.indexOf("://") + 3);
        String ip = temp.substring(0, temp.indexOf(":"));
        temp = temp.substring(temp.indexOf(":") + 1);
        String port = "";
        if (temp.contains("/")) {
            port = temp.substring(0, temp.indexOf("/"));
            temp = temp.substring(temp.indexOf("/"));
        } else {
            port = temp;
            temp = "";
        }
        this.path = temp;
        this.rootURL = address.substring(0, address.lastIndexOf(path));

        this.ip = ip;
        this.username = username;
        this.password = password;
        this.port = Integer.parseInt(port);
        return true;
    }

    /**
     * 用于判断上一次的URL是否与当前次相同
     *
     * @param address
     * @return
     */
    private boolean equalsAddress(String address) {
        if (address.equals(this.address)) {
            return true;
        }
        if (this.address == null) {
            return false;
        }
        String url = this.address;
        if (url.endsWith("/")) {
            url = url.substring(0, url.lastIndexOf("/"));
        } else {
            url = url + "/";
        }
        if (url.equals(address)) {
            return true;
        }
        return false;
    }

}
