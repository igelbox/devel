package server;

import java.nio.channels.SocketChannel;
import server.etty.*;

/**
 *
 * @author igel
 */
public class Main {

    public static void main( String[] args ) throws Throwable {
        Runnable m = new Acceptor( 8080, 6 ) {

            @Override
            protected AbstractSession createSession( SocketChannel channel ) {
                return new AbstractSession( channel ) {
                };
            }
        };
//        Runnable m = new Netty();
        m.run();
    }
}
