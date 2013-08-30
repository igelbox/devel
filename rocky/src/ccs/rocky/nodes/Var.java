package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
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

    private final Port.Output output = new Port.Output.FixedState( "out", this, null, Port.State.VAR );
    private final Iterable<Port.Output> outputs = new Iterabled.Element<Port.Output>( output );
    private final Generatable.Generator gen = new Generator.Fieldable() {
        int id;

        @Override
        public void gen_prolog( MethodVisitor mv, Locals locals, int samples, int samplerate ) {
            pushNode( mv, locals, Var.this );
            id = locals.newVar();
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, Type.getInternalName( Var.class ), "value", "()F" );
            mv.visitVarInsn( Opcodes.FSTORE, id );
        }

        @Override
        public void gen_inloop( MethodVisitor mv, Port.Output out ) {
            mv.visitVarInsn( Opcodes.FLOAD, id );
        }
    };
    private float value;

    public Var( String id, Loader loader ) {
        super( id, loader );
        Loader.Attribute a = loader.findAttribute( "value" );
        if ( a != null )
            this.value = a.asFloat();
    }

    @Override
    public String caption() {
        return Float.toString( value );
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
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
        super.store( storer );
        if ( value != 0 )
            storer.putFloat( "value", value );
    }

    @Override
    public Generator generator() {
        return gen;
    }
}
