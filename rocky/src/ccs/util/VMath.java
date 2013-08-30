package ccs.util;

/**
 *
 * @author igel
 */
public class VMath {

    public static class V2 {

        public final double x, y;

        public V2( double x, double y ) {
            this.x = x;
            this.y = y;
        }

        public double length() {
            return Math.sqrt( x * x + y * y );
        }

        public V2 scale( double k ) {
            return new V2( x * k, y * k );
        }

        public V2 normalize() {
            return scale( 1.0 / length() );
        }

        public double project( V2 v ) {
            return x * v.x + y * v.y;
        }

        @Override
        public String toString() {
            return String.format( "V(%f;%f)", x, y );
        }
    }
}
