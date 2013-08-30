package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
public class Sub extends AbstractOp {

    private static class Descr extends Descriptor<Sub> {

        @Override
        public String caption() {
            return "sub";
        }

        @Override
        public String tag() {
            return "sub";
        }

        @Override
        public Sub createNode( int id ) {
            return new Sub( id, this );
        }

        @Override
        public Sub loadNode( Loader loader ) {
            return new Sub( this, loader );
        }
    }
    public static final Descriptor<Sub> DESCRIPTOR = new Descr();
    private final Port.Input inputX = new Port.Input( 0, this );
    private final Port.Input inputY = new Port.Input( 1, this );

    public Sub( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "-" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Sub( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "-" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitInsn( Opcodes.FSUB );
    }
}
