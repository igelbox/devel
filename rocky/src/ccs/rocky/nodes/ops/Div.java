package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
public class Div extends AbstractOp {

    private static class Descr extends Descriptor<Div> {

        @Override
        public String caption() {
            return "div";
        }

        @Override
        public String tag() {
            return "div";
        }

        @Override
        public Div createNode( int id ) {
            return new Div( id, this );
        }

        @Override
        public Div loadNode( Loader loader ) {
            return new Div( this, loader );
        }
    }
    public static final Descriptor<Div> DESCRIPTOR = new Descr();
    private final Port.Input inputX = new Port.Input( 0, this );
    private final Port.Input inputY = new Port.Input( 1, this );

    public Div( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "/" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Div( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "/" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitInsn( Opcodes.FDIV );
    }
}
