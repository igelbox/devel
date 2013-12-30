package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 *
 * @author igel
 */
public class Mina implements Runnable {

    @Override
    public void run() {
        try {
            NioSocketAcceptor acceptor = new NioSocketAcceptor();
            acceptor.setHandler( new IoHandlerAdapter() {

                @Override
                public void messageReceived( IoSession session, Object message ) throws Exception {
                    session.write( message );
                }
            } );
            acceptor.bind( new InetSocketAddress( 8080 ) );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
