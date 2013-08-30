package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Echo extends Filter {

    private final float[] last = new float[1024];
    private final int samples;

    public Echo( int samples ) {
        this.samples = samples;
    }

    private float get( float[] buffer, int idx ) {
        return idx >= 0 ? buffer[idx] : last[last.length + idx];
    }

    @Override
    public void filter( float[] buffer ) {
        float[] out = new float[buffer.length];
        for ( int i = 0; i < buffer.length; i++ ) {
            float v = 2.0f * buffer[i] + get( buffer, i - samples );
            out[i] = v / 3.0f;
        }
//        for ( int i = 0; i < buffer.length; i++ ) {
//            float v = buffer[i];
//            for ( int j = 0; j < samples; j++ )
//                v += get( buffer, i - (1 << j) );
//            out[i] = v / 11.0f;
//        }
        System.arraycopy( buffer, 0, last, 0, buffer.length );
        System.arraycopy( out, 0, buffer, 0, buffer.length );
    }
}
