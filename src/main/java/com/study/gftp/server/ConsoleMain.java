package com.study.gftp.server;

import com.study.gftp.GFTPUser;
import com.study.gftp.exception.ParseUserException;
import com.study.gftp.helper.GFTPHelper;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 服务端程序控制台程序的方式启动
 */
public class ConsoleMain {
    private static Logger logger;

    public static void main(String[] args) {
        loadLog4jConf(args);
        logger = Logger.getLogger(ConsoleMain.class);
        try {
            start(args);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            System.exit(0);
        }
    }

    /**
     * 配置log4j
     *
     * @param args
     */
    public static void loadLog4jConf(String[] args) {
        if (args != null && args.length == 2 && args[1] != null && new File(args[1]).exists()) {
            PropertyConfigurator.configure(args[1]);
        } else if (new File("conf/server-log.properties").exists()) {
            PropertyConfigurator.configure("conf/server-log.properties");
        }
    }

    /**
     * 开启服务器
     *
     * @param args
     * @throws Exception
     */
    public static void start(String[] args) throws Exception {
        Properties properties;
        File defaultConf = new File("conf/gftp-server.properties");
        if (args != null && args.length > 0) {
            properties = loadFromArgs(args);
        } else if (defaultConf.exists()) {
            properties = new Properties();
            properties.load(new FileInputStream(defaultConf));
        } else {
            properties = GFTPHelper.createPropertiesFromClassPath("gftp-server.properties");
        }

        String size = properties.getProperty("maxObjectSize");
        int maxObjectSize = size == null ? 0 : Integer.parseInt(size);
        String rooPath = properties.getProperty("rootPath");
        String[] portStrings = properties.getProperty("ports").split(",");
        List<Integer> ports = new ArrayList<>();
        for (String port : portStrings) {
            ports.add(Integer.parseInt(port));
        }

        GFTPServerConfiguration configuration = GFTPHelper.createConfiguration(maxObjectSize, rooPath, ports);
        initUserFromProperties(properties, configuration);
        if (configuration.getRootPath() == null) {
            logger.error("rootPath没有设置");
            return;
        }
        File file = new File(configuration.getRootPath());
        if (!file.exists() || !file.isDirectory()) {
            logger.error("当前选中的不是一个文件夹或者是不存在");
            return;
        }
        logger.info("解析服务器配置文件 :" + properties);
        logger.info("解析结果为 :" + configuration);
        new GFTPServer().init(configuration);
    }

    /**
     * 从args参数中解析
     *
     * @param args
     * @return
     * @throws IOException
     */
    public static Properties loadFromArgs(String[] args) throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(args[0]));
        return properties;
    }

    /**
     * 解析配置文件
     *
     * @param properties
     * @param configuration
     * @throws ParseUserException
     */
    public static void initUserFromProperties(Properties properties, GFTPServerConfiguration configuration) throws ParseUserException {
        String users = properties.getProperty("users");
        if (users == null) {
            return;
        }
        List<GFTPUser> gftpUsers = new ArrayList<>();
        String[] userTargets = users.split(",");
        for (String username : userTargets) {
            String userInfo = properties.getProperty(username);
            if (userInfo == null) {
                continue;
            }
            String[] infos = userInfo.split(",");
            if (infos.length != 2) {
                throw new ParseUserException("从文件<=>" + properties + "<=>中解析用户:" + username + ",其必须声明密码和读写权限,例如: jack=123,rw");
            }
            GFTPUser user = GFTPHelper.createUser(username, infos[0], infos[1]);
            logger.info("从配置文件中解析到用户 :" + user);
            gftpUsers.add(user);
        }
        configuration.setUsers(gftpUsers);
    }
}
