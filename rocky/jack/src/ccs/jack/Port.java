package ccs.jack;

/**
 *
 * @author igel
 */
public class Port {

    public static final int IS_INPUT = 0x1;
    public static final int IS_OUTPUT = 0x2;
    public static final int IS_PHYSICAL = 0x4;
    final long handle;

    Port( long handle ) {
        this.handle = handle;
    }

    public void get( float[] buffer, int samples ) {
        getBuffer( handle, samples, buffer );
    }

    public void set( float[] buffer, int samples ) {
        setBuffer( handle, samples, buffer );
    }

    public String fullName() {
        return getFullName( handle );
    }

    public String clientName() {
        String[] sp = fullName().split( ":" );
        return sp[0];
    }

    public String name() {
        return getShortName( handle );
    }

    public void name( String name ) {
        setShortName( handle, name );
    }

    private static native void getBuffer( long port, int samples, float[] buffer );

    private static native void setBuffer( long port, int samples, float[] buffer );

    private static native String getFullName( long port );

    private static native String getShortName( long port );

    private static native void setShortName( long port, String name );
}
