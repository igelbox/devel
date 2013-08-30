package ccs.rocky.runtime;

/**
 *
 * @author igel
 */
public class FMath {

    public static float pow( float a, float b ) {
        return (float) Math.pow( a, b );
    }

    public static float log( float a ) {
        return (float) Math.log( a );
    }

    public static float exp( float a ) {
        return (float) Math.exp( a );
    }

    public static float inv( float a ) {
        return -a;
    }
}
