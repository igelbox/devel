package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import ccs.rocky.runtime.Generatable;
import ccs.util.Iterabled;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author igel
 */
public class Timer extends Node implements Generatable {

    private final Port.Output output = new Port.Output.FixedState( "time", this, null, Port.State.SIGNAL );
    private final Iterable<Port.Output> outputs = new Iterabled.Element<Port.Output>( output );
    private final Generatable.Generator gen = new Generatable.Generator() {
        int s, t, sr;

        @Override
        public void gen_prolog( MethodVisitor mv, Locals locals, int samples, int samplerate ) {
            s = locals.sampleVar();
            t = locals.timeVar();
            sr = samplerate;
        }

        @Override
        public void gen_inloop( MethodVisitor mv, Port.Output out ) {
            mv.visitVarInsn( Opcodes.FLOAD, t );
            mv.visitLdcInsn( sr );
            mv.visitVarInsn( Opcodes.ILOAD, s );
            mv.visitMethodInsn( Opcodes.INVOKESTATIC, Type.getInternalName( Timer.class ), "_op", "(FII)F" );
        }
    };

    public Timer( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    @Override
    public Generator generator() {
        return gen;
    }

    public static float _op( int i ) {
        System.out.println( i );
        return 0;
    }

    public static float _op( float time, int samplerate, int sample ) {
        return time + (float) sample / (float) samplerate;
    }
}
