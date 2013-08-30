package ccs.jack;

/**
 *
 * @author igel
 */
public class Client {

    static {
        System.loadLibrary( "jack" );
    }
    private final String name;
    private final long handle;

    @SuppressWarnings( "LeakingThisInConstructor" )
    public Client( String name ) throws Error {
        this.name = name;
        this.handle = open( name, Jack.register( this ) );
    }

    public String name() {
        return name;
    }

    public void activate() throws Error {
        activate( handle );
    }

    public void deactivate() throws Error {
        deactivate( handle );
    }

    protected int process( int samples ) {
        return 0;
    }

    public void close() {
        Jack.unregister( this );
        close( handle );
    }

    private static native long open( String name, int id ) throws Error;

    private static native void activate( long handle ) throws Error;

    private static native void deactivate( long handle ) throws Error;

    private static native int close( long handle );
}
