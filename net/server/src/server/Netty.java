package server;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.channel.StaticChannelPipeline;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

/**
 *
 * @author igel
 */
public class Netty implements Runnable {

    private static class Session extends SimpleChannelHandler {

        @Override
        public void messageReceived( ChannelHandlerContext ctx, MessageEvent e ) throws Exception {
            e.getChannel().write( e.getMessage() );
        }
    }

    @Override
    public void run() {
        ChannelFactory factory = new NioServerSocketChannelFactory( Executors.newCachedThreadPool(), Executors.newCachedThreadPool() );
        ServerBootstrap bootstrap = new ServerBootstrap( factory );
        bootstrap.setPipelineFactory( new ChannelPipelineFactory() {

            @Override
            public ChannelPipeline getPipeline() throws Exception {
                return new StaticChannelPipeline( new Session() );
            }
        } );
        bootstrap.bind( new InetSocketAddress( 8080 ) );
    }
}
