package com.study.gftp;

import java.io.Serializable;
import java.net.InetAddress;

public class MessageData<T> implements Serializable {
    private String version;
    private String path;
    private String fileName;
    private boolean finished;
    private InetAddress me;
    private T data;
    private boolean keepAlive;
    private boolean upload;
    private boolean download;
    private boolean delete;
    private boolean createFolder;
    private GFTPUser user;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public InetAddress getMe() {
        return me;
    }

    public void setMe(InetAddress me) {
        this.me = me;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public boolean isDownload() {
        return download;
    }

    public void setDownload(boolean download) {
        this.download = download;
    }

    public GFTPUser getUser() {
        return user;
    }

    public void setUser(GFTPUser user) {
        this.user = user;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }

    public boolean isCreateFolder() {
        return createFolder;
    }

    public void setCreateFolder(boolean createFolder) {
        this.createFolder = createFolder;
    }

    @Override
    public String toString() {
        return "MessageData{" +
                "version='" + version + '\'' +
                ", path='" + path + '\'' +
                ", fileName='" + fileName + '\'' +
                ", finished=" + finished +
                ", me=" + me +
                ", data=" + data +
                ", keepAlive=" + keepAlive +
                ", upload=" + upload +
                ", download=" + download +
                ", delete=" + delete +
                ", createFolder=" + createFolder +
                ", user=" + user +
                '}';
    }
}
