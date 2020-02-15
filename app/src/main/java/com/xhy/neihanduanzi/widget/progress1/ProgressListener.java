package com.xhy.neihanduanzi.widget.progress1;

public interface ProgressListener {

    void progress(long bytesRead, long contentLength, boolean done);

}
