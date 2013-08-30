package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
public class Mod extends AbstractOp {

    private static class Descr extends Descriptor<Mod> {

        @Override
        public String caption() {
            return "mod";
        }

        @Override
        public String tag() {
            return "mod";
        }

        @Override
        public Mod createNode( int id ) {
            return new Mod( id, this );
        }

        @Override
        public Mod loadNode( Loader loader ) {
            return new Mod( this, loader );
        }
    }
    public static final Descriptor<Mod> DESCRIPTOR = new Descr();
    private final Port.Input inputX = new Port.Input( 0, this );
    private final Port.Input inputY = new Port.Input( 1, this );

    public Mod( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "mod" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Mod( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "mod" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitInsn( Opcodes.FREM );
    }
}
