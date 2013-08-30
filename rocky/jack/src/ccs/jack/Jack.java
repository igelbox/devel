package ccs.jack;

/**
 *
 * @author igel
 */
public class Jack {

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

    static int process( int client, int samples ) {
        return clients[client].process( samples );
    }
}
