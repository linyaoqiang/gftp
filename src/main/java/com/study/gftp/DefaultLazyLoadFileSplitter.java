package com.study.gftp;

import java.io.IOException;
import java.io.InputStream;

public class DefaultLazyLoadFileSplitter extends AbstractLazyLoadFileSplitter {

    public DefaultLazyLoadFileSplitter(InputStream in, int chunkSize) throws IOException {
        super(in, chunkSize);
    }

    public DefaultLazyLoadFileSplitter(String fileName, int chunkSize) throws IOException {
        super(fileName, chunkSize);
    }

    public DefaultLazyLoadFileSplitter(InputStream in) throws IOException {
        super(in);
    }

    public DefaultLazyLoadFileSplitter(String fileName) throws IOException {
        super(fileName);
    }
}
