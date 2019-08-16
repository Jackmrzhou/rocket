package org.gilmour.rocket.core;

public interface ChannelHandlerContext {


    void fireChannelRead(Object msg);
}
