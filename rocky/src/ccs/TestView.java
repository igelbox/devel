package ccs;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class TestView extends View implements View.Draggable {

    private abstract class Port extends View {

        @Override
        public int y() {
            return TestView.this.y();
        }

        @Override
        public int rx() {
            return 4;
        }

        @Override
        public int ry() {
            return 2;
        }

        @Override
        public void paint( Graphics2D g, Collection<View> selection ) {
            int x = x(), y = y(), rx = rx(), ry = ry();
            g.setColor( selection.contains( this ) ? Color.BLUE : Color.GRAY );
            g.drawRect( x - rx, y - ry, rx * 2, ry * 2 );
        }
    }

    private class PortIn extends Port {

        @Override
        public int x() {
            return TestView.this.x() - TestView.this.rx() - rx();
        }
    }

    private class PortOut extends Port {

        @Override
        public int x() {
            return TestView.this.x() + TestView.this.rx() + rx();
        }
    }
    private final Collection<Port> ports = new ArrayList<Port>();
    private int x, y;

    public TestView( int x, int y ) {
        this.x = x;
        this.y = y;
        ports.add( new PortIn() );
        ports.add( new PortOut() );
    }

    @Override
    public int x() {
        return x;
    }

    @Override
    public int y() {
        return y;
    }

    @Override
    public int rx() {
        return 32;
    }

    @Override
    public int ry() {
        return 32;
    }

    @Override
    public void paint( Graphics2D g, Collection<View> selection ) {
        int _x = x(), _y = y(), rx = rx(), ry = ry();
        g.setColor( selection.contains( this ) ? Color.BLUE : Color.GRAY );
        g.drawRect( _x - rx, _y - ry, rx * 2, ry * 2 );
        for ( View v : ports )
            v.paint( g, selection );
    }

    @Override
    public void select( Point p, Collection<View> selection ) {
        for ( View v : ports )
            if ( v.hit( p ) ) {
                selection.add( v );
                return;
            }
        super.select( p, selection );
    }

    @Override
    public void select( Rectangle r, Collection<View> selection ) {
        if ( hit( r ) )
            selection.add( this );
        else
            for ( View v : ports )
                v.select( r, selection );
    }

    @Override
    public void x( int x ) {
        this.x = x;
    }

    @Override
    public void y( int y ) {
        this.y = y;
    }
}
