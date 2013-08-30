package ccs.rocky.ops;

import ccs.rocky.Node;
import ccs.rocky.Node.Port.Input;

/**
 *
 * @author igel
 */
public abstract class AbstractOp extends Node implements Node.Source, Node.Sink {

    private final String caption;
    private final Outputs outputs = new Outputs( notifRemove );
    protected final Inputs inputs = new Inputs( notifRemove );

    public AbstractOp( String caption ) {
        this.caption = caption;
        outputs.add( new Port.Output( this ) );
    }

    @Override
    public final String caption() {
        return caption;
    }

    @Override
    public Iterable<Input> inputs() {
        return inputs;
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
    }
}
