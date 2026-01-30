package com.jbase.core.storage;

import java.nio.ByteBuffer;

public class MetaPage {

    private final ByteBuffer buf;

    public MetaPage(byte[] data) {
        this.buf = ByteBuffer.wrap(data);
    }

    public int getRootPageId() {
        return buf.getInt(0);
    }

    public void setRootPageId(int pageId) {
        buf.putInt(0, pageId);
    }

}
