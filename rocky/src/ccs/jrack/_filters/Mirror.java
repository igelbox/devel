package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Mirror extends Filter {

    private final float limit;

    public Mirror( float limit ) {
        this.limit = limit;
    }

    @Override
    public void filter( float[] buffer ) {
        for ( int i = buffer.length - 1; i >= 0; i-- ) {
            float v = buffer[i];
//            if ( v > limit )
//                buffer[i] = 2.0f * limit - v;
//            else if ( v < -limit )
//                buffer[i] = -2.0f * limit - v;
            if ( (v < -limit) || (v > limit) ) {
//                do
                    v = Math.signum( v ) * 2.0f*limit - v;
//                while ( (v < -limit) || (v > limit) );
                buffer[i] = v;
            }
        }
    }
}
