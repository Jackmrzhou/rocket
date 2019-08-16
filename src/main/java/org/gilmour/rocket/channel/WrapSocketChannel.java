package org.gilmour.rocket.channel;

import org.gilmour.rocket.core.HandlerPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

public class WrapSocketChannel {
    private final Logger logger = LoggerFactory.getLogger(WrapSocketChannel.class);
    private HandlerPipeline pipeline;
    private java.nio.channels.SocketChannel JdkChannel;
    private volatile SelectionKey selectionKey;

    public WrapSocketChannel(java.nio.channels.SocketChannel channel) {
        this.JdkChannel = channel;
        this.pipeline = new HandlerPipeline(this);
    }

    public SelectionKey register(Selector sel, int ops,
                          Object att)
            throws ClosedChannelException {
        SelectionKey key = JdkChannel.register(sel, ops, att);
        this.selectionKey = key;
        return key;
    }

    public HandlerPipeline getPipeline() {
        return pipeline;
    }

    public java.nio.channels.SocketChannel underlying() {
        return this.JdkChannel;
    }

    // fired by read event
    public void read() {
        try {
            assert selectionKey != null;
            ByteBuffer buffer = BufferAllocator.allocate();
            int sz = JdkChannel.read(buffer);
            if (sz < 0) {
                // EOF received
                logger.info("remote {} close the connection", JdkChannel.getRemoteAddress());
                pipeline.fireInactive();
                int ops = this.selectionKey.interestOps();
                this.selectionKey.interestOps(ops & (~SelectionKey.OP_READ));
            }
            while (sz > 0) {
                logger.debug("fire read on {}, total size {}", JdkChannel.getRemoteAddress(), sz);
                pipeline.fireChannelRead(buffer);
                buffer.clear();
                sz = JdkChannel.read(buffer);
            }
        } catch (Exception e) {
            // todo: fire exception
            e.printStackTrace();
        }
    }
}
