package ccs.jack;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class Client {

    private final String name;
    private final long handle;
    private final Collection<Port> ports = new ArrayList<Port>();
    private boolean active;

    @SuppressWarnings( "LeakingThisInConstructor" )
    public Client( String name ) throws Error {
        this.name = name;
        this.handle = open( name, Jack.register( this ) );
    }

    public String name() {
        return name;
    }

    public boolean active() {
        return active;
    }

    public void activate() throws Error {
        activate( handle );
        active = true;
    }

    public void deactivate() throws Error {
        deactivate( handle );
        active = false;
    }

    protected int process( int samples ) {
        return 0;
    }

    public void close() {
        Jack.unregister( this );
        close( handle );
    }

    public Port registerPort( String name, long flags ) {
        Port p = new Port( regPort( handle, name, Jack.DEFAULT_AUDIO_TYPE, flags, 0 ) );
        ports.add( p );
        return p;
    }

    public void unregisterPort( Port p ) {
        if ( ports.remove( p ) )
            unregPort( handle, p.handle );
    }

    public Iterable<Port> ports() {
        return ports;
    }

    public Port[] findPorts( String namePattern, String typePattern, long flags ) {
        String[] tmp = findPorts( handle, namePattern, typePattern, flags );
        Port[] result = new Port[tmp.length];
        for ( int i = 0; i < result.length; i++ )
            result[i] = new Port( portByName( handle, tmp[i] ) );
        return result;
    }

    public Port[] findAllPorts() {
        return findPorts( null, null, 0 );
    }

    public int bufferSize() {
        return bufferSize( handle );
    }

    public int sampleRate() {
        return sampleRate( handle );
    }

    public void connectPorts( Port src, Port dst ) {
        connectPorts( handle, src.fullName(), dst.fullName() );
    }

    public void disconnectPorts( Port src, Port dst ) {
        disconnectPorts( handle, src.fullName(), dst.fullName() );
    }

    private static native long open( String name, int id ) throws Error;

    private static native void activate( long client ) throws Error;

    private static native void deactivate( long client ) throws Error;

    private static native int close( long client );

    private static native String[] findPorts( long client, String namePattern, String typePattern, long flags );

    private static native long regPort( long client, String name, String type, long flags, long bs );

    private static native long portByName( long client, String name );

    private static native void unregPort( long client, long port );

    public static native int bufferSize( long client );

    public static native int sampleRate( long client );

    private static native void connectPorts( long client, String source, String destination );

    private static native void disconnectPorts( long client, String source, String destination );
}
