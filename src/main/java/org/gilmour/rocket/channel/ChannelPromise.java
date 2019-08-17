package org.gilmour.rocket.channel;

public interface ChannelPromise extends ChannelFuture{
    void setDone();
}
