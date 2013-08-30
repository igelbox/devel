package ccs;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;

/**
 *
 * @author igel
 */
public abstract class View {

    public interface Draggable {

        void x( int x );

        void y( int y );
    }

    public abstract int x();

    public abstract int y();

    public abstract int rx();

    public abstract int ry();

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

    public abstract void paint( Graphics2D g, Collection<View> selection );
}
