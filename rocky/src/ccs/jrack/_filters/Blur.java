package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Blur extends Filter {

    private final float[] last = new float[1024];
    private final int radius;

    public Blur( int radius ) {
        this.radius = radius;
    }

    private float get( float[] buffer, int idx ) {
        return idx >= 0 ? buffer[idx] : last[last.length + idx];
    }

    @Override
    public void filter( float[] buffer ) {
        float[] out = new float[buffer.length];
        for ( int i = 0; i < buffer.length; i++ ) {
            float v = buffer[i];
            for ( int j = 1; j < radius; j++ )
                v += get( buffer, i - j );
            out[i] = v / (float) radius;
        }
        System.arraycopy( buffer, 0, last, 0, buffer.length );
        System.arraycopy( out, 0, buffer, 0, buffer.length );
    }
}
