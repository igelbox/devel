package client;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;

/**
 *
 * @author igel
 */
public class Test extends Thread {

    static final byte[] DATA = new byte[16384];

    static {
        for ( int i = 0; i < DATA.length; i++ )
            DATA[i] = (byte) i;
    }
    private final Selector selector = Selector.open();
    private final SocketChannel channel = SocketChannel.open();
    private final SelectionKey key;
    private final int loops;

    public Test( int loops ) throws IOException {
        this.loops = loops;
        channel.connect( new InetSocketAddress( "localhost", 8080 ) );
        channel.configureBlocking( false );
        key = channel.register( selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE );
    }

    @Override
    public void run() {
        ByteBuffer wrt = ByteBuffer.wrap( DATA );
        ByteBuffer rdr = ByteBuffer.allocate( 4096 );
        try {
            for ( int _ = 0; _ < loops; _++ ) {
                wrt.clear();
                int idx = 0;
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
                while ( idx < DATA.length ) {
                    selector.select();
                    int ops = key.readyOps();
                    if ( (ops & SelectionKey.OP_READ) != 0 ) {
                        rdr.clear();
                        channel.read( rdr );
                        rdr.flip();
                        while ( rdr.hasRemaining() ) {
                            byte b0 = rdr.get(), b1 = (byte) idx;
                            if ( b0 != b1 )
                                throw new IllegalArgumentException();
                            idx++;
                        }
                    }
                    if ( (ops & SelectionKey.OP_WRITE) != 0 ) {
                        channel.write( wrt );
                        if ( !wrt.hasRemaining() )
                            key.interestOps( key.interestOps() & ~SelectionKey.OP_WRITE );
                    }
                    selector.selectedKeys().clear();
                }
            }
            for ( SelectionKey k : selector.keys() )
                k.channel().close();
            selector.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }
}
