package ccs.jrack.functions;

import ccs.jrack.Function;

/**
 *
 * @author igel
 */
public class Gain extends Function {

    private final Arg in, gain;

    public Gain( Arg in, Arg gain ) {
        this.in = in;
        this.gain = gain;
    }

    @Override
    public float apply() {
        return in.get() * gain.get();
    }
}
