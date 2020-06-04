package com.study.gftp.client;

import com.study.gftp.*;
import com.study.gftp.handler.ClientReadDataChannelHandler;
import com.study.gftp.handler.DownloadFileChannelHandler;
import com.study.gftp.helper.GFTPHelper;
import com.study.gftp.helper.MarshallingFactoryHelper;
import com.study.gftp.exception.AuthenticationException;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.io.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * GFtp客户端逻辑程序
 * 推荐在web应用中使用GFTPConnectionPool等，并且下载使用非阻塞式方法
 * 但是需要注意非阻塞式下载时不要同时下载多个文件,非阻塞式下载是线程极度不安全的方法
 * 主要原因是因为没有写标志为那一份下载数据的逻辑,将在下一个版本中通过非阻塞式下载多个文件
 */
public class GFTPClient {
    /**
     * 工作时的线程组
     */
    private EventLoopGroup workerGroup;

    /**
     * 客户端配置对象
     */
    private Bootstrap bootstrap;
    /**
     * 通道未来，用于写入数据等，与服务器进行交互
     */
    private ChannelFuture future;
    private ClientReadDataChannelHandler handler;
    /**
     * waitTime每一次等待服务器返回数据的时间等待时间
     */
    public static int waitTime = 50;

    private String ip;
    private int port;
    /**
     * 登录时的用户
     */
    private GFTPUser user;
    private boolean running;

    /**
     * 初始化
     *
     * @param ip   服务器ip地址，或者是域名
     * @param port 服务器端口
     * @throws InterruptedException
     */
    public void init(String ip, int port) throws InterruptedException {
        this.ip = ip;
        this.port = port;
        handler = new ClientReadDataChannelHandler(GFTPClient.this);
        //创建线程组
        workerGroup = new NioEventLoopGroup();
        //创建客户端启动配置对象
        bootstrap = new Bootstrap();

        //设置线程组
        bootstrap.group(workerGroup);

        //设置io模式NIO 非阻塞
        bootstrap.channel(NioSocketChannel.class);
        /**
         * handler方法类似于ServerBootstrap.childHandler()   添加处理器连
         */
        bootstrap.handler(new ChannelInitializer<Channel>() {

            @Override
            protected void initChannel(Channel ch) throws Exception {
                //添加处理器(处理器链,多个处理器的数组)可以使用单例的
                // 如果使用单例的话,其存在线程安全问题，在处理器的实现类使用@Sharable
                ch.pipeline().addLast(MarshallingFactoryHelper.buildMarshallingDecoder());
                ch.pipeline().addLast(MarshallingFactoryHelper.buildMarshallingEncoder());
                ch.pipeline().addLast(handler);

            }
        });
    }

    public void connect() throws InterruptedException {
        if (future == null || !future.channel().isActive()) {
            future = bootstrap.connect(ip, port).sync();
        }
    }

    /**
     * 优雅的关闭连接，会进行资源释放
     */
    public void release() {
        workerGroup.shutdownGracefully();
    }


    /**
     * 进行上传的核心方法
     *
     * @param in        输入流
     * @param path      服务器目录
     * @param fileName  上传到服务器的文件名
     * @param chunkSize 每一次装箱(byte[])的大小
     * @throws InterruptedException 连接失败的情况
     * @throws IOException          读取输入流的失败的情况
     */
    public void upload(InputStream in, String path, String fileName, int chunkSize) throws InterruptedException, IOException {
        //进行连接
        connect();
        //创建文件分割器，用于分割文件，split为核心方法
        FileSplitter splitter = new DefaultLazyLoadFileSplitter(in, chunkSize);

        //设置状态
        running = true;
        //遍历每一个字节数组
        while (splitter.hasNext()){

            //获取到当前字节数组
            byte[] bytes = splitter.next();
            //进行压缩
            byte[] target = GzipUtils.zip(bytes);
            //通过工具类GFTPHelper创建创建MessageData对象
            MessageData<byte[]> byteData = GFTPHelper.createByteArrayMessageData("1.0", false, target);
            if (splitter.isFinished()) {
                byteData.setFinished(true);
            }
            byteData.setUpload(true);
            byteData.setPath(path);
            byteData.setFileName(fileName);
            byteData.setKeepAlive(true);
            byteData.setUser(user);

            /**
             * 如果当前不是连接状态，或者读取数据的handler被关闭的连接导致running为false
             * 则跳出循环
             */
            if (!future.channel().isActive() || !running) {
                break;
            }
            //sync方法保证数据正常的写出去
            future.channel().writeAndFlush(byteData);
        }

    }

