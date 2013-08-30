package ccs.jack;

/**
 *
 * @author igel
 */
public class Client {

    private final long handle;

    public Client( String name ) {
        int id = Jack.clientsCount++;
        handle = Jack.client_new( name, id );
        Jack.clients[id] = this;
//        Jack.client_register( this );
    }

    public void activate() {
        Jack.client_activate( handle );
    }

    public long registerPort( String name, int flags ) {
        return Jack.client_port_register( handle, name, Jack.DEFAULT_AUDIO_TYPE, flags, 0 );
    }

    public int bufferSize() {
        return Jack.buffer_size( handle );
    }

    public int sampleRate() {
        return Jack.sample_rate( handle );
    }

    public void close() {
        Jack.client_close( handle );
    }

    protected int process( int samples ) {
        return 0;
    }

    protected void onShutdown() {
    }
}
