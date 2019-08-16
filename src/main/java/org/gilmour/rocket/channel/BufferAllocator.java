package org.gilmour.rocket.channel;

import java.nio.ByteBuffer;

public class BufferAllocator {

    private static final int DefaultBufferSize = 4096;

    public static ByteBuffer allocate() {
        return ByteBuffer.allocate(DefaultBufferSize);
    }
}
