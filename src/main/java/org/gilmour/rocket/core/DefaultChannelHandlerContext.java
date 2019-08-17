package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.ChannelFuture;
import org.gilmour.rocket.channel.ChannelPromise;
import org.gilmour.rocket.channel.DefaultChannelPromise;

public class DefaultChannelHandlerContext implements ChannelHandlerContext {
    DefaultChannelHandlerContext next, prev;
    ChannelHandler handler;

    public DefaultChannelHandlerContext(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public ChannelFuture write(Object msg) {
        ChannelPromise promise = new DefaultChannelPromise();
        next.invokeWrite(msg, false, promise);
        return promise;
    }

    @Override
    public ChannelFuture write(Object msg, ChannelPromise promise) {
        next.invokeWrite(msg, false, promise);
        return promise;
    }

    @Override
    public void flush() {
        next.invokeFlush();
    }

    public void invokeFlush() {
        if (handler instanceof ChannelOutBoundHandler) {
            ((ChannelOutBoundHandler) handler).ChannelFlush(this);
        } else {
            flush();
        }
    }

    private ChannelFuture invokeWrite(Object msg, boolean flush, ChannelPromise promise) {
        if (handler instanceof ChannelOutBoundHandler) {
            ((ChannelOutBoundHandler) handler).ChannelWrite(this, msg, promise);
        } else {
            write(msg, promise);
        }
        return promise;
    }

    @Override
    public void fireChannelRead(Object msg) {
        next.invokeChannelRead(msg);
    }

    void invokeChannelRead(Object msg) {
        if (handler instanceof ChannelInBoundHandler){
            ((ChannelInBoundHandler) handler).ChannelRead(this, msg);
        } else {
            fireChannelRead(msg);
        }
    }
}
