package client;

/**
 *
 * @author igel
 */
public class Algorithm {

    private final int resolution, from, to;
    private final double[] metred;
    private int count;

    public Algorithm( int resolution, int from, int to ) {
        int d = to - from + 1;
        this.resolution = Math.min( resolution, d );
        this.from = from;
        this.to = to;
        this.metred = new double[d];
        for ( int i = 0; i < metred.length; i++ )
            metred[i] = Double.NaN;
    }

    public boolean hasNext() {
        return count < resolution;
    }

    public int next() {
        switch ( count++ ) {
            case 0:
                return to;
            case 1:
                return from;
        }
        int idx = 0, i0 = 0;
        double mx = 0;
        for ( int i1 = 0; i1 < metred.length; i1++ ) {
            double m0 = metred[i0], m1 = metred[i1];
            if ( !Double.isNaN( m1 ) ) {
                if ( !Double.isNaN( m0 ) && ((i1 - i0) > 1) ) {
                    double d = Math.abs( m1 - m0 );
                    if ( d > mx ) {
                        mx = d;
                        idx = (i1 + i0) / 2;
                    }
                }
                i0 = i1;
            }
        }
        if ( idx == 0 )
            throw new IllegalStateException();
        return idx + from;
    }

    public void put( int n, double p ) {
        metred[n - from] = p;
    }
}
