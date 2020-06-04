package com.study.gftp;

import java.io.IOException;
import java.util.List;

public interface FileSplitter {
    void split() throws IOException;

    List<byte[]> getData();

    boolean hasNext();

    byte[] next() throws IOException;

    boolean isFinished();
}
