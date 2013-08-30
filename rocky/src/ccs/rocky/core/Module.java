package ccs.rocky.core;

import ccs.rocky.core.Port.Input;
import ccs.rocky.core.Port.Output;
import ccs.rocky.core.utils.Ports;
import ccs.rocky.nodes.Dot;
import ccs.rocky.nodes.NodesFactory;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.util.Cloud;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author igel
 */
public class Module implements Iterable<Node> {

    public static abstract class Listener {

        public enum NodeOp {

            ADD, DEL
        }

        protected void node( Node node, NodeOp op ) {
        }

        protected void flow( Module module ) {
        }
    }

    public static class In extends Dot {

        private static class Descr extends Node.Descriptor<In> {

            @Override
            public String caption() {
                return "in";
            }

            @Override
            public String tag() {
                return "in";
            }

            @Override
            public In createNode( int id ) {
                return new In( id, this );
            }

            @Override
            public In loadNode( Loader loader ) {
                return new In( this, loader );
            }
        }
        public static final Descriptor<In> DESCRIPTOR = new Descr();

        public In( int id, Descriptor<?> descriptor ) {
            super( id, descriptor );
        }

        public In( Descriptor<?> descriptor, Loader loader ) {
            super( descriptor, loader );
        }

        @Override
        public String caption() {
            return ">>";
        }

        @Override
        public State state() {
            return State.SIGNAL;
        }
    }

    public static class Out extends Dot {

        private static class Descr extends Node.Descriptor<Out> {

            @Override
            public String caption() {
                return "out";
            }

            @Override
            public String tag() {
                return "out";
            }

            @Override
            public Out createNode( int id ) {
                return new Out( id, this );
            }

            @Override
            public Out loadNode( Loader loader ) {
                return new Out( this, loader );
            }
        }
        public static final Descriptor<Out> DESCRIPTOR = new Descr();

        public Out( int id, Descriptor<?> descriptor ) {
            super( id, descriptor );
        }

        public Out( Descriptor<?> descriptor, Loader loader ) {
            super( descriptor, loader );
        }

        @Override
        public String caption() {
            return ">>";
        }
    }
    private final Node.Listener nodeListener = new Node.Listener() {

        @Override
        protected void notifyFlow() {
            for ( Listener l : listeners )
                l.flow( Module.this );
        }
    };
    private final Cloud<Listener> listeners = new Cloud<Listener>();
    private final Collection<Node> nodes = new ArrayList<Node>();
    protected final Ports<Port.Input> inputs = new Ports<Port.Input>();
    protected final Ports<Port.Output> outputs = new Ports<Port.Output>();
    private int idGen;

    public Module() {
    }

    private static Port getPort( int id, Iterable<? extends Port> ports ) {
        for ( Port p : ports )
            if ( id == p.id() )
                return p;
        throw new IllegalArgumentException();
    }

    private Port getPort( int nid, int pid, boolean isin ) {
        for ( Node n : nodes )
            if ( nid == n.id() )
                return getPort( pid, isin ? n.inputs() : n.outputs() );
        throw new IllegalArgumentException();
    }

    public void load( Loader loader ) {
        //nodes
        Loader.Attribute anodes = loader.findAttribute( "nodes" );
        if ( anodes != null )
            for ( Loader.Attribute a : anodes.asLoader() ) {
                Loader l = a.asLoader();
                Node.Descriptor<?> d = NodesFactory.findDescriptor( a.name );
                Node n = d.loadNode( l );
                idGen = Math.max( idGen, n.id() + 1 );
                nodes.add( n );
                if ( n instanceof In )
                    inputs.add( ((In) n).input() );
                else if ( n instanceof Out )
                    outputs.add( ((Out) n).output() );
                n.listen( nodeListener );
            }
        //links
        Loader.Attribute alinks = loader.findAttribute( "links" );
        if ( alinks != null )
            for ( Loader.Attribute a : alinks.asLoader() )
                if ( "link".equals( a.name ) ) {
                    Loader l = a.asLoader();
                    int snid = l.findAttribute( "sn" ).asInt();
                    int spid = l.findAttribute( "sp" ).asInt();
                    int dnid = l.findAttribute( "dn" ).asInt();
                    int dpid = l.findAttribute( "dp" ).asInt();
                    Port.Output from = (Port.Output) getPort( snid, spid, false );
                    Port.Input to = (Port.Input) getPort( dnid, dpid, true );
                    to.connect( from );
                }
    }

    public int genId() {
        return idGen++;
    }

    public void add( Node node ) {
        if ( !nodes.add( node ) )
            return;
        idGen = Math.max( idGen, node.id() + 1 );
        for ( Listener l : listeners )
            l.node( node, Listener.NodeOp.ADD );
        if ( node instanceof In )
            inputs.add( ((In) node).input() );
        else if ( node instanceof Out )
            outputs.add( ((Out) node).output() );
        node.listen( nodeListener );
    }

    public void remove( Node node ) {
        if ( node.descriptor().system() )
            return;
        if ( !nodes.remove( node ) )
            return;
//        for ( Port p : Iterabled.multi( node.inputs(), node.outputs() ) )
//            for ( Node.Listener l : node.listeners )
//                l.notifyPort( p, Node.Listener.PortOp.DEL );
        for ( Listener l : listeners )
            l.node( node, Listener.NodeOp.DEL );
        node.notifyDelete();
        if ( node instanceof In )
            inputs.remove( ((In) node).input() );
        else if ( node instanceof Out )
            outputs.remove( ((Out) node).output() );
        node.unlisten( nodeListener );
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    public boolean listen( Listener l ) {
        return listeners.add( l );
    }

    public void store( Storer storer ) {
        storer.putInt( "version", 0 );
        //nodes
        Storer ns = storer.put( "nodes" );
        for ( Node n : nodes ) {
            Node.Descriptor<?> d = n.descriptor();
            if ( d.system() )
                continue;
            Storer s = ns.put( d.tag() );
            s.putInt( "id", n.id() );
            n.store( s );
        }
        //links
        Storer ls = storer.put( "links" );
        for ( Node n : nodes )
            for ( Input i : n.inputs() ) {
                Output o = i.connected();
                if ( o != null ) {
                    Storer s = ls.put( "link" );
                    s.putInt( "sn", o.node().id() );
                    s.putInt( "sp", o.id() );
                    s.putInt( "dn", i.node().id() );
                    s.putInt( "dp", i.id() );
                }
            }
    }

    public Node findNodeById( int id ) {
        for ( Node n : nodes )
            if ( n.id() == id )
                return n;
        throw new IllegalArgumentException( "No nodes found" );
    }
}
