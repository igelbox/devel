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
public class Log extends AbstractOp {

    private static class Descr extends Descriptor<Log> {

        @Override
        public String caption() {
            return "log";
        }

        @Override
        public String tag() {
            return "log";
        }

        @Override
        public Log createNode( int id ) {
            return new Log( id, this );
        }

        @Override
        public Log loadNode( Loader loader ) {
            return new Log( this, loader );
        }
    }
    public static final Descriptor<Log> DESCRIPTOR = new Descr();
    private final Port.Input input = new Port.Input( 0, this );

    public Log( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "log" );
        inputs.add( input );
    }

    public Log( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "log" );
        inputs.add( input );
    }

    @Override
    protected void gen_inloop( MethodVisitor mv, Port.Output out ) {
        mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( getClass() ), "_op", "(F)F" );
    }

    public static float _op( float x ) {
        return (float) Math.log( x );
    }
}
