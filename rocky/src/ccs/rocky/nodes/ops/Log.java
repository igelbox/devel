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
public class Log extends AbstractOp.Single {

    public Log( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( getClass() ), "_op", "(F)F" );
    }

    public static float _op( float x ) {
        return (float) Math.log( x );
    }
}
