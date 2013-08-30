package ccs.jack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author igel
 */
public class Jack {

    static {
        try {
            File lib = File.createTempFile( ".rocky", ".so" );
            InputStream in = Jack.class.getResourceAsStream( "/libjack.so" );
            OutputStream out = new FileOutputStream( lib );
            try {
                byte[] buff = new byte[4096];
                int l;
                while ( (l = in.read( buff )) >= 0 )
                    out.write( buff, 0, l );
            } finally {
                out.close();
            }
            System.load( lib.getAbsolutePath() );
        } catch ( Throwable e ) {
            throw new RuntimeException( e );
        }
    }
    public static final String DEFAULT_AUDIO_TYPE = "32 bit float mono audio";
    private static Client[] clients = new Client[1];

    static synchronized int register( Client client ) {
        for ( int i = 0; i < clients.length; i++ )
            if ( clients[i] == null ) {
                clients[i] = client;
                return i;
            }
        int z = clients.length;
        Client[] tmp = new Client[z * 3 / 2 + 1];
        System.arraycopy( clients, 0, tmp, 0, z );
        tmp[z] = client;
        clients = tmp;
        return z;
    }

    static synchronized void unregister( Client client ) {
        for ( int i = 0; i < clients.length; i++ )
            if ( clients[i] == client ) {
                clients[i] = null;
                break;
            }
    }

    static int onProcess( int client, int samples ) {
        return clients[client].process( samples );
    }

    static void onClientReg( String name, boolean reg ) {
        System.out.println( "cr:\"" + name + "\":" + reg );
    }

    static void onPortReg( long handle, boolean reg ) {
        System.out.println( "pr:" + handle + ":" + reg );
    }

    static void onPortConnect( long aHandle, long bHandle, boolean connect ) {
        System.out.println( "pc:" + aHandle + ":" + bHandle + ":" + connect );
    }

    static void onPortRename( long handle, String oldName, String newName ) {
    }

    public static native long getTime();
}
