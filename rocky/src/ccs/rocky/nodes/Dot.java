package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import ccs.util.Iterabled;

/**
 *
 * @author igel
 */
public class Dot extends Node {

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
}
