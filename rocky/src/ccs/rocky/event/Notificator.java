package ccs.rocky.event;

import ccs.rocky.Node.Port;
import ccs.rocky.Node.Port.Input;
import ccs.rocky.Node.Port.Output;
import ccs.util.Cloud;

/**
 *
 * @author igel
 */
public class Notificator<T extends Listener> {

    public static class PortRemoval extends Notificator<Listener.PortRemove> implements Listener.PortRemove {

        @Override
        public void portRemoved( Port port ) {
            for ( Listener.PortRemove l : listeners )
                l.portRemoved( port );
        }
    }

    public static class PortConnect extends Notificator<Listener.PortConnect> implements Listener.PortConnect {

        @Override
        public void portConnected( Input port, Output to ) {
            for ( Listener.PortConnect l : listeners )
                l.portConnected( port, to );
        }
    }
    protected final Cloud<T> listeners = new Cloud<T>();

    public boolean attach( T l ) {
        return listeners.add( l );
    }

    public boolean detach( T l ) {
        return listeners.remove( l );
    }
}
