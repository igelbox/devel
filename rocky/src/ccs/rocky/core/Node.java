package ccs.rocky.core;

import ccs.rocky.persistent.Storer;
import ccs.util.Cloud;
import ccs.util.Iterabled;

/**
 * Абстрактный узел графа обработки сигнала
 *
 * @author igel
 */
public abstract class Node {

    public static abstract class Listener {

        public enum PortOp {

            ADD, DEL
        }

        protected void notifyPort( Port port, PortOp op ) {
        }
    }
    private int id;
    private final NodeDescriptor<?> descriptor;
    protected final Cloud<Listener> listeners = new Cloud<Listener>();

    public Node( int id, NodeDescriptor<?> descriptor ) {
        this.id = id;
        this.descriptor = descriptor;
    }

    /** Краткое имя узла */
    public abstract String caption();

    /** Перечень входных портов */
    public Iterable<Port.Input> inputs() {
        return Iterabled.emptyIterable();
    }

    /** Перечень выходных портов */
    public Iterable<Port.Output> outputs() {
        return Iterabled.emptyIterable();
    }

    public boolean listen( Listener listener ) {
        return listeners.add( listener );
    }

    public boolean unlisten( Listener listener ) {
        return listeners.remove( listener );
    }

    public void store( Storer storer ) {
    }
}
