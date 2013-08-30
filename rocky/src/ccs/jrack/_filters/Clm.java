package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Clm extends Filter {

    @Override
    public void filter( float[] buffer ) {
        for ( int i = 0; i < buffer.length; i++ ) {
            float v = buffer[i];
            float a = Math.abs( v ), s = Math.signum( v );
            a = (float) Math.log( 1.0 + a * 500.0 );
            buffer[i] = a * s;
        }
    }
}
