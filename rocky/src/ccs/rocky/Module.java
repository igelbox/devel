package ccs.rocky;

import ccs.rocky.event.Notificator;
import ccs.rocky.ops.AbstractOp;
import ccs.rocky.ops.Neg;
import ccs.rocky.ops.Sum;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author igel
 */
public abstract class Module extends Node implements Iterable<Node> {

    private final Collection<Node> components = new ArrayList<Node>();
    private final Outputs inputs = new Outputs( notifRemove );
    private final Inputs outputs = new Inputs( notifRemove );

    public Module() {
        Port.Output in0 = new Port.Output( this );
        inputs.add( in0 );
        Port.Output in1 = new Port.Output( this );
        inputs.add( in1 );
        Port.Input out0 = new Port.Input( this );
        outputs.add( out0 );
        {//test
            AbstractOp c0 = new Neg();
            components.add( c0 );
            c0.inputs().iterator().next().connectTo( in0 );
            AbstractOp c1 = new Sum();
            components.add( c1 );
            Iterator<Node.Port.Input> i = c1.inputs().iterator();
            i.next().connectTo( c0.outputs().iterator().next() );
            i.next().connectTo( in1 );
            out0.connectTo( c1.outputs().iterator().next() );
        }
    }

    public Iterable<Port.Output> inputs() {
        return inputs;
    }

    public Iterable<Port.Input> outputs() {
        return outputs;
    }

    @Override
    public Iterator<Node> iterator() {
        return components.iterator();
    }
}
