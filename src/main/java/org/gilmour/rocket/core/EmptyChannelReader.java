package org.gilmour.rocket.core;

// default channel reader
public class EmptyChannelReader implements ChannelInBoundHandler {
    private HandlerPipeline pipeline;

    public EmptyChannelReader(HandlerPipeline pipeline){
        this.pipeline = pipeline;
    }

    @Override
    public void ChannelRead(ChannelHandlerContext context, Object msg) {
        // do nothing here
        context.fireChannelRead(msg);
    }
}
