package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author igel
 */
public class Abs extends AbstractOp {

    private static class Descr extends Descriptor<Abs> {

        @Override
        public String caption() {
            return "abs";
        }

        @Override
        public String tag() {
            return "abs";
        }

        @Override
        public Abs createNode( int id ) {
            return new Abs( id, this );
        }

        @Override
        public Abs loadNode( Loader loader ) {
            return new Abs( this, loader );
        }
    }
    public static final Descriptor<Abs> DESCRIPTOR = new Descr();
    private final Port.Input input = new Port.Input( 0, this );

    public Abs( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "|x|" );
        inputs.add( input );
    }

    public Abs( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "|x|" );
        inputs.add( input );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( Math.class ), "abs", "(F)F" );
    }
}
