package org.gilmour.rocket;

import org.gilmour.rocket.channel.ChannelPromise;
import org.gilmour.rocket.channel.WrapSocketChannel;
import org.gilmour.rocket.core.ChannelInBoundHandler;
import org.gilmour.rocket.core.ChannelHandlerContext;
import org.gilmour.rocket.core.ChannelInitializer;
import org.gilmour.rocket.core.ChannelOutBoundHandler;
import org.gilmour.rocket.server.RocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

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
                    channel.getPipeline().AddLast(new ChannelInBoundHandler() {
                        @Override
                        public void ChannelRead(ChannelHandlerContext context, Object msg) {
                            if (msg instanceof ByteBuffer){
                                logger.info(msg.toString());
                                ((ByteBuffer) msg).flip();
                                String msgStr = Charset.defaultCharset().decode((ByteBuffer) msg).toString();
                                logger.info("{}", msgStr.length());
                                context.write("From Server: " + msgStr);
                                context.flush();
                            }else {
                                logger.warn("not ByteBuffer type");
                            }
                        }
                    });

                    channel.getPipeline().AddLast(new ChannelOutBoundHandler() {
                        @Override
                        public void ChannelWrite(ChannelHandlerContext context, Object msg, ChannelPromise promise) {
                            logger.info("outbound handler got write message:{}", msg);
                            // do encoding
                            if (msg instanceof String) {
                                logger.info("start encoding, total size {}", ((String) msg).length());
                                context.write(ByteBuffer.wrap(((String) msg).getBytes(Charset.defaultCharset())), promise);
                            }
                        }

                        @Override
                        public void ChannelFlush(ChannelHandlerContext context) {
                            logger.info("outbound handler got flush request");
                            context.flush();
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
