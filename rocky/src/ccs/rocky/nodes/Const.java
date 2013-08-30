package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.rocky.runtime.Generatable;
import ccs.util.Iterabled;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author igel
 */
public class Const extends Node implements Generatable {

    private static class Descr extends Descriptor<Const> {

        @Override
        public String caption() {
            return "C";
        }

        @Override
        public String tag() {
            return "const";
        }

        @Override
        public Const createNode( int id ) {
            return new Const( id, this );
        }

        @Override
        public Const loadNode( Loader loader ) {
            return new Const( this, loader );
        }
    }
    public static final Descriptor<Const> DESCRIPTOR = new Descr();
    private final Port.Output output = new Port.Output( 0, this );
    private final Generatable.Generator gen = new Generator() {
        @Override
        public void gen_inloop( MethodVisitor mv, Output out ) {
            mv.visitLdcInsn( value );
        }
    };
    private float value;

    public Const( int id, Descriptor<?> descriptor ) {
        super( id, descriptor );
    }

    public Const( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader );
        Loader.Attribute a = loader.findAttribute( "value" );
        if ( a != null )
            this.value = a.asFloat();
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
        notifyFlow();
    }

    @Override
    public void store( Storer storer ) {
        storer.putFloat( "value", value );
    }

    @Override
    public Generator generator() {
        return gen;
    }
}