    /**
     * 阻塞式上传的核心方法
     *
     * @param in        输入流
     * @param path      服务器目录
     * @param fileName  上传到服务器的文件名
     * @param chunkSize 每一次装箱(byte[])的大小
     * @return 服务器返回的标志性字符串
     * @throws InterruptedException    连接失败的情况
     * @throws IOException             读取输入流的失败的情况
     * @throws AuthenticationException 服务器没有返回数据抛出异常
     */
    public String uploadSync(InputStream in, String path, String fileName, int chunkSize) throws InterruptedException, AuthenticationException, IOException {
        /**
         * 做上传逻辑
         */
        upload(in, path, fileName, chunkSize);
        /**
         * 阻塞式等待信息
         */
        CountDownLatch latch=new CountDownLatch(1);
        handler.setLatch(latch);
        latch.await();
        /**
         * 获取信息
         */
        String message = handler.getMessage();
        if (message != null) {
            return message;
        }
        /**
         * 如果没有返回信息则说明异常了
         */
        throw new AuthenticationException("当前用户没有写权限，无法上传");
    }

    /**
     * 重载方法
     *
     * @param pathFile  客户端文件路径
     * @param path      服务器目录
     * @param fileName  上传到服务器的文件名
     * @param chunkSize 方块大小
     * @throws Exception 运行时可能跑出的异常
     */
    public void upload(String pathFile, String path, String fileName, int chunkSize) throws Exception {
        upload(new FileInputStream(pathFile), path, fileName, chunkSize);
    }

    public String uploadSync(String pathFile, String path, String fileName, int chunkSize) throws Exception {
        return uploadSync(new FileInputStream(pathFile), path, fileName, chunkSize);
    }

    public void upload(InputStream in, String path, String fileName) throws InterruptedException, IOException, AuthenticationException {
        upload(in, path, fileName, DownloadFileChannelHandler.CHUCK_SIZE);
    }

    public String uploadSync(InputStream in, String path, String fileName) throws InterruptedException, IOException, AuthenticationException {
        return uploadSync(in, path, fileName, DownloadFileChannelHandler.CHUCK_SIZE);
    }

    public void upload(String srcFile, String path, String fileName) throws Exception {
        upload(srcFile, path, fileName, DownloadFileChannelHandler.CHUCK_SIZE);
    }

    public String uploadSync(String srcFile, String path, String fileName) throws Exception {
        return uploadSync(srcFile, path, fileName, DownloadFileChannelHandler.CHUCK_SIZE);
    }


    /**
     * 通过一个服务器目录和文件名还有本地的文件路径进行下载
     *
     * @param path     服务器目录，其实也可以直接是服务器目录+服务器文件名，服务器中有自己的处理
     * @param fileName 服务器文件名
     * @param destPath 本地文件路径
     * @throws InterruptedException 可能失败，断开连接等情况
     */
    public void download(String path, String fileName, String destPath) throws InterruptedException {
        /**
         * 设置下载路径
         */
        handler.setFilePath(destPath);
        /**
         * 进行下载逻辑的核心方法
         */
        download0(path, fileName);

    }

    /**
     * 阻塞式的下载文件
     * 通过一个服务器目录和文件名还有本地的文件路径进行下载
     *
     * @param path     服务器目录，其实也可以直接是服务器目录+服务器文件名，服务器中有自己的处理
     * @param fileName 服务器文件名
     * @param destPath 本地文件路径
     * @throws InterruptedException    可能失败，断开连接等情况
     * @throws AuthenticationException 权限不够或者认证失败的情况
     */
    public String downloadSync(String path, String fileName, String destPath) throws InterruptedException, AuthenticationException {
        download(path, fileName, destPath);
        running = true;
        CountDownLatch latch = new CountDownLatch(1);
        handler.setLatch(latch);
        latch.await();
        String message = handler.getMessage();
        if (message != null) {
            return message;
        }
        throw new AuthenticationException("下载失败,可能是权限不够");
    }

