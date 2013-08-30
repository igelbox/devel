package ccs.jrack.functions;

import ccs.jack.Client;
import ccs.jrack.Function;

/**
 *
 * @author igel
 */
public class Delay extends Function {

    private final Client client;
    private final Arg.Back in;
    private final Arg time;
    private final float k;

    public Delay( Client client, Arg.Back in, Arg time ) {
        this.client = client;
        k = client.sampleRate();
        this.in = in;
        this.time = time;
    }

    @Override
    public float apply() {
        return in.get( (int) (k * time.get()) );
    }
}
