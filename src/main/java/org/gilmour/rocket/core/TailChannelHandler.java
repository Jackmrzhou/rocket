package org.gilmour.rocket.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TailChannelHandler implements ChannelHandler {
    private final Logger logger = LoggerFactory.getLogger(TailChannelHandler.class);

    @Override
    public void ChannelRead(ChannelHandlerContext context, Object msg) {
        logger.warn("unhandled message {}", msg);
    }
}
