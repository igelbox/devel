package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
public class Sum extends AbstractOp {

    private static class Descr extends Descriptor<Sum> {

        @Override
        public String caption() {
            return "sum";
        }

        @Override
        public String tag() {
            return "sum";
        }

        @Override
        public Sum createNode( int id ) {
            return new Sum( id, this );
        }

        @Override
        public Sum loadNode( Loader loader ) {
            return new Sum( this, loader );
        }
    }
    public static final Descriptor<Sum> DESCRIPTOR = new Descr();
    private final Port.Input inputX = new Port.Input( 0, this );
    private final Port.Input inputY = new Port.Input( 1, this );

    public Sum( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "+" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Sum( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "+" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Port.Input inputX() {
        return inputX;
    }

    public Port.Input inputY() {
        return inputY;
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Output out ) {
        mv.visitInsn( Opcodes.FADD );
    }
}
