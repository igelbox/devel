package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
public class Mod extends AbstractOp.Double {

    public Mod( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitInsn( Opcodes.FREM );
    }
}
