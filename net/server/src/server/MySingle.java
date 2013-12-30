package server;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 *
 * @author igel
 */
public class MySingle implements Runnable {

    private static final Queue<ByteBuffer> pool = new ConcurrentLinkedQueue<ByteBuffer>();

    private static class Session {

        final Queue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();
    }
    private final Selector selector = Selector.open();

    public MySingle() throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking( false );
        ServerSocket socket = channel.socket();
        socket.bind( new InetSocketAddress( 8080 ) );
        channel.register( selector, SelectionKey.OP_ACCEPT );
    }

    @Override
    public void run() {
        try {
            while ( true ) {
                int n = selector.select();
                if ( n <= 0 )
                    continue;
                Collection<SelectionKey> keys = selector.selectedKeys();
                for ( SelectionKey k : keys )
                    if ( k.isAcceptable() ) {
                        SocketChannel ch = ((ServerSocketChannel) k.channel()).accept();
                        ch.configureBlocking( false );
                        SelectionKey sk = ch.register( selector, SelectionKey.OP_READ );
                        sk.attach( new Session() );
                    } else {
                        Session session = (Session) k.attachment();
                        Queue<ByteBuffer> queue = session.queue;
                        SocketChannel channel = (SocketChannel) k.channel();
                        if ( k.isWritable() ) {
                            ByteBuffer buff = queue.peek();
                            if ( buff != null ) {
                                channel.write( buff );
                                if ( !buff.hasRemaining() )
                                    pool.offer( queue.remove() );
                            }
                            if ( queue.isEmpty() )
                                k.interestOps( k.interestOps() & ~SelectionKey.OP_WRITE );
                        }
                        if ( k.isReadable() ) {
                            ByteBuffer buff = pool.poll();
                            if ( buff == null )
                                buff = ByteBuffer.allocate( 16384 );
                            else
                                buff.clear();
                            channel.read( buff );
                            buff.flip();
                            if ( buff.hasRemaining() ) {
                                queue.offer( buff );
                                k.interestOps( k.interestOps() | SelectionKey.OP_WRITE );
                            } else {
                                k.channel().close();
                                k.cancel();
                            }
                        }
                    }
                keys.clear();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
