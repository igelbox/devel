package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;

/**
 *
 * @author igel
 */
public class Div extends AbstractOp {

    private final Port.Input inputX = new Port.Input( this );
    private final Port.Input inputY = new Port.Input( this );

    public Div() {
        super( "x/y" );
        inputs.add( inputX );
        inputs.add( inputY );
    }

    public Port.Input inputX() {
        return inputX;
    }

    public Port.Input inputY() {
        return inputY;
    }
}
