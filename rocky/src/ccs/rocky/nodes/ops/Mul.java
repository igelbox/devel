package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;

/**
 *
 * @author igel
 */
public class Mul extends AbstractOp {

    private static class Descr extends Descriptor<Mul> {

        @Override
        public String caption() {
            return "mul";
        }

        @Override
        public String tag() {
            return "mul";
        }

        @Override
        public Mul createNode( int id ) {
            return new Mul( id, this );
        }

        @Override
        public Mul loadNode( Loader loader ) {
            return new Mul( this, loader );
        }
    }
    public static final Descriptor<Mul> DESCRIPTOR = new Descr();
    private final Port.Input inputX = new Port.Input( 0, this );
    private final Port.Input inputY = new Port.Input( 1, this );

    public Mul( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "x" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Mul( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "x" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Port.Input inputX() {
        return inputX;
    }

    public Port.Input inputY() {
        return inputY;
    }
}
