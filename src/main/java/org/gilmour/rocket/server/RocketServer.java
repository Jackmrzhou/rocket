package org.gilmour.rocket.server;

import org.gilmour.rocket.core.Acceptor;
import org.gilmour.rocket.core.ChannelInitializer;
import org.gilmour.rocket.core.NioEventLoop;
import org.gilmour.rocket.core.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class RocketServer {
    private ExecutorService acceptExecutor;
    private Acceptor acceptor;
    private ChannelInitializer initializer;
    private NioEventLoopGroup nioEventLoopGroup;

    public void Bootstrap(ChannelInitializer initializer) throws Exception {
        // default init
        acceptExecutor = Executors.newSingleThreadExecutor();
        nioEventLoopGroup = new NioEventLoopGroup(2);
        acceptor = new Acceptor(initializer, nioEventLoopGroup);
    }

    public void bind(int port) {
        this.acceptor.setBindingAddr(new InetSocketAddress("0.0.0.0", port));
    }

    public void Start() {
        Future f = this.acceptExecutor.submit(acceptor);
        try {
            f.get();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
