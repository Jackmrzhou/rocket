package org.gilmour.rocket.channel;

import org.gilmour.rocket.core.HandlerPipeline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;

import org.gilmour.rocket.channel.ChannelOutboundBuffer.Entry;

public class WrapSocketChannel {
    private final Logger logger = LoggerFactory.getLogger(WrapSocketChannel.class);
    private HandlerPipeline pipeline;
    private java.nio.channels.SocketChannel JdkChannel;
    private volatile SelectionKey selectionKey;
    private ChannelOutboundBuffer outboundBuffer;

    public WrapSocketChannel(java.nio.channels.SocketChannel channel) {
        this.JdkChannel = channel;
        this.pipeline = new HandlerPipeline(this);
        this.outboundBuffer = new ChannelOutboundBuffer();
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

    // fired by write
    // doesn't actually flush into socket
    public void write(Object msg, ChannelPromise promise) {
        logger.debug("receive write request");
        if (msg instanceof ByteBuffer) {
            this.outboundBuffer.addBuffer((ByteBuffer) msg, promise);
        } else {
            logger.warn("not ByteBuffer type");
        }
    }

    public void flush() {
        logger.debug("receive flush request");
        if (this.outboundBuffer.isEmpty()){
            return;
        }

        Entry entry = this.outboundBuffer.get();

        // try write
        try {
            int sz = JdkChannel.write(entry.getBuffer());
            logger.debug("initial write total {}", sz);
            if (sz <= 0) {
                logger.debug("channel unwritable now");
                incompleteWrite(true);
            }
            while (sz > 0) {
                if (!entry.getBuffer().hasRemaining()) {
                    entry.getPromise().setDone();
                    this.outboundBuffer.pop();
                    if (this.outboundBuffer.isEmpty()){
                        break;
                    }
                    entry = this.outboundBuffer.get();
                }

                sz = JdkChannel.write(entry.getBuffer());
            }

            if (this.outboundBuffer.isEmpty()){
                incompleteWrite(false);
            }
        }catch (Exception e) {
            // todo: fire exception
            e.printStackTrace();
        }
    }

    private void incompleteWrite(boolean incomplete) {
        int ops = this.selectionKey.interestOps();
        if (incomplete) {
            this.selectionKey.interestOps(ops | SelectionKey.OP_WRITE);
        } else {
            // all buffers are written, unregister the event
            this.selectionKey.interestOps(ops & (~SelectionKey.OP_WRITE));
        }
    }
}
