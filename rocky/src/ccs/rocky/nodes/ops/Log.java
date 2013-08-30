package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;

/**
 *
 * @author igel
 */
public class Log extends AbstractOp {

    private final Port.Input input = new Port.Input( this );

    public Log() {
        super( "lg" );
        inputs.add( input );
    }

    public Port.Input input() {
        return input;
    }
}
