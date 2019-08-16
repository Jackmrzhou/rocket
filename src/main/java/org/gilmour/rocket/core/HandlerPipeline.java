package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.WrapSocketChannel;

public class HandlerPipeline {
    private final WrapSocketChannel channel;
    private DefaultChannelHandlerContext HeadContext, TailContext;
    public HandlerPipeline(WrapSocketChannel channel) {
        this.channel = channel;
        initPipeline();
    }

    public void AddLast(ChannelHandler handler) {
        DefaultChannelHandlerContext newCtx = new DefaultChannelHandlerContext(handler);
        DefaultChannelHandlerContext prevCtx = TailContext.prev;
        TailContext.prev = newCtx;
        newCtx.next = TailContext;
        prevCtx.next = newCtx;
        newCtx.prev = prevCtx;
    }

    private void initPipeline(){
        HeadContext = new DefaultChannelHandlerContext(new EmptyChannelReader());
        TailContext = new DefaultChannelHandlerContext(new TailChannelHandler());
        HeadContext.next = TailContext;
        HeadContext.prev = null;
        TailContext.prev = HeadContext;
        TailContext.next = null;
    }

    public void fireChannelRead(Object msg) {
        HeadContext.invokeChannelRead(msg);
    }

    public void fireInactive() {

    }
}
