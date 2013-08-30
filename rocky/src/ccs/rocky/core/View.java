package ccs.rocky.core;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.util.Collection;

/**
 *
 * @author igel
 */
public abstract class View {

    public static abstract class Hits<T> {

        public static final Hits<Object> VOID = new Hits<Object>() {
            @Override
            public void associate( Shape area, Object object ) {
            }
        };

        public abstract void associate( Shape area, T object );
    }
//    public boolean select( Point p, Collection<View> selection ) {
//        return hit( p ) && selection.add( this );
//    }
//
//    public boolean select( Rectangle r, Collection<View> selection ) {
//        return hit( r ) && selection.add( this );
//    }
//
//    public boolean hit( Point p ) {
//        int dx = p.x - x(), dy = p.y - y();
//        return (Math.abs( dx ) <= rx()) && (Math.abs( dy ) <= ry());
//    }
//
//    protected boolean hit( Rectangle r ) {
//        int x = x(), y = y(), rx = rx(), ry = ry();
//        return r.contains( x - rx, y - ry ) && r.contains( x + rx, y + ry );
//    }

    /** Центр - X */
    public abstract int x();

    /** Центр - Y */
    public abstract int y();

//    /** Половина ширины */
//    protected abstract int rx();
//
//    /** Половина высоты */
//    protected abstract int ry();
    public abstract void paint( Graphics2D g, Collection<View> selected, Hits<? super View> hits );
}
