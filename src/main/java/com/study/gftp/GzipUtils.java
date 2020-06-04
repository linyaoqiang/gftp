package com.study.gftp;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {
    /**
     * 压缩指定字节数组
     *
     * @param data
     * @return
     * @throws IOException
     */
    public static byte[] unzip(byte[] data) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //获取Gzip输入流用于将压缩后的数据解压缩
        GZIPInputStream gzipIn = new GZIPInputStream(in);
        byte[] bytes = new byte[1024];
        int flush = -1;
        while ((flush = gzipIn.read(bytes)) != -1) {
            out.write(bytes, 0, flush);
            out.flush();
        }
        byte[] target = out.toByteArray();
        in.close();
        out.close();
        return target;
    }

    public static byte[] zip(byte[] data) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //GZIP输入流，用于压缩数据
        GZIPOutputStream gzipOut = new GZIPOutputStream(out);
        //写入到gZip中，其会进行压缩
        gzipOut.write(data);
        //完成压缩
        gzipOut.finish();
        //获取压缩后的字节数组
        byte[] target = out.toByteArray();
        out.close();
        return target;

    }
}