    /**
     * 这是一个阻塞式方法,将服务器文件写入到字节数组流中
     *
     * @param path
     * @param fileName
     * @return
     * @throws InterruptedException
     * @throws AuthenticationException
     */
    public ByteArrayOutputStream download(String path, String fileName) throws InterruptedException, AuthenticationException {
        /**
         * 设置下载路径为空
         */
        handler.setFilePath(null);
        /**
         * 开始下载逻辑
         */
        download0(path, fileName);
        CountDownLatch latch = new CountDownLatch(1);
        handler.setLatch(latch);
        latch.await();
        /**
         * 获取到输出流
         */
        ByteArrayOutputStream out = handler.getByteOut();
        if (out != null) {
            return out;
        }
        /**
         * 抛出异常
         */
        throw new AuthenticationException("没有权限进行下载,或者是服务器中没有该文件");
    }

    /**
     * 进行下载操作的核心方法
     *
     * @param path
     * @param fileName
     * @throws InterruptedException
     */
    private void download0(String path, String fileName) throws InterruptedException {
        /*
         * 连接到服务器
         */
        connect();
        /**
         * 设置下载的一些参数
         */
        MessageData<byte[]> data = GFTPHelper.createByteArrayMessageData("1.0", true, null);
        data.setPath(path);
        data.setFileName(fileName);
        data.setDownload(true);
        data.setUser(user);
        future.channel().writeAndFlush(data);
    }

    /**
     * 读取服务器每个目录下的所有文件
     *
     * @param path 服务器目录
     * @return 服务器返回的数据集合
     * @throws InterruptedException    可能出现连接不上服务器的情况
     * @throws AuthenticationException 认证失败信息，权限不够等信息
     */
    public List<FileDocumentation> readPathFile(String path) throws InterruptedException, AuthenticationException {
        connect();
        MessageData<String> data = GFTPHelper.createMessageData("1.0", null);
        data.setPath(path);
        data.setUser(user);
        future.channel().writeAndFlush(data);
        CountDownLatch latch = new CountDownLatch(1);
        handler.setLatch(latch);
        latch.await();
        List<FileDocumentation> message = handler.getDocumentations();
        if (message != null) {
            return message;
        }
        throw new AuthenticationException("没有权限访问");
    }

    /**
     * 释放资源
     */
    public void close() {
        if (future != null) {
            future.channel().close();
        }
        release();
    }

    /*
     * 进行登录，实际就是创建GFTPUser，并设置为当前客户端连接的验证信息
     */
    public void login(String username, String password) {
        user = GFTPHelper.createUser(username, password, null);
    }

    /**
     * 用于handler操作数据
     *
     * @param running
     */
    public void setRunning(boolean running) {
        this.running = running;
    }

    /**
     * 删除文件的核心方法，该方法是阻塞式的
     *
     * @param documentation
     * @return
     * @throws InterruptedException
     */
    public String deleteFile(FileDocumentation documentation) throws InterruptedException {
        //进行连接
        connect();

        //设置数据，并且数据必须是一个FileDocumentation,根据其currentParent和fileName进行文件删除
        MessageData<FileDocumentation> messageData = GFTPHelper.createMessageData("1.0");
        messageData.setData(documentation);
        messageData.setFinished(true);
        messageData.setDelete(true);

        messageData.setUser(user);

        /**
         * 写入数据给服务器
         */
        future.channel().writeAndFlush(messageData);
        CountDownLatch latch = new CountDownLatch(1);
        handler.setLatch(latch);
        latch.await();
        /**
         * 直接将数据返回
         */
        String message = handler.getMessage();
        return message;
    }

    /**
     * 创建文件夹
     *
     * @param path       服务器目录
     * @param folderName 创建的目录名
     * @return 服务器返回标志性数据 true false 字符串
     * @throws InterruptedException
     */
    public String createFolder(String path, String folderName) throws InterruptedException {
        /**
         * 创建文件夹的所需要的FileDocumentation对象
         */

        FileDocumentation documentation = new FileDocumentation();
        documentation.setCurrentParent(path);
        documentation.setFileName(folderName);
        return createFolder(documentation);
    }

    /**
     * 创建文件夹的核心方法
     *
     * @param documentation
     * @return
     * @throws InterruptedException
     */
    public String createFolder(FileDocumentation documentation) throws InterruptedException {
        connect();
        MessageData messageData = GFTPHelper.createMessageData("1.0");
        messageData.setData(documentation);
        messageData.setFinished(true);
        messageData.setCreateFolder(true);
        messageData.setUser(user);

        future.channel().writeAndFlush(messageData);
        CountDownLatch latch = new CountDownLatch(1);
        handler.setLatch(latch);
        latch.await();
        String message = handler.getMessage();
        return message;
    }
}
