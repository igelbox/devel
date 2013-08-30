package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Gain extends Filter {

    private final float gain;

    public Gain( float gain ) {
        this.gain = gain;
    }

    @Override
    public void filter( float[] buffer ) {
        for ( int i = buffer.length - 1; i >= 0; i-- )
            buffer[i] = buffer[i] * gain;
    }
}
