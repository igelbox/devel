package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port.Output;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author igel
 */
public class Sig extends AbstractOp.Single {

    public Sig( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( Math.class ), "signum", "(F)F" );
    }
}
