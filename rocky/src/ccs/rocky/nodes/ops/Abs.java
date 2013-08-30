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
@Node.Descr( caption = "|x|" )
public class Abs extends AbstractOp.Single {

    public Abs( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( Math.class ), "abs", "(F)F" );
    }
}
