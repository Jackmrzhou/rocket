package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.ChannelFuture;
import org.gilmour.rocket.channel.ChannelPromise;

public interface ChannelHandlerContext {
    // outbound
    ChannelFuture write(Object msg);
    ChannelFuture write(Object msg, ChannelPromise promise);

    void flush();

    // inbound
    void fireChannelRead(Object msg);
}
