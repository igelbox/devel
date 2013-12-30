package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Collection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.*;

/**
 *
 * @author igel
 */
public class Listener extends Thread {

    private final Selector selector = Selector.open();
    private final Queue<SocketChannel> toReg = new ConcurrentLinkedQueue<SocketChannel>();
    private final AtomicInteger state = new AtomicInteger();
    private int ii;

    public Listener() throws IOException {
    }

    synchronized void accept( SocketChannel channel ) throws Exception {
        if ( !state.compareAndSet( 0, 2 ) ) {
            state.set( 2 );
            selector.wakeup();
            wait();
        }
        System.out.println( "A" + (ii++) );
        _accept( channel );
        state.set( 0 );
        notify();
//        toReg.offer( channel );
//        synchronized ( this ) {
//            if ( select ) {
//                accept = true;
//                selector.wakeup();
//            }
//        }
    }

    @Override
    public void run() {
        try {
            while ( true ) {
                synchronized ( this ) {
                    while ( !state.compareAndSet( 0, 1 ) )
                        wait();
                }
                int n = selector.select();
                synchronized ( this ) {
                    state.compareAndSet( 1, 0 );
                    notify();
                }
//                SocketChannel ch;
//                while ( (ch = toReg.poll()) != null )
//                    _accept( ch );
                if ( n <= 0 )
                    continue;
                Collection<SelectionKey> keys = selector.selectedKeys();
                for ( SelectionKey k : keys ) {
                    Session session = (Session) k.attachment();
                    SocketChannel channel = (SocketChannel) k.channel();
                    Queue<ByteBuffer> queue = session.queue;
                    if ( k.isWritable() ) {
                        ByteBuffer buff = queue.peek();
                        if ( buff != null ) {
                            channel.write( buff );
                            if ( !buff.hasRemaining() )
                                queue.remove();
                        }
                    }
                    if ( k.isReadable() ) {
                        ByteBuffer buff = ByteBuffer.allocate( 4096 );
                        channel.read( buff );
                        buff.flip();
                        if ( buff.hasRemaining() )
                            queue.offer( buff );
                        else {
                            channel.close();
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

    private void _accept( SocketChannel channel ) throws IOException {
        channel.configureBlocking( false );
        SelectionKey sk = channel.register( selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE );
//        sk.attach( new Session( sk ) );
    }
}
