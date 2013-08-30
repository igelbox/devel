package ccs.rocky.nodes.ops;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port.Output;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
@Node.Descr( caption = "+" )
public class Sum extends AbstractOp.Double {

    public Sum( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Output out ) {
        mv.visitInsn( Opcodes.FADD );
    }
}
