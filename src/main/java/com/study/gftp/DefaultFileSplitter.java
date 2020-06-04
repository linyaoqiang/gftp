package com.study.gftp;

import java.io.IOException;
import java.io.InputStream;

@Deprecated
public class DefaultFileSplitter extends AbstractFileSplitter{

    public DefaultFileSplitter(String fileName, int chunkSize) throws Exception {
        super(fileName, chunkSize);
        this.split();
    }
    public DefaultFileSplitter(InputStream in,int chuckSize) throws IOException {
        super(in,chuckSize);
        this.split();
    }
}
