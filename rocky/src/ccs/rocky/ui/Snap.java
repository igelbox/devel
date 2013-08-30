package ccs.rocky.ui;

/**
 *
 * @author igel
 */
public class Snap {

    public static final Snap NO_SNAP = new Snap( 1 );
    public final int step;

    public Snap( int step ) {
        this.step = step;
    }

    public int snap( int i ) {
        return i / step * step;
    }
}
