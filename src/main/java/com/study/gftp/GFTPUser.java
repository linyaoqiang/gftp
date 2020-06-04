package com.study.gftp;

import java.io.Serializable;

public class GFTPUser implements Serializable {
    private String username;
    private String password;
    private boolean canRead;
    private boolean canWrite;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isCanRead() {
        return canRead;
    }

    public void setCanRead(boolean canRead) {
        this.canRead = canRead;
    }

    public boolean isCanWrite() {
        return canWrite;
    }

    public void setCanWrite(boolean canWrite) {
        this.canWrite = canWrite;
    }

    @Override
    public String toString() {
        return "GFTPUser{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", canRead=" + canRead +
                ", canWrite=" + canWrite +
                '}';
    }
}
