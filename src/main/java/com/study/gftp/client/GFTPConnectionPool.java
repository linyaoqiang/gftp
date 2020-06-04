package com.study.gftp.client;

import java.util.ArrayList;
import java.util.List;

public class GFTPConnectionPool {
    private List<GFTPClient> clients = new ArrayList<>();
    private ThreadLocal<GFTPClient> clientThreadLocal = new ThreadLocal<>();
    private int idleSize;
    private int activeSize;
    public static int MIN_IDLE;
    public static int MAX_IDLE;
    public static int MAX_ACTIVE;
    private String ip;
    private int port;
    private boolean needToLogin;
    private String username;
    private String password;

    public GFTPConnectionPool(int minIdle, int maxIdle, int maxActive, String ip, int port) {

        MIN_IDLE = minIdle;
        MAX_IDLE = maxIdle;
        MAX_ACTIVE = maxActive;
        this.ip = ip;
        this.port = port;
    }

    public synchronized void init() throws InterruptedException {
        while (idleSize < MIN_IDLE) {
            GFTPClient client = new GFTPClient();
            client.init(ip, port);
            clients.add(client);
            if (needToLogin) {
                client.login(username, password);
            }
            idleSize++;
        }
    }

    public synchronized GFTPClient getGFTPClient() throws InterruptedException {
        if (activeSize >= MAX_ACTIVE) {
            wait();
        }
        if (clients.size() == 0) {
            init();
        }
        GFTPClient client = clients.remove(0);
        idleSize--;
        activeSize++;
        return client;
    }

    public synchronized GFTPClient getCurrentClient() throws InterruptedException {
        GFTPClient client = clientThreadLocal.get();
        if (client == null) {
            client = getGFTPClient();
            clientThreadLocal.set(client);
        }
        return client;
    }

    public synchronized void releaseClient(GFTPClient client) {
        if (clients.size() >= MAX_IDLE) {
            client.close();
        } else {
            clients.add(client);
            idleSize++;
        }
        notify();
        activeSize--;
    }

    public void setNeedToLogin(boolean needToLogin) {
        this.needToLogin = needToLogin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
