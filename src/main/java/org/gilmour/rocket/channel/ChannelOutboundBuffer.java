package org.gilmour.rocket.channel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;

public class ChannelOutboundBuffer {
    private final Logger logger = LoggerFactory.getLogger(ChannelOutboundBuffer.class);
    private List<Entry> buffers;

    public ChannelOutboundBuffer() {
        this.buffers = new LinkedList<>();
    }

    //todo: threshold

    public void addBuffer(ByteBuffer buffer, ChannelPromise promise) {
        // empty buffer, discard
        if (!buffer.hasRemaining()) {
            return;
        }
        buffers.add(new Entry(buffer, promise));
    }

    public boolean isEmpty(){
        return buffers.isEmpty();
    }

    public Entry get() {
        if (buffers.isEmpty())
            return null;
        return buffers.get(0);
    }

    public void pop() {
        if (!buffers.isEmpty())
            buffers.remove(0);
    }

    class Entry {
        ByteBuffer buffer;
        ChannelPromise promise;

        Entry(ByteBuffer buffer, ChannelPromise promise) {
            this.buffer = buffer;
            this.promise = promise;
        }

        public ByteBuffer getBuffer(){
            return buffer;
        }

        public ChannelPromise getPromise(){
            return promise;
        }
    }
}
