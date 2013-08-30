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
public class Pow extends AbstractOp {

    private static class Descr extends Descriptor<Pow> {

        @Override
        public String caption() {
            return "pow";
        }

        @Override
        public String tag() {
            return "pow";
        }

        @Override
        public Pow createNode( int id ) {
            return new Pow( id, this );
        }

        @Override
        public Pow loadNode( Loader loader ) {
            return new Pow( this, loader );
        }
    }
    public static final Descriptor<Pow> DESCRIPTOR = new Descr();
    private final Port.Input inputX = new Port.Input( 0, this );
    private final Port.Input inputY = new Port.Input( 1, this );

    public Pow( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "^" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Pow( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "^" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( getClass() ), "_op", "(FF)F" );
    }

    public static float _op( float a, float b ) {
        return (float) Math.pow( a, b );
    }
}
