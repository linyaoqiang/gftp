package com.study.gftp.handler;

import com.study.gftp.FileDocumentation;
import com.study.gftp.MessageData;
import com.study.gftp.exception.AuthenticationException;
import com.study.gftp.helper.GFTPHelper;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;


@ChannelHandler.Sharable
public class CreateFolderChannelHandler extends ChannelInboundHandlerAdapter {
    private String rootPath;
    private static Logger logger = Logger.getLogger(CreateFolderChannelHandler.class);

    public CreateFolderChannelHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageData messageData = (MessageData) msg;
        if (!messageData.isCreateFolder() || messageData.getData() == null || !(messageData.getData() instanceof FileDocumentation)) {
            ctx.fireChannelRead(msg);
            return;
        }
        boolean ok = false;
        if (messageData.getUser() == null) {
            ok = doCreateFolder(ctx, messageData);
        } else if (messageData.getUser().isCanWrite()) {
            ok = doCreateFolder(ctx, messageData);
        } else {
            throw new AuthenticationException("没有权限创建目录 :" + messageData.getUser());
        }
        String status = ok ? "成功" : "失败";
        logger.info("用户 :" + messageData.getUser() + " 创建目录:" + messageData.getData() + status);
        messageData.setData("" + ok);
        ctx.channel().writeAndFlush(messageData).sync();


    }

    private boolean doCreateFolder(ChannelHandlerContext ctx, MessageData<FileDocumentation> messageData) {
        FileDocumentation documentation = messageData.getData();
        String path = GFTPHelper.getPath(rootPath, documentation.getCurrentParent(), documentation.getFileName());
        System.out.println(path);
        boolean ok = new File(path).mkdirs();
        return ok;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        cause.printStackTrace();
        ctx.close();
    }
}
