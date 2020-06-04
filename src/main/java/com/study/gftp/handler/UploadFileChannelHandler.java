package com.study.gftp.handler;

import com.study.gftp.GzipUtils;
import com.study.gftp.MessageData;
import com.study.gftp.helper.GFTPHelper;
import com.study.gftp.exception.AuthenticationException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ChannelHandler.Sharable
public class UploadFileChannelHandler extends ChannelInboundHandlerAdapter {
    private String rootPath;
    private static Logger logger = Logger.getLogger(UploadFileChannelHandler.class);
    public UploadFileChannelHandler(String rootPath) {
        this.rootPath = rootPath;
    }


    private Map<String,BufferedOutputStream> uploads = new HashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        MessageData messageData = (MessageData) msg;
        if (messageData.getData() == null || !(messageData.getData() instanceof byte[]) || !messageData.isUpload()) {
            ctx.fireChannelRead(msg);
            return;
        }

        if (messageData.getUser() == null) {
            doUpload(ctx,messageData);
        } else if(messageData.getUser().isCanWrite()){
            doUpload(ctx,messageData);
        }else{
            throw new AuthenticationException("认证不通过，因为没有写入权限，上传失败:"+messageData.getUser());
        }
    }

    public void doUpload(ChannelHandlerContext ctx,MessageData messageData) throws IOException, AuthenticationException {
        byte[] bytes = (byte[]) messageData.getData();
        String path = GFTPHelper.getPath(rootPath, messageData.getPath(), messageData.getFileName());
        BufferedOutputStream out = uploads.get(path);
        if (out == null) {
            File file = new File(path);
            if(!file.getParentFile().exists()){
                boolean ok=file.getParentFile().mkdirs();
                if(!ok){
                    throw new AuthenticationException("无法创建文件夹 :"+file);
                }
            }
            out = new BufferedOutputStream(new FileOutputStream(path));
            uploads.put(path,out);
        }
        byte[] target = GzipUtils.unzip(bytes);
        out.write(target);
        out.flush();

        if (messageData.isFinished()) {
            out.close();
            uploads.remove(path);
            out = null;
            logger.info("用户上传文件成功:"+path);
            messageData.setData("ok");
            ctx.channel().writeAndFlush(messageData);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(),cause);
        cause.printStackTrace();
        ctx.close();
    }
}
