package com.study.gftp.server;

import com.study.gftp.GFTPUser;

import java.util.List;

/**
 * 服务器的配置对象
 */
public class GFTPServerConfiguration {
    private int maxObjectSize;
    private String rootPath;
    private List<Integer> ports;
    private List<GFTPUser> users;

    public int getMaxObjectSize() {
        return maxObjectSize;
    }

    public void setMaxObjectSize(int maxObjectSize) {
        this.maxObjectSize = maxObjectSize;
    }

    public String getRootPath() {
        return rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public List<Integer> getPorts() {
        return ports;
    }

    public void setPorts(List<Integer> ports) {
        this.ports = ports;
    }

    public List<GFTPUser> getUsers() {
        return users;
    }

    public void setUsers(List<GFTPUser> users) {
        this.users = users;
    }

    @Override
    public String toString() {
        return "GFTPServerConfiguration{" +
                "maxObjectSize=" + maxObjectSize +
                ", rootPath='" + rootPath + '\'' +
                ", ports=" + ports +
                ", users=" + users +
                '}';
    }
}
