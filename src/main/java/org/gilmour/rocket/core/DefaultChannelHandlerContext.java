package org.gilmour.rocket.core;

public class DefaultChannelHandlerContext implements ChannelHandlerContext {
    DefaultChannelHandlerContext next, prev;
    ChannelHandler handler;

    public DefaultChannelHandlerContext(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public void fireChannelRead(Object msg) {
        next.invokeChannelRead(msg);
    }

    void invokeChannelRead(Object msg) {
        handler.ChannelRead(this, msg);
    }
}
