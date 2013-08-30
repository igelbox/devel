package ccs.rocky.core;

//import ccs.rocky.core.Node.Listener.PortOp;
import ccs.util.Cloud;

/**
 *
 * @author igel
 */
public class Port {

    public static class Output extends Port {

        public Output( int id, Node node ) {
            super( id, node );
        }

        protected void connected( Input p, boolean connected ) {
        }
    }

    public static class Input extends Port {

        public static abstract class Listener {

            protected void notifyConnected( Input port, Output to ) {
            }
        }
        private final Node.Listener nl = new Node.Listener() {

            @Override
            protected void notifyDelete() {
                connect( null );
            }

//            @Override
//            protected void notifyPort( Port port, PortOp op ) {
//                if ( (port == connected) && (op == PortOp.DEL) )
//                    connect( null );
//            }
            @Override
            protected void notifyFlow() {
                node().notifyFlow();
            }
        };
        private final Cloud<Listener> listeners = new Cloud<Listener>();

        public Input( int id, Node node ) {
            super( id, node );
        }
        protected Output connected;

        public Output connected() {
            return connected;
        }

        public void connect( Output to ) {
            if ( connected != null ) {
                connected.node().unlisten( nl );
                connected.connected( this, false );
            }
            connected = to;
            if ( connected != null ) {
                connected.node().listen( nl );
                connected.connected( this, true );
            }
            for ( Listener l : listeners )
                l.notifyConnected( this, to );
            node().notifyFlow();
        }

        public boolean listen( Listener listener ) {
            return listeners.add( listener );
        }

        public boolean unlisten( Listener listener ) {
            return listeners.remove( listener );
        }
    }
    private final int id;
    private final Node node;

    public Port( int id, Node node ) {
        this.id = id;
        this.node = node;
    }

    public int id() {
        return id;
    }

    public String idWithNode() {
        return String.format( "%d.%d", node.id(), id );
    }

    public Node node() {
        return node;
    }

    /** Краткое имя порта */
    public String caption() {
        return "";
    }
}
