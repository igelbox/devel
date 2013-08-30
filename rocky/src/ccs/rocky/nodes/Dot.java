package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.persistent.Loader;
import ccs.rocky.runtime.Generatable;
import ccs.util.Iterabled;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author igel
 */
public class Dot extends Node implements Generatable {

    private static class Descr extends Descriptor<Dot> {

        @Override
        public String caption() {
            return "dot";
        }

        @Override
        public String tag() {
            return "dot";
        }

        @Override
        public Dot createNode( int id ) {
            return new Dot( id, this );
        }

        @Override
        public Dot loadNode( Loader loader ) {
            return new Dot( this, loader );
        }
    }
    public static final Descriptor<Dot> DESCRIPTOR = new Descr();
    private static final Generator GEN = new Generator() {
        @Override
        public void gen_inloop( MethodVisitor mv, Output out ) {
        }
    };
    private final Port.Output output = new Port.Output( 0, this );
    private final Iterable<Port.Output> outputs = new Iterabled.Element<Port.Output>( output );
    private final Port.Input input = new Port.Input( 0, this );
    private final Iterable<Port.Input> inputs = new Iterabled.Element<Port.Input>( input );

    public Dot( int id, Descriptor<?> descriptor ) {
        super( id, descriptor );
    }

    public Dot( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader );
    }

    @Override
    public String caption() {
        return "@";
    }

    @Override
    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    public Port.Input input() {
        return input;
    }

    public Port.Output output() {
        return output;
    }

    @Override
    public Generator generator() {
        return GEN;
    }
}
