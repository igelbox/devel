package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Clamper extends Filter {

    private final float min, max;

    public Clamper( float min, float max ) {
        this.min = min;
        this.max = max;
    }

    public Clamper( float amp ) {
        this.min = -amp / 2.0f;
        this.max = amp / 2.0f;
    }

    @Override
    public void filter( float[] buffer ) {
        for ( int i = buffer.length - 1; i >= 0; i-- )
            buffer[i] = Math.min( max, Math.max( min, buffer[i] ) );
    }
}
