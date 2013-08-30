package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author igel
 */
public class Sig extends AbstractOp {

    private static class Descr extends Descriptor<Sig> {

        @Override
        public String caption() {
            return "sig";
        }

        @Override
        public String tag() {
            return "signum";
        }

        @Override
        public Sig createNode( int id ) {
            return new Sig( id, this );
        }

        @Override
        public Sig loadNode( Loader loader ) {
            return new Sig( this, loader );
        }
    }
    public static final Descriptor<Sig> DESCRIPTOR = new Descr();
    private final Port.Input input = new Port.Input( 0, this );

    public Sig( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "sg" );
        inputs.add( input );
    }

    public Sig( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "sg" );
        inputs.add( input );
    }

    public Port.Input input() {
        return input;
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( Math.class ), "signum", "(F)F" );
    }
}
