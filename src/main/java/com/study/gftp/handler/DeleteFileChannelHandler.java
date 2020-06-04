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

@ChannelHandler.Sharable
public class DeleteFileChannelHandler extends ChannelInboundHandlerAdapter {
    private String rootPath;
    private static Logger logger = Logger.getLogger(DeleteFileChannelHandler.class);
    public DeleteFileChannelHandler(String rootPath) {
        this.rootPath = rootPath;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageData messageData =(MessageData)msg;
        if (!messageData.isDelete() || messageData.getData() == null || !(messageData.getData() instanceof FileDocumentation)) {
            ctx.fireChannelRead(msg);
            return;
        }
        boolean ok = false;
        if (messageData.getUser() == null) {
            ok = doDelete(ctx, messageData);
        } else if (messageData.getUser().isCanWrite()) {
            ok = doDelete(ctx, messageData);
        } else {
            throw new AuthenticationException("删除文件或者文件夹失败,用户权限不够 :" + messageData.getUser());
        }
        String status = ok?"成功":"失败";
        logger.info("用户 :"+messageData.getUser()+"删除文件 :" +status);
        messageData.setData("" + ok);
        ctx.channel().writeAndFlush(messageData).sync();
    }

    public boolean doDelete(ChannelHandlerContext ctx, MessageData messageData) {
        FileDocumentation documentation = (FileDocumentation) messageData.getData();
        String path = GFTPHelper.getPath(rootPath, documentation.getCurrentParent(), documentation.getFileName());
        File file = new File(path);
        boolean ok = false;
        if (file.exists()) {
            if (file.isFile()) {
                ok = deleteFile(file);
            }else{
                ok = deleteDirectory(file);
            }
        }
        return ok;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(),cause);
        cause.printStackTrace();
        ctx.close();
    }

    public boolean deleteDirectory(File file) {
        // 如果dir不以文件分隔符结尾，自动添加文件分隔符
        boolean flag = true;
        // 删除文件夹中的所有文件包括子目录
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            // 删除子文件
            if (files[i].isFile()) {
                flag = deleteFile(files[i]);
                if (!flag)
                    break;
            }
            // 删除子目录
            else if (files[i].isDirectory()) {
                flag = deleteDirectory(files[i]);
                if (!flag)
                    break;
            }
        }
        if (!flag) {
            return false;
        }
        // 删除当前目录
        return file.delete();
    }

    public boolean deleteFile(File file) {
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        return file.delete();
    }

}
