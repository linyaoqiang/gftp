package com.study.gftp;

import java.io.*;
import java.util.List;

public abstract class AbstractLazyLoadFileSplitter implements FileSplitter {
    private BufferedInputStream in;
    private long total;
    private long readTotal;
    private int chunkSize;
    public static final int DEFAULT_CHUNK_SIZE = 8192;

    public AbstractLazyLoadFileSplitter(InputStream in, int chunkSize) throws IOException {
        this.total = in.available();
        this.readTotal = 0;
        this.in = new BufferedInputStream(in);
        this.chunkSize = chunkSize;
    }

    public AbstractLazyLoadFileSplitter(String fileName, int chunkSize) throws IOException {
        this(new FileInputStream(fileName), chunkSize);
    }

    public AbstractLazyLoadFileSplitter(InputStream in) throws IOException {
        this(in, DEFAULT_CHUNK_SIZE);
    }

    @Override
    public void split() throws IOException {

    }

    public AbstractLazyLoadFileSplitter(String fileName) throws IOException {
        this(fileName, DEFAULT_CHUNK_SIZE);
    }

    private byte[] readBytes() throws IOException {
        if (total - readTotal <= 0) {
            return null;
        }
        int flush = 0;
        if (total - readTotal >= chunkSize) {
            flush = chunkSize;
        } else {
            flush = (int) (total - readTotal);
        }
        byte[] data = new byte[flush];
        readTotal += flush;
        in.read(data);

        if (readTotal >= total) {
            in.close();
            in = null;
        }
        return data;
    }


    @Override
    public boolean hasNext() {
        return readTotal < total;
    }

    @Override
    public byte[] next() throws IOException {
        return readBytes();
    }

    @Override
    public List<byte[]> getData() {
        return null;
    }

    @Override
    public boolean isFinished() {
        return total <= readTotal;
    }
}
