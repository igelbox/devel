package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Input;
import ccs.rocky.core.Port.Output;
import ccs.rocky.core.utils.Ports;
import ccs.rocky.persistent.Loader;

/**
 *
 * @author igel
 */
public class Test extends Node {

    private final Ports<Port.Input> inputs = new Ports<Port.Input>();
    private final Ports<Port.Output> outputs = new Ports<Port.Output>();

    public Test( String id, Loader loader ) {
        super( id, loader );
        inputs.add( new Input( "i1", this, "" ) );
        inputs.add( new Input( "i2", this, "B" ) );
        inputs.add( new Input( "i3", this, null ) );
        inputs.add( new Input( "i4", this, "Dd" ) );
        outputs.add( new Output( "o1", this, "" ) );
        outputs.add( new Output( "o2", this, "B" ) );
        outputs.add( new Output( "o3", this, null ) );
        outputs.add( new Output( "o4", this, "XxxX" ) );
    }

    @Override
    public Iterable<Input> inputs() {
        return inputs;
    }

    @Override
    public Iterable<Output> outputs() {
        return outputs;
    }
}
