package ccs.jack;

/**
 *
 * @author igel
 */
public class Jack {

    public static final String DEFAULT_AUDIO_TYPE = "32 bit float mono audio";
    public static final int PORT_IS_INPUT = 0x1;
    public static final int PORT_IS_OUTPUT = 0x2;
    public static final int PORT_IS_PHYSICAL = 0x4;

    static {
        System.loadLibrary( "jack" );
    }

    static class Error extends RuntimeException {
    }
    static final Client[] clients = new Client[16];
    static int clientsCount = 0;

    public static native long client_new( String name, int id ) throws Error;

    public static native void client_activate( long client ) throws Error;

    public static native void client_close( long client ) throws Error;

    public static native int buffer_size( long client ) throws Error;

    public static native int sample_rate( long client ) throws Error;

    public static native void get_buffer( long port, float[] buffer, int offset, int samples ) throws Error;

    public static native void set_buffer( long port, float[] buffer, int offset, int samples ) throws Error;

    static void on_shutdown( int clientId ) {
        clients[clientId].onShutdown();
    }

    public static native long client_port_register( long client, String name, String type, long flags, long bs ) throws Error;

    static int client_process( int clientId, int samples ) {
        return clients[clientId].process( samples );
    }
}
