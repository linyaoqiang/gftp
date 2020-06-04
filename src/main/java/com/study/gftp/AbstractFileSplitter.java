package com.study.gftp;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFileSplitter implements FileSplitter {
    private List<byte[]> dataList = new ArrayList<>();
    private InputStream in;
    private int chunkSize = 0;
    private long chunkCount = 0;
    private long totalLength = 0;

    public AbstractFileSplitter(String fileName, int chunkSize) throws Exception {
        this(new FileInputStream(fileName), chunkSize);
    }

    public AbstractFileSplitter(InputStream in, int chunkSize) throws IOException {
        init(in,chunkSize);
    }

    private void init(InputStream in,int chunkSize) throws IOException {
        this.in = in;
        this.chunkSize = chunkSize;
        long length = this.totalLength = in.available();
        this.chunkCount = length % chunkSize == 0 ? length / chunkSize : length / chunkSize + 1;
    }


    @Override

    public void split() throws IOException {
        for (int i = 0; i < chunkCount; i++) {
            byte[] bytes = null;
            if (i < chunkCount - 1) {
                bytes = new byte[chunkSize];
            } else {
                bytes = new byte[(int) (totalLength - (chunkCount - 1) * chunkSize)];
            }
            in.read(bytes);
            dataList.add(bytes);
        }
        in.close();
    }


    public List<byte[]> getData() {
        return dataList;
    }

    @Override
    public boolean hasNext() {
        return false;
    }

    @Override
    public byte[] next() {
        return new byte[0];
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
