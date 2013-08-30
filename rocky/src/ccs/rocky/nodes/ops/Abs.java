package ccs.rocky.nodes.ops;

import ccs.rocky.core.NodeDescriptor;
import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;

/**
 *
 * @author igel
 */
public class Abs extends AbstractOp {

    private static class Descriptor extends NodeDescriptor<Abs> {

        @Override
        public String caption() {
            return "abs";
        }

        @Override
        public String tag() {
            return "abs";
        }

        @Override
        public Abs createNode() {
            return new Abs( 0, this );
        }

        @Override
        public Abs loadNode( Loader loader ) {
            return new Abs( 0, this );
        }
    }
    public static final NodeDescriptor<?> DESCRIPTOR = new Descriptor();
    private final Port.Input input = new Port.Input( this );

    public Abs( int id, NodeDescriptor<?> descriptor ) {
        super( id, descriptor, "|x|" );
        inputs.add( input );
    }

    public Port.Input input() {
        return input;
    }
}
