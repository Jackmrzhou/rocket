package org.gilmour.rocket.core;

// default channel reader
public class EmptyChannelReader implements ChannelHandler{
    @Override
    public void ChannelRead(ChannelHandlerContext context, Object msg) {
        // do nothing here
        context.fireChannelRead(msg);
    }
}
