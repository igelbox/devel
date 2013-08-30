package ccs.rocky.core;

import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.util.Cloud;
import ccs.util.Iterabled;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Абстрактный узел графа обработки сигнала
 *
 * @author igel
 */
public abstract class Node {

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    public @interface Param {
    }

    public enum State {

        CONST, VAR, SIGNAL
    }

    public static abstract class Descriptor<T extends Node> {

        /** Краткое имя узла */
        public abstract String caption();

        public abstract String tag();

        public abstract T createNode( int id );

        public abstract T loadNode( Loader loader );

        public boolean system() {
            return false;
        }
    }

    public static abstract class Listener {

//        public enum PortOp {
//
//            ADD, DEL
//        }
//
//        protected void notifyPort( Port port, PortOp op ) {
//        }
        protected void notifyDelete() {
        }

        protected void notifyFlow() {
        }
    }
    private final int id;
    private final Descriptor<?> descriptor;
    protected final Cloud<Listener> listeners = new Cloud<Listener>();
    private State _state;

    public Node( int id, Descriptor<?> descriptor ) {
        this.id = id;
        this.descriptor = descriptor;
    }

    public Node( Descriptor<?> descriptor, Loader loader ) {
        this.id = loader.findAttribute( "id" ).asInt();
        this.descriptor = descriptor;
    }

    public int id() {
        return id;
    }

    public Descriptor<?> descriptor() {
        return descriptor;
    }

    /** Краткое имя узла */
    public String caption() {
        return descriptor.caption();
    }

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

    public State state() {
        if ( _state != null )
            return _state;
        State s = State.CONST;
        for ( Port.Input i : inputs() ) {
            Port.Output o = i.connected();
            if ( o == null )
                continue;
            State os = o.node().state();
            switch ( os ) {
                case VAR:
                    if ( s == State.CONST )
                        s = os;
                    break;
                case SIGNAL:
                    s = os;
                    break;
            }
        }
        _state = s;
        return s;
    }

    public Port findPortById( int id ) {
        for ( Port p : inputs() )
            if ( p.id() == id )
                return p;
        for ( Port p : outputs() )
            if ( p.id() == id )
                return p;
        throw new IllegalArgumentException( "Port not found" );
    }

    protected void notifyFlow() {
        _state = null;
        for ( Listener l : listeners )
            l.notifyFlow();
    }

    void notifyDelete() {
        for ( Listener l : listeners )
            l.notifyDelete();
    }
}
