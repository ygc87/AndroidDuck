package com.xhy.neihanduanzi.widget.progress;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mkt on 2017/11/9.
 */

public final class ContentLengthReadCallbackInputStream extends FilterInputStream {
    private InputStreamReadCallback readCallback;
    private final long contentLength;
    private int readSoFar;
    public static InputStream obtain(InputStream other, long contentLength, InputStreamReadCallback readCallback) {
        return new ContentLengthReadCallbackInputStream(other, contentLength, readCallback);
    }
    ContentLengthReadCallbackInputStream(InputStream in, long contentLength, InputStreamReadCallback readCallback) {
        super(in);
        this.readCallback = readCallback;
        this.contentLength = contentLength;
    }
    @Override
    public synchronized int available() throws IOException {
        return (int) Math.max(contentLength - readSoFar, in.available());
    }
    @Override
    public synchronized int read() throws IOException {
        return checkReadSoFarOrThrow(super.read());
    }
    @Override
    public int read(byte[] buffer) throws IOException {
        return read(buffer, 0 , buffer.length );
    }
    @Override
    public synchronized int read(byte[] buffer, int byteOffset, int byteCount) throws IOException {
        return checkReadSoFarOrThrow(super.read(buffer, byteOffset, byteCount));
    }
    private int checkReadSoFarOrThrow(int read) throws IOException {
        if (read >= 0) {
            readSoFar += read;
        } else if (contentLength - readSoFar > 0) {
            throw new IOException("Failed to read all expected data"
                    + ", expected: " + contentLength
                    + ", but read: " + readSoFar);
        }
        if (readCallback != null) {
            //实际网络读取数和总长度在这里回调到外部
            readCallback.onRead(readSoFar, contentLength);
        }
        return read;
    }
}
