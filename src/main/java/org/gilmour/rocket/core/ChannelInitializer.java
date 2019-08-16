package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.WrapSocketChannel;

public abstract class ChannelInitializer {

    public abstract void initChannel(WrapSocketChannel channel);
}
