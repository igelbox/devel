package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;

/**
 *
 * @author igel
 */
public class Neg extends AbstractOp {

    private final Port.Input input = new Port.Input( this );

    public Neg() {
        super( "-x" );
        inputs.add( input );
    }

    public Port.Input input() {
        return input;
    }
}
