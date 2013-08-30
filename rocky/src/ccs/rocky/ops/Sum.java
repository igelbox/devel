package ccs.rocky.ops;

/**
 *
 * @author igel
 */
public class Sum extends AbstractOp {

    public Sum() {
        super( "x+y" );
        inputs.add( new Port.Input( this ) );
        inputs.add( new Port.Input( this ) );
    }
}
