package com.study.gftp.handler;


import com.study.gftp.FileDocumentation;
import com.study.gftp.MessageData;
import com.study.gftp.helper.GFTPHelper;
import com.study.gftp.exception.AuthenticationException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@ChannelHandler.Sharable
public class ReadPathFileChannelHandler extends ChannelInboundHandlerAdapter {
    public String rootPath;
    private Logger logger = Logger.getLogger(ReadPathFileChannelHandler.class);
    public ReadPathFileChannelHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageData messageData = (MessageData) msg;
        if (messageData.getUser() == null) {
            doReadPathFile(ctx, messageData);
        } else if (messageData.getUser().isCanRead()) {
            doReadPathFile(ctx, messageData);
        } else {
            throw new AuthenticationException("认证未通过,没有读取的权限 :" + messageData.getUser());
        }
    }

    public void doReadPathFile(ChannelHandlerContext ctx, MessageData messageData) throws InterruptedException {
        String path = GFTPHelper.getPath(rootPath, messageData.getPath(), null);
        File file = new File(path);
        if (!file.exists()||!file.isDirectory()) {
            MessageData data = new MessageData();
            data.setData(new ArrayList<FileDocumentation>());
            ctx.channel().writeAndFlush(data).sync();
            return;
        }
        File[] fileArray = file.listFiles();
        List<FileDocumentation> fileDocumentationList = new ArrayList<>();
        for (File f : fileArray) {
            if (f.isHidden() || !f.exists()) {
                continue;
            }
            FileDocumentation documentation = new FileDocumentation();
            if (f.isFile()) {
                documentation.setFile(true);
            } else {
                documentation.setDirectory(true);
            }

            String p = messageData.getPath();
            p = p.endsWith(File.separator) ? p : p + File.separator;
            documentation.setCurrentParent(p);
            documentation.setFileName(f.getName());
            documentation.setLength(f.length());
            fileDocumentationList.add(documentation);
        }
        MessageData<List<FileDocumentation>> data = GFTPHelper.createMessageData("1.0");
        data.setData(fileDocumentationList);
        ctx.channel().writeAndFlush(data);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(),cause);
        cause.printStackTrace();
        ctx.close();
    }
}
