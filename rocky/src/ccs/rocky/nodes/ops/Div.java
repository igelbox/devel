package ccs.rocky.nodes.ops;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
@Node.Descr( caption = "/" )
public class Div extends AbstractOp.Double {

    public Div( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitInsn( Opcodes.FDIV );
    }
}
