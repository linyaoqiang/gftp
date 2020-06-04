package com.study.gftp;

import com.study.gftp.client.GFTPClient;
import com.study.gftp.client.GFTPClientManager;
import com.study.gftp.exception.AuthenticationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

public class GFTPClientTest {

   /* @Test
    public void upload() throws Exception {
        GFTPClient client = GFTPClientManager.getClient();
        long start = System.currentTimeMillis();
        client.upload("E://Desktop//c.flv", "/", "d.flv");
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        Assert.assertTrue(end - start < 10000);
    }

    @Test
    public void uploadSync() throws Exception {
        GFTPClient client = GFTPClientManager.getClient();
        long start = System.currentTimeMillis();
        client.uploadSync("C:\\Users\\Administrator\\Documents\\绘图12M08D.vsdx", "", "a.jar");
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        Assert.assertTrue(end - start < 14000);
    }

    @Test
    public void readPathFile() throws Exception {
        GFTPClient client = GFTPClientManager.getClient();
        long start = System.currentTimeMillis();
        List<FileDocumentation> documentations = client.readPathFile("/");
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        Assert.assertTrue(end - start < 3000);
        System.out.println(documentations);

        start = System.currentTimeMillis();
        documentations = client.readPathFile("/");
        end = System.currentTimeMillis();
        System.out.println(end - start);
        Assert.assertTrue(end - start < 3000);
        System.out.println(documentations);
    }

    @Test
    public void download() throws InterruptedException, AuthenticationException {
        GFTPClient client = GFTPClientManager.getClient();
        long start = System.currentTimeMillis();
        client.download("/", "d.flv", "E://Desktop//aaaa.flv");
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        Assert.assertTrue(end - start < 3000);
        Thread.sleep(20000);
    }

    @Test
    public void downloadSync() throws InterruptedException, AuthenticationException {
        GFTPClient client = GFTPClientManager.getClient();
        long start = System.currentTimeMillis();
        client.downloadSync("/", "d.flv", "E://Desktop//aaaa.flv");
        long end = System.currentTimeMillis();
        System.out.println(end - start);
        Assert.assertTrue(end - start < 15000);
        start = System.currentTimeMillis();
        client.downloadSync("/", "d.flv", "E://Desktop//bbbb.flv");
        end = System.currentTimeMillis();
        System.out.println(end - start);

    }*/
}
