package ccs.rocky.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;

/**
 *
 * @author igel
 */
public abstract class View extends ccs.rocky.geom.Point {

    protected static final Color PAINT_DEFAULT = Color.GRAY;
    protected static final Color PAINT_SELECTED = Color.getHSBColor( 0.66f, 0.66f, 1.00f );

    public void select( Point p, Collection<View> selection ) {
        if ( hit( p ) )
            selection.add( this );
    }

    public void select( Rectangle r, Collection<View> selection ) {
        if ( hit( r ) )
            selection.add( this );
    }

    public boolean hit( Point p ) {
        int dx = p.x - x(), dy = p.y - y();
        return (Math.abs( dx ) <= rx()) && (Math.abs( dy ) <= ry());
    }

    protected boolean hit( Rectangle r ) {
        int x = x(), y = y(), rx = rx(), ry = ry();
        return r.contains( x - rx, y - ry ) && r.contains( x + rx, y + ry );
    }

    /** Центр - X */
    @Override
    public abstract int x();

    /** Центр - Y */
    @Override
    public abstract int y();

    /** Половина ширины */
    public abstract int rx();

    /** Половина высоты */
    public abstract int ry();

    public abstract void paint( Graphics2D g );
}
