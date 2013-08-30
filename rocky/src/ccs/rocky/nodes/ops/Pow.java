package ccs.rocky.nodes.ops;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author igel
 */
@Node.Descr( caption = "^" )
public class Pow extends AbstractOp.Double {

    public Pow( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( getClass() ), "_op", "(FF)F" );
    }

    public static float _op( float a, float b ) {
        return (float) Math.pow( a, b );
    }
}
