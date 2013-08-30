package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;

/**
 *
 * @author igel
 */
public class Inv extends AbstractOp {

    private static class Descr extends Descriptor<Inv> {

        @Override
        public String caption() {
            return "inv";
        }

        @Override
        public String tag() {
            return "inv";
        }

        @Override
        public Inv createNode( int id ) {
            return new Inv( id, this );
        }

        @Override
        public Inv loadNode( Loader loader ) {
            return new Inv( this, loader );
        }
    }
    public static final Descriptor<Inv> DESCRIPTOR = new Descr();
    private final Port.Input input = new Port.Input( 0, this );

    public Inv( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "inv" );
        inputs.add( input );
    }

    public Inv( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "inv" );
        inputs.add( input );
    }

    public Port.Input input() {
        return input;
    }
}
