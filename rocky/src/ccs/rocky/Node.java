package ccs.rocky;

import ccs.rocky.event.Listener;
import ccs.rocky.event.Notificator;
import ccs.rocky.event.Notificator.PortRemoval;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * Абстрактный узел графа обработки сигнала
 *
 * @author igel
 */
public abstract class Node {

    public static class Port {

        public static class Output extends Port {

            public Output( Node component ) {
                super( component );
            }
        }

        public static class Input extends Port {

            private final Listener.PortRemove l = new Listener.PortRemove() {

                @Override
                public void portRemoved( Port port ) {
                    if ( port == connectedTo )
                        connectTo( null );
                }
            };
            protected Output connectedTo;

            public Input( Node component ) {
                super( component );
            }

            public Output connectedTo() {
                return connectedTo;
            }

            public void connectTo( Output to ) {
                if ( connectedTo != null )
                    connectedTo.node().notifRemove.detach( l );
                connectedTo = to;
                if ( connectedTo != null )
                    connectedTo.node().notifRemove.attach( l );
                node().notifConnect.portConnected( this, connectedTo );
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
        public String name() {
            return "";
        }
    }

    public interface Sink {

        /** Перечень входных портов */
        Iterable<Port.Input> inputs();
    }

    private static class Ports<T extends Port> implements Iterable<T> {

        private final Notificator.PortRemoval not;
        private final Collection<T> ports = new ArrayList<T>();

        public Ports( PortRemoval not ) {
            this.not = not;
        }

        public boolean add( T port ) {
            return ports.add( port );
        }

        public boolean remove( T port ) {
            boolean r = ports.remove( port );
            if ( r )
                not.portRemoved( port );
            return r;
        }

        @Override
        public Iterator<T> iterator() {
            return ports.iterator();
        }
    }

    public static class Inputs extends Ports<Port.Input> {

        public Inputs( PortRemoval not ) {
            super( not );
        }
    }

    public static class Outputs extends Ports<Port.Output> {

        public Outputs( PortRemoval not ) {
            super( not );
        }
    }

    public interface Source {

        /** Перечень выходных портов */
        Iterable<Port.Output> outputs();
    }
    protected final Notificator.PortRemoval notifRemove = new Notificator.PortRemoval();
    private final Notificator.PortConnect notifConnect = new Notificator.PortConnect();

    /** Краткое имя компонента */
    public abstract String caption();
}
