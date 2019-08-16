package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.WrapSocketChannel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NioEventLoopGroup {
    List<NioEventLoop> nioEventLoops;
    int counter;
    public  NioEventLoopGroup(int num) {
        counter = 0;
        nioEventLoops = new ArrayList<>();
        for (int i = 0; i < num; ++i) {
            nioEventLoops.add(new NioEventLoop(null));
        }
    }

    public void AddNewChannel(WrapSocketChannel channel) throws IOException {
        counter++;
        if (counter > nioEventLoops.size()){
            counter = 0;
        }
        nioEventLoops.get(counter).AddNewChannel(channel);
    }
}
