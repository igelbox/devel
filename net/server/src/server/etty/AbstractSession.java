package server.etty;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.*;
import java.util.concurrent.*;

/**
 *
 * @author igel
 */
public abstract class AbstractSession {

    private ByteBuffer buffer;
    final Queue<ByteBuffer> queue = new ConcurrentLinkedQueue<ByteBuffer>();
    final SocketChannel channel;

    public AbstractSession( SocketChannel channel ) {
        this.channel = channel;
    }

    ByteBuffer peek() {
        if ( buffer == null )
            buffer = queue.poll();
        return buffer;
    }

    boolean rem() {
        buffer = queue.poll();
        return buffer != null;
    }
}
