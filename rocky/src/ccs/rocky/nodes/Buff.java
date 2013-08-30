package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.core.utils.Ports;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.rocky.runtime.Generatable;
import ccs.util.Iterabled;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author igel
 */
public class Buff extends Node implements Generatable {

    private static class Descr extends Descriptor<Buff> {

        @Override
        public String caption() {
            return "B";
        }

        @Override
        public String tag() {
            return "buff";
        }

        @Override
        public Buff createNode( int id ) {
            return new Buff( id, this );
        }

        @Override
        public Buff loadNode( Loader loader ) {
            return new Buff( this, loader );
        }
    }
    public static final Descriptor<Buff> DESCRIPTOR = new Descr();
    private final Port.Input input = new Port.Input( 0, this );
    private final Iterable<Port.Input> inputs = new Iterabled.Element<Port.Input>( input );
    private final Ports<Port.Output> outputs = new Ports<Port.Output>();
    private final Port.Output max = new Port.Output( 0, this );
    private final Port.Output min = new Port.Output( 1, this );
    private final Generatable.Generator gen = new Generatable.Generator.Fieldable() {
        @Override
        public void gen_inloop( MethodVisitor mv, Output out ) {
        }
    };
    private float delay;

    public Buff( int id, Descriptor<?> descriptor ) {
        super( id, descriptor );
        outputs.add( max );
        outputs.add( min );
    }

    public Buff( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader );
        Loader.Attribute a = loader.findAttribute( "value" );
        if ( a != null )
            this.delay = a.asFloat();
        outputs.add( max );
        outputs.add( min );
    }

    @Override
    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    public Port.Output max() {
        return max;
    }

    public Port.Output min() {
        return min;
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    @Param
    public float delay() {
        return delay;
    }

    public void delay( float delay ) {
        this.delay = delay;
        notifyFlow();
    }

    @Override
    public void store( Storer storer ) {
        storer.putFloat( "delay", delay );
    }

    @Override
    public Generator generator() {
        return gen;
    }
}
