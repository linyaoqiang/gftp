package com.study.gftp.handler;

import com.study.gftp.*;
import com.study.gftp.helper.GFTPHelper;
import com.study.gftp.exception.AuthenticationException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.File;


@ChannelHandler.Sharable
public class DownloadFileChannelHandler extends ChannelInboundHandlerAdapter {
    private String rootPath;
    public static int CHUCK_SIZE = 204800;
    private static Logger logger = Logger.getLogger(DownloadFileChannelHandler.class);

    public DownloadFileChannelHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageData messageData = (MessageData) msg;
        if (!messageData.isDownload()) {
            ctx.fireChannelRead(msg);
            return;
        }
        if (messageData.getUser() == null) {
            doDownload(ctx,messageData);
        }else if(messageData.getUser().isCanRead()){
            doDownload(ctx,messageData);
        }else{
            throw  new AuthenticationException("认证未通过,没有读取下载的权限 :"+messageData.getUser());
        }

    }

    public void doDownload(ChannelHandlerContext ctx, MessageData messageData) throws Exception {
        String path = GFTPHelper.getPath(rootPath, messageData.getPath(), messageData.getFileName());
        File file = new File(path);

        if(!file.exists()||!file.isFile()){
            messageData.setData("error not a file");
            ctx.channel().writeAndFlush(messageData);
            return;
        }


        FileSplitter splitter = new DefaultLazyLoadFileSplitter(file.getAbsolutePath(),CHUCK_SIZE);

        while (splitter.hasNext()){
            byte[] data = splitter.next();
            byte[] target = GzipUtils.zip(data);
            MessageData sendData = GFTPHelper.createByteArrayMessageData("1.0", false, target);
            sendData.setPath(path);
            if (splitter.isFinished()) {
                sendData.setFinished(true);
                logger.info(messageData.getUser()+"下载了 :"+file.getAbsolutePath());
            }
            //sync同步写入 防止数据出错

            ctx.channel().writeAndFlush(sendData);
        }

        MessageData<String> stringData = GFTPHelper.createMessageData("1.0","finished");
        ctx.channel().writeAndFlush(stringData);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(),cause);
        cause.printStackTrace();
        ctx.close();
    }
}
