package com.study.gftp.server;


import com.study.gftp.GFTPUser;
import com.study.gftp.handler.*;
import com.study.gftp.helper.MarshallingFactoryHelper;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class GFTPServerChannelInitializer extends ChannelInitializer {
    private String rootPath;
    private int maxObjectSize;
    private List<GFTPUser> users;
    private GFTPFileServerChannelHandler gftpFileServerChannelHandler;
    private UploadFileChannelHandler uploadFileChannelHandler;
    private DownloadFileChannelHandler downloadFileChannelHandler;
    private CreateFolderChannelHandler createFolderChannelHandler;
    private DeleteFileChannelHandler deleteFileChannelHandler;
    private ReadPathFileChannelHandler readPathFileChannelHandler;

    public GFTPServerChannelInitializer(GFTPServerConfiguration configuration) {
        this.rootPath = configuration.getRootPath();
        this.maxObjectSize = configuration.getMaxObjectSize();
        this.users = configuration.getUsers();
        initChannelHandlers();
    }

    public void initChannelHandlers() {
        gftpFileServerChannelHandler = new GFTPFileServerChannelHandler(users);
        uploadFileChannelHandler = new UploadFileChannelHandler(rootPath);
        downloadFileChannelHandler = new DownloadFileChannelHandler(rootPath);
        deleteFileChannelHandler = new DeleteFileChannelHandler(rootPath);
        createFolderChannelHandler = new CreateFolderChannelHandler(rootPath);
        readPathFileChannelHandler = new ReadPathFileChannelHandler(rootPath);
    }


    @Override
    protected void initChannel(Channel ch) throws Exception {
        //120秒没有消息发过来，则表示端口连接
        ch.pipeline().addLast(new ReadTimeoutHandler(120, TimeUnit.SECONDS));

        //用于序列化
        if (maxObjectSize > 0) {
            ch.pipeline().addLast(MarshallingFactoryHelper.buildMarshallingDecoder(maxObjectSize));
        } else {
            ch.pipeline().addLast(MarshallingFactoryHelper.buildMarshallingDecoder());
        }
        ch.pipeline().addLast(MarshallingFactoryHelper.buildMarshallingEncoder());
        //自定义处理器
       ch.pipeline().addLast(gftpFileServerChannelHandler);
       ch.pipeline().addLast(uploadFileChannelHandler);
       ch.pipeline().addLast(downloadFileChannelHandler);
       ch.pipeline().addLast(deleteFileChannelHandler);
       ch.pipeline().addLast(createFolderChannelHandler);
       ch.pipeline().addLast(readPathFileChannelHandler);
    }
}
