package com.study.gftp.handler;

import com.study.gftp.FileDocumentation;
import com.study.gftp.GzipUtils;
import com.study.gftp.MessageData;
import com.study.gftp.client.GFTPClient;
import com.study.gftp.exception.ReadByteArrayException;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@ChannelHandler.Sharable
public class ClientReadDataChannelHandler extends ChannelInboundHandlerAdapter {
    private MessageData messageData;
    private String filePath;
    private BufferedOutputStream out;
    private GFTPClient client;
    private ByteArrayOutputStream byteOut;
    private ByteArrayOutputStream temp;
    private CountDownLatch latch;

    public ClientReadDataChannelHandler(GFTPClient client) {
        this.client = client;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageData) {
            MessageData messageData = (MessageData) msg;
            if (messageData.getData() == null) {
                return;
            }
            if (messageData.getData() instanceof byte[]) {
                readByteArrayData(ctx, messageData);
                return;
            }
            readMessage(ctx, messageData);
        }

    }

    private synchronized void readByteArrayData(ChannelHandlerContext context, MessageData<byte[]> data) throws ReadByteArrayException, IOException {
        if (this.out == null) {
            if (filePath != null) {
                this.out = new BufferedOutputStream(new FileOutputStream(filePath));
            } else {
                this.temp = new ByteArrayOutputStream();
                this.out = new BufferedOutputStream(this.temp);
            }
        }
        byte[] target = GzipUtils.unzip(data.getData());
        out.write(target);
        out.flush();
        if (data.isFinished() && filePath != null) {
            out.close();
            out = null;
        } else if (data.isFinished()) {
            byteOut = temp;
            if (latch != null) {
                latch.countDown();
            }
        }
    }

    private void readMessage(ChannelHandlerContext context, MessageData message) {
        this.messageData = message;
        if (latch != null) {
            latch.countDown();
        }
    }

    public List<FileDocumentation> getDocumentations() {
        MessageData data = getMessageData();
        if (data != null) {
            return (List<FileDocumentation>) data.getData();
        }
        return null;
    }

    public MessageData getMessageData() {
        MessageData data = this.messageData;
        this.messageData = null;
        return data;
    }

    public String getMessage() {
        MessageData data = getMessageData();
        if (data != null) {
            return data.getData().toString();
        }
        return null;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        client.setRunning(false);
        TimeUnit.SECONDS.sleep(8);
        if(latch!=null){
            latch.countDown();
        }
        //System.out.println("disconnected from server:" + ctx.channel().remoteAddress());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //System.out.println("connected to server:" + ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        if (latch != null) {
            latch.countDown();
        }
    }

    public ByteArrayOutputStream getByteOut() {
        ByteArrayOutputStream out = byteOut;
        if (out != null) {
            this.byteOut = null;
            this.out = null;
            this.filePath = null;
            this.temp = null;
            this.messageData = null;
            return out;
        }
        return null;
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void setLatch(CountDownLatch latch) {
        this.latch = latch;
    }
}
