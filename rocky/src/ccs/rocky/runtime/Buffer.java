package ccs.rocky.runtime;

/**
 *
 * @author igel
 */
public class Buffer implements Sink {

    private static class Chunk {

        private final float[] data;
        private final float min, max;

        public Chunk( float[] data ) {
            float mn = Float.MAX_VALUE, mx = -mn;
            this.data = data;
            for ( float f : data ) {
                mn = Math.min( mn, f );
                mx = Math.max( mx, f );
            }
            this.min = mn;
            this.max = mx;
        }
    }
    private final Chunk[] chunks = new Chunk[16];
    private final float[] data;
    private int cc;
    private float min, max;

    public Buffer( int size ) {
        this.data = new float[size];
    }

    public void add() {
        if ( cc == 16 ) {
            System.arraycopy( chunks, 1, chunks, 0, 15 );
            cc--;
        }
        chunks[cc++] = new Chunk( data );
        min = Float.MAX_VALUE;
        max = -min;
        for ( int i = 0; i < cc; i++ ) {
            Chunk c = chunks[i];
            min = Math.min( min, c.min );
            max = Math.max( max, c.max );
        }
    }

    public float max() {
        return max;
    }

    public float min() {
        return min;
    }

    @Override
    public float[] buffer() {
        return data;
    }
}
