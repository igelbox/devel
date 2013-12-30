package server;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author igel
 */
public class Session {

    private static final Queue<ByteBuffer> pool = new ConcurrentLinkedQueue<ByteBuffer>();
    private final Semaphore semaphore;
    private final SelectionKey key;
    private final SocketChannel channel;
    private final AtomicBoolean writeLock = new AtomicBoolean();
    final Queue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();
    final Runnable reader = new Runnable() {

        @Override
        public void run() {
            try {
                boolean run = true, readed = false;
                while ( run ) {
                    ByteBuffer buffer = pool.poll();
                    if ( buffer == null )
                        buffer = ByteBuffer.allocate( 16384 );
                    else
                        buffer.clear();
                    do
                        if ( channel.read( buffer ) <= 0 ) {
                            run = false;
                            break;
                        }
                    while ( buffer.hasRemaining() );
                    buffer.flip();
                    if ( buffer.hasRemaining() )
                        onReceived( buffer );
                    else {
                        if ( readed )
                            return;
                        pool.offer( buffer );
                        channel.close();
                        key.cancel();
                        return;
                    }
                    readed = true;
                }
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            } finally {
                semaphore.release();
            }
        }
    };
    final Runnable writer = new Runnable() {

        @Override
        public void run() {
            try {
                if ( !flush( false ) )
                    key.interestOps( key.interestOps() & ~SelectionKey.OP_WRITE );
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            } finally {
                semaphore.release();
            }
        }
    };

    public Session( SelectionKey key, Semaphore semaphore ) {
        this.key = key;
        channel = (SocketChannel) key.channel();
        this.semaphore = semaphore;
    }

    private void onReceived( ByteBuffer buffer ) throws IOException {
        sendx( buffer );
    }

    void sendx( ByteBuffer buffer ) throws IOException {
        if ( writeLock.compareAndSet( false, true ) )
            try {
                synchronized ( queue ) {
                    if ( queue.isEmpty() )
                        channel.write( buffer );
                    send( buffer, true );
                }
            } finally {
                writeLock.set( false );
            }
        else
            send( buffer, false );
    }

    void send( ByteBuffer buffer, boolean inPresend ) throws IOException {
        if ( buffer.hasRemaining() )
            synchronized ( queue ) {
                queue.offer( buffer );
                if ( inPresend || !flush( inPresend ) ) {
                    key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
                    key.selector().wakeup();
                }
            }
        else {
            pool.offer( buffer );
            if ( !flush( inPresend ) ) {
                key.interestOps( key.interestOps() | SelectionKey.OP_WRITE );
                key.selector().wakeup();
            }
        }
    }

    private boolean flush( boolean locked ) throws IOException {
        if ( locked || writeLock.compareAndSet( false, true ) )
            try {
                ByteBuffer buff;
                while ( (buff = queue.peek()) != null ) {
                    channel.write( buff );
                    if ( buff.hasRemaining() )
                        return false;
                    pool.offer( queue.remove() );
                }
                return buff == null;
            } finally {
                if ( !locked )
                    writeLock.set( false );
            }
        return false;
    }
}
