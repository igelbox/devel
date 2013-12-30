package client;

/**
 *
 * @author igel
 */
public class TimeUtil {

    private static final long NS_MASK = 0x3FFFFFFFFFFL;
    private static final long MS_MASK = 0x3FFFFFL;

    public static long ticks() {
        long ns = System.nanoTime(), ms = System.currentTimeMillis();
        return ((ms & MS_MASK) << 42) | (ns & NS_MASK);
    }

    public static double delta( long t1, long t0 ) {
        long ns0 = t0 & NS_MASK, ms0 = (t0 >> 42) & MS_MASK;
        long ns1 = t1 & NS_MASK, ms1 = (t1 >> 42) & MS_MASK;
        double md = (ms1 - ms0) * 1E-3;
        double nd = (ns1 - ns0) * 1E-9;
        if ( Math.abs( md - nd ) < 0.1 )
            return nd;
        return md;
    }
}
