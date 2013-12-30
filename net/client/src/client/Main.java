package client;

/**
 *
 * @author igel
 */
public class Main {

    private static double test( int n ) throws Throwable {
        Thread[] threads = new Thread[n];
        final int loops = 8;
        double result = 0;
        for ( int _ = 0; _ < loops; _++ ) {
            for ( int i = 0; i < threads.length; i++ )
                threads[i] = new Test( 1024 );
            long t0 = TimeUtil.ticks();
            for ( Thread t : threads )
                t.start();
            for ( Thread t : threads )
                t.join();
            result += n / TimeUtil.delta( TimeUtil.ticks(), t0 );
        }
        return result / loops;
    }

    public static void main( String[] args ) throws Throwable {
        test( 100 );
        System.out.println( "\"N\";\"I\"" );
        Algorithm a = new Algorithm( 24, 1, 100 );
        while ( a.hasNext() ) {
            int n = a.next();
            double p = test( n );
            System.out.println( String.format( "%d;%.3f", n, p ).replace( '.', ',' ) );
            a.put( n, p );
        }
    }
}
