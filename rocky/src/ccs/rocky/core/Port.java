package ccs.rocky.core;

import ccs.rocky.core.Node.Listener.PortOp;
import ccs.util.Cloud;

/**
 *
 * @author igel
 */
public class Port {

    public static class Output extends Port {

        public Output( Node node ) {
            super( node );
        }
    }

    public static class Input extends Port {

        public static abstract class Listener {

            protected void notifyConnected( Input port, Output to ) {
            }
        }
        private final Node.Listener nl = new Node.Listener() {

            @Override
            protected void notifyPort( Port port, PortOp op ) {
                if ( (port == connected) && (op == PortOp.DEL) )
                    connect( null );
            }
        };
        private final Cloud<Listener> listeners = new Cloud<Listener>();

        public Input( Node node ) {
            super( node );
        }
        protected Output connected;

        public Output connected() {
            return connected;
        }

        public void connect( Output to ) {
            connected = to;
            if ( to != null )
                to.node().listen( nl );
            for ( Listener l : listeners )
                l.notifyConnected( this, to );
        }

        public boolean listen( Listener listener ) {
            return listeners.add( listener );
        }

        public boolean unlisten( Listener listener ) {
            return listeners.remove( listener );
        }
    }
    private final Node node;

    public Port( Node node ) {
        this.node = node;
    }

    public Node node() {
        return node;
    }

    /** Краткое имя порта */
    public String caption() {
        return "";
    }
}
