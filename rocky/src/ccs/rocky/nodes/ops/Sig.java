package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;

/**
 *
 * @author igel
 */
public class Sig extends AbstractOp {

    private final Port.Input input = new Port.Input( this );

    public Sig() {
        super( "sg" );
        inputs.add( input );
    }

    public Port.Input input() {
        return input;
    }
}
