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
public class Sin extends AbstractOp {

    private static class Descr extends Descriptor<Sin> {

        @Override
        public String caption() {
            return "sin";
        }

        @Override
        public String tag() {
            return "sin";
        }

        @Override
        public Sin createNode( int id ) {
            return new Sin( id, this );
        }

        @Override
        public Sin loadNode( Loader loader ) {
            return new Sin( this, loader );
        }
    }
    public static final Descriptor<Sin> DESCRIPTOR = new Descr();
    private final Port.Input input = new Port.Input( 0, this );

    public Sin( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "sin" );
        inputs.add( input );
    }

    public Sin( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "sin" );
        inputs.add( input );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( getClass() ), "_op", "(F)F" );
    }

    public static float _op( float x ) {
        return (float) Math.sin( x );
    }
}
