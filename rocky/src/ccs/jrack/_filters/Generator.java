package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Generator extends Filter {

    private final float freq;

    public Generator( float freq ) {
        this.freq = freq;
    }

    @Override
    public void filter( float[] buffer ) {
        for ( int i = buffer.length - 1; i >= 0; i-- )
            buffer[i] = (float) Math.sin( (float) i / (float) buffer.length * freq );
    }
}
