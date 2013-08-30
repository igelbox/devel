package ccs.jrack.functions;

import ccs.jack.Client;
import ccs.jrack.Function;

/**
 *
 * @author igel
 */
public class Amplitude extends Function {

    private final Arg.Back in;
    private final Arg time;
    private final float k;

    public Amplitude( Client client, Arg.Back in, Arg time ) {
        k = client.sampleRate();
        this.in = in;
        this.time = time;
    }

    @Override
    public float apply() {
        int offs = (int) (k * time.get());
        float min = Float.MAX_VALUE, max = -min;
        for ( int i = offs; i >= 0; i-- ) {
            float v = in.get( i );
            if ( v < min )
                min = v;
            if ( v > max )
                max = v;
        }
        return (max - min) / 2.0f;
    }
}
