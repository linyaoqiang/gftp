package com.study.gftp.helper;

import com.study.gftp.GFTPUser;
import com.study.gftp.MessageData;
import com.study.gftp.server.GFTPServerConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class GFTPHelper {
    /**
     * 获取服务器中资源的绝对路径
     *
     * @param rootPath
     * @param path
     * @param fileName
     * @return
     */
    public static String getPath(String rootPath, String path, String fileName) {
        path = path.replace("/", File.separator);
        path = path.endsWith("/") ? path : path + "/";
        return rootPath + path + (fileName == null ? "" : fileName);
    }

    /**
     * 创建一个数据为字节数组的MessageData
     *
     * @param version
     * @param finished
     * @param data
     * @return
     */
    public static MessageData<byte[]> createByteArrayMessageData(String version, boolean finished, byte[] data) {
        MessageData<byte[]> messageData = createMessageData(version);
        messageData.setFinished(finished);
        messageData.setData(data);
        return messageData;
    }

    public static MessageData<String> createMessageData(String version, String message) {
        MessageData<String> messageData = createMessageData(version);
        messageData.setData(message);
        return messageData;
    }

    public static MessageData createMessageData(String version) {
        MessageData messageData = new MessageData();
        messageData.setVersion(version);
        return messageData;
    }

    /**
     * 从当前类路径中获取到输入流
     *
     * @param fileName
     * @return
     */
    public static InputStream createInputStreamFromClassPath(String fileName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
    }

    /**
     * 从类路径中获取到Properties对象，加载指定资源
     *
     * @param fileNames
     * @return
     * @throws IOException
     */
    public static Properties createPropertiesFromClassPath(String... fileNames) throws IOException {
        Properties properties = new Properties();
        for (String fileName : fileNames) {
            properties.load(createInputStreamFromClassPath(fileName));
        }
        return properties;
    }

    /**
     * 创建一个配置对象
     *
     * @param maxObjectSize
     * @param rootPath
     * @param ports
     * @return
     */
    public static GFTPServerConfiguration createConfiguration(int maxObjectSize, String rootPath, List<Integer> ports) {
        GFTPServerConfiguration configuration = new GFTPServerConfiguration();
        configuration.setMaxObjectSize(maxObjectSize);
        configuration.setRootPath(rootPath);
        configuration.setPorts(ports);
        return configuration;
    }

    /**
     * 创建一个user
     *
     * @param username
     * @param password
     * @param power
     * @return
     */
    public static GFTPUser createUser(String username, String password, String power) {
        GFTPUser user = new GFTPUser();
        user.setUsername(username);
        user.setPassword(password);
        if (power != null) {
            if (power.contains("r")) {
                user.setCanRead(true);
            }
            if (power.contains("w")) {
                user.setCanWrite(true);
            }
        }

        return user;
    }
}
