package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Normalize extends Filter {

    @Override
    public void filter( float[] buffer ) {
        float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
        for ( float s : buffer ) {
            min = Math.min( min, s );
            max = Math.max( max, s );
        }
        float ofs = (max + min) / 2.0f;
        float amp = max - min;
        if ( amp > 0.0f )
            for ( int i = buffer.length - 1; i >= 0; i-- )
                buffer[i] = (buffer[i] - ofs) / amp * 2.0f;
    }
}
