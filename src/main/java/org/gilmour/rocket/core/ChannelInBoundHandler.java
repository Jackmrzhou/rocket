package org.gilmour.rocket.core;

public interface ChannelInBoundHandler extends ChannelHandler {
    void ChannelRead(ChannelHandlerContext context, Object msg);
}
