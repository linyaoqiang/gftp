package com.study.gftp.client;

import com.study.gftp.helper.GFTPHelper;

import java.io.IOException;
import java.util.Properties;

public class GFTPClientManager {
    public static GFTPConnectionPool pool;

    static {
        try {
            init();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }

    private static void init() throws IOException {
        Properties properties = GFTPHelper.createPropertiesFromClassPath("gftp-client.properties");
        int minIdle = Integer.parseInt(properties.getProperty("minIdle"));
        int maxIdle = Integer.parseInt(properties.getProperty("maxIdle"));
        int maxActive = Integer.parseInt(properties.getProperty("maxActive"));
        String ip = properties.getProperty("ip");
        int port = Integer.parseInt(properties.getProperty("port"));
        pool = new GFTPConnectionPool(minIdle, maxIdle, maxActive, ip, port);
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        if (username == null || password == null) {
            return;
        }
        pool.setNeedToLogin(true);
        pool.setPassword(password);
        pool.setUsername(username);
    }

    public static GFTPClient getClient() throws InterruptedException {
        return pool.getGFTPClient();
    }

    public static void releaseClient(GFTPClient client) {
        pool.releaseClient(client);
    }
}
