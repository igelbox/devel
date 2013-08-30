package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Compressor extends Filter {

    private final float[] last = new float[4096];

    private float get( float[] buffer, int idx ) {
        return idx >= 0 ? buffer[idx] : last[last.length + idx];
    }

    @Override
    public void filter( float[] buffer ) {
        float[] out = new float[buffer.length];
        for ( int i = 0; i < buffer.length; i++ ) {
            float min = Float.MAX_VALUE, max = Float.MIN_VALUE;
            for ( int j = i - 256; j <= i; j++ ) {
                float v = get( buffer, j );
                min = Math.min( min, v );
                max = Math.max( max, v );
            }
            float ofs = (max + min) / 2.0f;
            float amp = max - min;
            out[i] = (buffer[i] - ofs) / amp * 2.0f;
        }
        System.arraycopy( buffer, buffer.length, last, 0, buffer.length );
        System.arraycopy( out, 0, buffer, 0, buffer.length );
    }
}
