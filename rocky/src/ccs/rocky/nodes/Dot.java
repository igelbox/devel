package ccs.rocky.nodes;

import ccs.rocky.core.Port.Output;
import ccs.rocky.nodes.ops.AbstractOp;
import ccs.rocky.persistent.Loader;
import ccs.rocky.runtime.Generatable;
import ccs.rocky.views.DotNodeView;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author igel
 */
public class Dot extends AbstractOp.Single implements Generatable {

    public Dot( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Output out ) {
    }

    @Override
    protected View createView() {
        return new DotNodeView( this );
    }
}
