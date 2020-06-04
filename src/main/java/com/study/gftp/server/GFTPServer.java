package com.study.gftp.server;

import com.study.gftp.exception.InitServerPortException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * GFtp服务器
 */
public class GFTPServer {
    private static Logger logger = Logger.getLogger(GFTPServer.class);
    /**
     * 创建服务器所需要线程组和资源
     */
    public static final String DEFAULT_DIR = System.getProperty("user.dir");
    private EventLoopGroup acceptGroup = new NioEventLoopGroup(4);
    private EventLoopGroup workerGroup = new NioEventLoopGroup(10);
    private ServerBootstrap bootstrap = new ServerBootstrap();
    public static final int DEFAULT_MAX_OBJECT_SIZE = 1024 * 1024 * 50;
    private ChannelFuture future;

    @SuppressWarnings("all")
    public void init(GFTPServerConfiguration configuration) throws Exception {
        if (configuration.getPorts() == null) {
            throw new InitServerPortException("没有可用的监听端口");
        }
        bootstrap.group(acceptGroup, workerGroup);
        bootstrap.channel(NioServerSocketChannel.class);
        bootstrap.option(ChannelOption.SO_BACKLOG, 128);//配置TCP参数,这里是设置缓冲区

        String rootPath = configuration.getRootPath();
        rootPath = rootPath == null ? DEFAULT_DIR : rootPath;
        /**
         * 初始化处理器
         */
        initHandler(configuration);
        /**
         * 初始化服务器监听端口，并开启服务器
         */
        initPorts(configuration.getPorts());
        /**
         * 打印服务器启动信息
         */
        printServerInfo(configuration.getPorts(), rootPath);
    }

    private void initHandler(GFTPServerConfiguration configuration) {
        bootstrap.childHandler(new GFTPServerChannelInitializer(configuration));
    }

    private void initPorts(List<Integer> ports) throws InterruptedException {
        for (Integer port : ports) {
            bootstrap.bind(port).sync();
        }
    }
    private void printServerInfo(List<Integer> ports, String path) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("server started in port");
        for (Integer port : ports) {
            stringBuffer.append(" " + port);
        }
        stringBuffer.append("<=>");
        stringBuffer.append("the root path :" + path);
        logger.info(stringBuffer.toString());
    }

    public void release() {
        acceptGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
