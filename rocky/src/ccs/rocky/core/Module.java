package ccs.rocky.core;

import ccs.rocky.core.utils.Ports;
import ccs.rocky.nodes.Dot;
import ccs.rocky.nodes.ops.Neg;
import ccs.rocky.nodes.ops.Sum;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.util.Cloud;
import ccs.util.Iterabled;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author igel
 */
public class Module extends Node implements Iterable<Node> {

    private static class Descriptor extends NodeDescriptor<Module> {

        @Override
        public String caption() {
            return "module";
        }

        @Override
        public String tag() {
            return "module";
        }

        @Override
        public Module createNode() {
            return new Module( 0, this );
        }

        @Override
        public Module loadNode( Loader loader ) {
            return new Module( 0, this );
        }
    }
    public static final NodeDescriptor<?> DESCRIPTOR = new Descriptor();

    public static abstract class Listener {

        public enum NodeOp {

            ADD, DEL
        }

        protected void node( Node node, NodeOp op ) {
        }
    }

    public static class In extends Dot {
    }

    public static class Out extends Dot {
    }
    private final Cloud<Listener> listeners = new Cloud<Listener>();
    private final Collection<Node> nodes = new ArrayList<Node>();
    private final Ports<Port.Input> inputs = new Ports<Port.Input>();
    private final Ports<Port.Output> outputs = new Ports<Port.Output>();

    public Module( int id, NodeDescriptor<?> descriptor ) {
        super( id, descriptor );
        In in0 = new In();
        inputs.add( in0.input() );
        nodes.add( in0 );
        In in1 = new In();
        inputs.add( in1.input() );
        nodes.add( in1 );
        Out out = new Out();
        outputs.add( out.output() );
        nodes.add( out );
        Neg n = new Neg();
        nodes.add( n );
        n.input().connect( in0.output() );
        Sum s = new Sum();
        s.inputX().connect( n.output() );
        s.inputY().connect( in1.output() );
        nodes.add( s );
        Dot d0 = new Dot();
        nodes.add( d0 );
        d0.input().connect( s.output() );
        Dot d1 = new Dot();
        nodes.add( d1 );
        d1.input().connect( d0.output() );
        out.input().connect( d1.output() );
    }

    public void add( Node node ) {
        if ( nodes.add( node ) )
            for ( Listener l : listeners )
                l.node( node, Listener.NodeOp.ADD );
    }

    public void remove( Node node ) {
        if ( nodes.remove( node ) ) {
            for ( Port p : Iterabled.multi( node.inputs(), node.outputs() ) )
                for ( Node.Listener l : node.listeners )
                    l.notifyPort( p, Node.Listener.PortOp.DEL );
            for ( Listener l : listeners )
                l.node( node, Listener.NodeOp.DEL );
        }
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    @Override
    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    @Override
    public String caption() {
        return "Module";
    }

    public boolean listen( Listener l ) {
        return listeners.add( l );
    }

    @Override
    public void store( Storer storer ) {
        for ( Node n : nodes ) {
            Storer s = storer.createStorer();
        }
//            storer.store( n.getClass().getSimpleName().toLowerCase(), n );
    }
}
