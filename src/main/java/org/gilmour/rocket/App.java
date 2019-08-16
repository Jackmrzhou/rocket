package org.gilmour.rocket;

import org.gilmour.rocket.channel.WrapSocketChannel;
import org.gilmour.rocket.core.ChannelHandler;
import org.gilmour.rocket.core.ChannelHandlerContext;
import org.gilmour.rocket.core.ChannelInitializer;
import org.gilmour.rocket.server.RocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.channels.Selector;
import java.nio.channels.spi.SelectorProvider;
import java.nio.charset.StandardCharsets;

/**
 * Hello world!
 *
 */
public class App 
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);
    public static void main( String[] args )
    {
        RocketServer server = new RocketServer();
        try {
            server.Bootstrap(new ChannelInitializer() {
                @Override
                public void initChannel(WrapSocketChannel channel) {
                    channel.getPipeline().AddLast(new ChannelHandler() {
                        @Override
                        public void ChannelRead(ChannelHandlerContext context, Object msg) {
                            if (msg instanceof ByteBuffer){
                                logger.info(msg.toString());
                                logger.info(new String(((ByteBuffer) msg).array()));
                            }else {
                                logger.warn("not ByteBuffer type");
                            }
                        }
                    });
                }
            });
            server.bind(9090);
            server.Start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
