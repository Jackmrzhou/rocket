package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.ChannelPromise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

class TailChannelHandler implements ChannelInBoundHandler, ChannelOutBoundHandler{
    private final Logger logger = LoggerFactory.getLogger(TailChannelHandler.class);
    private final HandlerPipeline pipeline;

    public TailChannelHandler(HandlerPipeline pipeline){
        this.pipeline = pipeline;
    }

    @Override
    public void ChannelRead(ChannelHandlerContext context, Object msg) {
        logger.warn("unhandled message {}", msg);
    }

    // Tail handler write all the message into internal buffer
    @Override
    public void ChannelWrite(ChannelHandlerContext context, Object msg, ChannelPromise promise) {
        this.pipeline.getChannel().write(msg, promise);
    }

    @Override
    public void ChannelFlush(ChannelHandlerContext context) {
        this.pipeline.getChannel().flush();
    }
}
