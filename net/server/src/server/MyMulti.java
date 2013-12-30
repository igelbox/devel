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
public class MyMulti implements Runnable {

    private static final Queue<ByteBuffer> pool = new ConcurrentLinkedQueue<ByteBuffer>();

    private static class Session {

        final Queue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();
    }

    private static class Listener extends Thread {

        private final Selector selector = Selector.open();
        private final SocketChannel[] pending = new SocketChannel[1024];
        private volatile int pendPos;
        private volatile boolean sel;

        public Listener() throws IOException {
        }

        @Override
        public void run() {
            try {
                while ( true ) {
                    sel = true;
                    int n = selector.select( 100 );
                    sel = false;
                    if ( pendPos > 0 )
                        synchronized ( pending ) {
                            while ( pendPos > 0 ) {
                                SocketChannel ch = pending[--pendPos];
                                ch.configureBlocking( false );
                                SelectionKey sk = ch.register( selector, SelectionKey.OP_READ );
                                sk.attach( new Session() );
                            }
                            pending.notify();
                        }
                    if ( n <= 0 )
                        continue;
                    Collection<SelectionKey> keys = selector.selectedKeys();
                    for ( SelectionKey k : keys ) {
                        Session session = (Session) k.attachment();
                        Queue<ByteBuffer> queue = session.queue;
                        SocketChannel channel = (SocketChannel) k.channel();
                        if ( k.isWritable() ) {
                            ByteBuffer buff = queue.peek();
                            if ( buff != null ) {
                                int left = 16, l;
                                do
                                    l = channel.write( buff );
                                while ( (l <= 0) && buff.hasRemaining() && (left-- > 0) );
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
                            int left = 16, l;
                            do
                                l = channel.read( buff );
                            while ( (l <= 0) && buff.hasRemaining() && (left-- > 0) );
                            buff.flip();
                            if ( buff.hasRemaining() ) {
                                queue.offer( buff );
                                k.interestOps( k.interestOps() | SelectionKey.OP_WRITE );
                            } else {
                                pool.offer( buff );
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

        public void accept( SocketChannel channel ) throws IOException, InterruptedException {
            synchronized ( pending ) {
                while ( pendPos > pending.length )
                    pending.wait();
                pending[pendPos++] = channel;
            }
            if ( sel )
                selector.wakeup();
        }
    }
    private final Selector selector = Selector.open();

    public MyMulti() throws IOException {
        ServerSocketChannel channel = ServerSocketChannel.open();
        channel.configureBlocking( false );
        ServerSocket socket = channel.socket();
        socket.bind( new InetSocketAddress( 8080 ) );
        channel.register( selector, SelectionKey.OP_ACCEPT );
    }

    @Override
    public void run() {
        try {
            Listener[] listeners = new Listener[4];
            for ( int i = 0; i < listeners.length; i++ ) {
                listeners[i] = new Listener();
                listeners[i].start();
            }
            int cl = 0;
            while ( true ) {
                int n = selector.select();
                if ( n <= 0 )
                    continue;
                Collection<SelectionKey> keys = selector.selectedKeys();
                for ( SelectionKey k : keys )
                    if ( k.isAcceptable() ) {
                        listeners[cl].accept( ((ServerSocketChannel) k.channel()).accept() );
                        cl = (cl + 1) % listeners.length;
                    }
                keys.clear();
            }
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }
}
