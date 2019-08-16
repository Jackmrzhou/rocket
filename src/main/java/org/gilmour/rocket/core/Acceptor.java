package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.WrapSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketOption;
import java.net.SocketOptions;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Acceptor implements Runnable{
    private Selector selector;
    private ServerSocketChannel ssc;
    private final Logger logger = LoggerFactory.getLogger(Acceptor.class);
    private InetSocketAddress address;
    private ChannelInitializer initializer;

    private NioEventLoopGroup eventLoops;

    public Acceptor(ChannelInitializer initializer, NioEventLoopGroup eventLoops){
        this.initializer = initializer;
        this.eventLoops = eventLoops;
    }

    @Override
    public void run() {
        try {
            if (address == null) {
                throw new Exception("binding address not set");
            }

            selector = Selector.open();
            ssc = ServerSocketChannel.open();
            ssc.configureBlocking(false);
            ssc.bind(address);
            ssc.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("server initialization done. Start Listening...");
            while (true) {
                int numKeys = selector.select();
                logger.info("incoming connections: {}", numKeys);
                Iterator keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = (SelectionKey) keys.next();
                    keys.remove();

                    if (!key.isValid()) {
                        logger.warn("invalid key: {}", key);
                    }

                    if (key.isAcceptable()) {
                        doAccept(key);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doAccept(SelectionKey key) throws IOException {
        ServerSocketChannel channel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = channel.accept();
        clientChannel.setOption(StandardSocketOptions.TCP_NODELAY, true);
        logger.info("remote {} connected", clientChannel.getRemoteAddress());
        clientChannel.configureBlocking(false);
        // unregister from acceptor's selector
        key.cancel();
        // do init
        WrapSocketChannel wrapSocketChannel = new WrapSocketChannel(clientChannel);
        initializer.initChannel(wrapSocketChannel);
        eventLoops.AddNewChannel(wrapSocketChannel);
    }

    public void setBindingAddr(InetSocketAddress addr) {
        this.address = addr;
    }
}
