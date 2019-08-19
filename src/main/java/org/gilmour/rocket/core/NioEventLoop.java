package org.gilmour.rocket.core;

import org.gilmour.rocket.channel.WrapSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class NioEventLoop implements Runnable{
    private Logger logger = LoggerFactory.getLogger(NioEventLoop.class);
    private Selector selector;
    private ExecutorService globalThreadPool;
    private ConcurrentLinkedQueue<WrapSocketChannel> NewChannelQueue;
    private long totalChannels;
    private ExecutorService singleThreadExec;
    private volatile AtomicBoolean wakenUp;

    public NioEventLoop(ExecutorService globalThreadPool){
        this.globalThreadPool = globalThreadPool;
        this.NewChannelQueue = new ConcurrentLinkedQueue<>();
        this.singleThreadExec = Executors.newSingleThreadExecutor();
        this.singleThreadExec.submit(this);
    }

    @Override
    public void run() {
        try {
            selector = Selector.open();
            while (true) {
                registerAll();

                int numKeys = selector.select();
                if (numKeys == 0) {
                    logger.info("wakeup on zero events!");
                    continue;
                }
                processSelectedKeys();
            }

        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void AddNewChannel(WrapSocketChannel channel) throws IOException {
        NewChannelQueue.add(channel);
        //may need kick off
        selector.wakeup();
        //wakenUp.getAndSet(true);
    }

    private void registerAll() throws IOException{
        Iterator<WrapSocketChannel> channels = NewChannelQueue.iterator();
        while (channels.hasNext()) {
            totalChannels++;
            WrapSocketChannel channel = channels.next();
            channels.remove();
            channel.register(selector, SelectionKey.OP_READ, channel);
        }
    }

    private void processSelectedKeys() {
        Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
        while (keyIterator.hasNext()) {
            SelectionKey key = keyIterator.next();
            if (!key.isValid()) {
                logger.warn("invalid key {}", key);
                key.cancel();
            }

            if (key.isReadable()) {
                WrapSocketChannel channel = (WrapSocketChannel) key.attachment();
                channel.read();
            }

            if (key.isWritable()) {
                WrapSocketChannel channel = (WrapSocketChannel) key.attachment();
                channel.flush();
            }

            keyIterator.remove();
        }
    }
}
