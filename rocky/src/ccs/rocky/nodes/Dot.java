package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.NodeDescriptor;
import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import ccs.util.Iterabled;

/**
 *
 * @author igel
 */
public class Dot extends Node {

    public static class Descriptor extends NodeDescriptor<Dot> {

        @Override
        public String caption() {
            return "dot";
        }

        @Override
        public String tag() {
            return "dot";
        }

        @Override
        public Dot createNode() {
            return new Dot();
        }

        @Override
        public Dot loadNode( Loader loader ) {
            return new Dot();
        }
    }
    private final Port.Output output = new Port.Output( this );
    private final Iterable<Port.Output> outputs = new Iterabled.Element<Port.Output>( output );
    private final Port.Input input = new Port.Input( this );
    private final Iterable<Port.Input> inputs = new Iterabled.Element<Port.Input>( input );

    @Override
    public String caption() {
        return ".";
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
