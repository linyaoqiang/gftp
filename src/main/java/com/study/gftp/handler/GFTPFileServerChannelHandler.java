package com.study.gftp.handler;

import com.study.gftp.GFTPUser;
import com.study.gftp.MessageData;
import com.study.gftp.server.GFTPServer;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.log4j.Logger;

import java.util.List;

@ChannelHandler.Sharable
public class GFTPFileServerChannelHandler extends ChannelInboundHandlerAdapter {
    private boolean useAuth;
    private List<GFTPUser> users;
    private static Logger logger = Logger.getLogger(GFTPFileServerChannelHandler.class);

    public GFTPFileServerChannelHandler(List<GFTPUser> users) {
        this.users = users;
        if (users != null && users.size() > 0) {
            useAuth = true;
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof MessageData && checkLogin((MessageData) msg)) {
            MessageData data = (MessageData) msg;
            ctx.fireChannelRead(msg);
            if (!data.isKeepAlive() || data.isFinished()) {
                ctx.close();
            }
        } else {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error(cause.getMessage(), cause);
        cause.printStackTrace();
        ctx.close();
    }

    public boolean checkLogin(MessageData messageData) {
        if (!useAuth) {
            if(messageData.getUser()!=null){
                messageData.setUser(null);
            }
            return true;
        }

        if(messageData.getUser()==null){
            return false;
        }
        return login(messageData);
    }

    public boolean login(MessageData messageData) {
        GFTPUser user = messageData.getUser();
        if (user == null)
            return false;

        for (GFTPUser auth : users) {
            if (auth.getUsername().equals(user.getUsername()) && auth.getPassword().equals(user.getPassword())) {
                user.setCanRead(auth.isCanRead());
                user.setCanWrite(auth.isCanWrite());
                return true;
            }
        }
        return false;
    }
}
