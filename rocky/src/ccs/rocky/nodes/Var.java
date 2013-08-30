package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.rocky.runtime.Generatable;
import ccs.util.Iterabled;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author igel
 */
public class Var extends Node implements Generatable {

    private static class Descr extends Descriptor<Var> {

        @Override
        public String caption() {
            return "V";
        }

        @Override
        public String tag() {
            return "var";
        }

        @Override
        public Var createNode( int id ) {
            return new Var( id, this );
        }

        @Override
        public Var loadNode( Loader loader ) {
            return new Var( this, loader );
        }
    }
    public static final Descriptor<Var> DESCRIPTOR = new Descr();
    private final Port.Output output = new Port.Output( 0, this );
    private final Generatable.Generator gen = new Generator.Fieldable() {
        int id;

        @Override
        public void gen_prolog( MethodVisitor mv, Locals locals, int samples, int samplerate ) {
            id = locals.newVar();
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, Type.getInternalName( Var.class ), "value", "()F" );
            mv.visitVarInsn( Opcodes.FSTORE, id );
        }

        @Override
        public void gen_inloop( MethodVisitor mv, Output out ) {
            mv.visitVarInsn( Opcodes.FLOAD, id );
        }
    };
    private float value;

    public Var( int id, Descriptor<?> descriptor ) {
        super( id, descriptor );
    }

    public Var( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader );
        Loader.Attribute a = loader.findAttribute( "value" );
        if ( a != null )
            this.value = a.asFloat();
    }

    public Port.Output output() {
        return output;
    }

    @Override
    public Iterable<Output> outputs() {
        return new Iterabled.Element<Port.Output>( output );
    }

    @Param
    public float value() {
        return value;
    }

    public void value( float value ) {
        this.value = value;
    }

    @Override
    public void store( Storer storer ) {
        storer.putFloat( "value", value );
    }

    @Override
    public State state() {
        return State.VAR;
    }

    @Override
    public Generator generator() {
        return gen;
    }
}
