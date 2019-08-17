package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.ChannelPromise;

public interface ChannelOutBoundHandler extends ChannelHandler{
    void ChannelWrite(ChannelHandlerContext context, Object msg, ChannelPromise promise);
    void ChannelFlush(ChannelHandlerContext context);
}
