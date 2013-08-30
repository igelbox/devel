package ccs.rocky.ops;

/**
 *
 * @author igel
 */
public class Neg extends AbstractOp {

    public Neg() {
        super( "-x" );
        inputs.add( new Port.Input( this ) );
    }
}
