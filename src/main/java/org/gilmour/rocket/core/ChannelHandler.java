package org.gilmour.rocket.core;

public interface ChannelHandler {
    void ChannelRead(ChannelHandlerContext context, Object msg);
}
