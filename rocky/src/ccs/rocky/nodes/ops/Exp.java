package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;

/**
 *
 * @author igel
 */
public class Exp extends AbstractOp {

    private static class Descr extends Descriptor<Exp> {

        @Override
        public String caption() {
            return "exp";
        }

        @Override
        public String tag() {
            return "exp";
        }

        @Override
        public Exp createNode( int id ) {
            return new Exp( id, this );
        }

        @Override
        public Exp loadNode( Loader loader ) {
            return new Exp( this, loader );
        }
    }
    public static final Descriptor<Exp> DESCRIPTOR = new Descr();
    private final Port.Input input = new Port.Input( 0, this );

    public Exp( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "exp" );
        inputs.add( input );
    }

    public Exp( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "exp" );
        inputs.add( input );
    }

    public Port.Input input() {
        return input;
    }
}
