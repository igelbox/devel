package ccs.jrack._filters;

import ccs.jrack.Filter;

/**
 *
 * @author igel
 */
public class Gate extends Filter {

    private final float treshold;

    public Gate( float treshold ) {
        this.treshold = treshold;
    }

    @Override
    public void filter( float[] buffer ) {
        for ( int i = buffer.length - 1; i >= 0; i-- )
            if ( Math.abs( buffer[i] ) > treshold )
                return;
        for ( int i = buffer.length - 1; i >= 0; i-- )
            buffer[i] = 0.0f;
    }
}
