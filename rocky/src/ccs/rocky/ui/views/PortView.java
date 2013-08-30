package ccs.rocky.ui.views;

import ccs.rocky.core.Port;
import ccs.rocky.ui.Snap;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.CubicCurve2D;
import java.util.Collection;

/**
 *
 * @author igel
 */
public abstract class PortView<T extends Port> extends View implements View.Draggable {

    protected final T port;
    protected final NodeView node;
    protected Point drag;

    protected PortView( T port, NodeView node ) {
        this.port = port;
        this.node = node;
    }

    public T port() {
        return port;
    }

    public NodeView node() {
        return node;
    }

    @Override
    public int rx() {
        return 3;
    }

    @Override
    public int ry() {
        return 2;
    }

    @Override
    public void paint( Graphics2D g, Collection<View> selected ) {
        final int x = x(), y = y(), rx = rx(), ry = ry(), w = rx * 2 + 1, h = ry * 2 + 1;
        g.setColor( selected.contains( this ) ? PAINT_SELECTED : PAINT_DEFAULT );
        g.fillRect( x - rx, y - ry, w, h );
        g.setColor( Color.DARK_GRAY );
        g.drawLine( x - rx + 1, y, x + rx - 1, y );
        if ( drag != null ) {
            g.setColor( PAINT_DEFAULT );
            if ( x() > node.x() )
                drawLink( g, x() + rx(), y(), drag.x, drag.y );
            else
                drawLink( g, drag.x, drag.y, x() - rx(), y() );
        }
    }

    @Override
    public void drag( Point from, Point to, Snap snap ) {
        drag = to;
    }

    @Override
    public void drop( boolean ok, View into ) {
        drag = null;
        if ( !ok )
            return;
        PortView pv = null;
        if ( into instanceof PortView )
            pv = (PortView) into;
        else if ( into instanceof NodeView ) {
            NodeView nv = (NodeView) into;
            if ( port instanceof Port.Input )
                for ( PortView p : nv.outputs() )
                    if ( pv != null ) {
                        pv = null;
                        break;
                    } else
                        pv = p;
            if ( port instanceof Port.Output )
                for ( PortView p : nv.inputs() )
                    if ( pv != null ) {
                        pv = null;
                        break;
                    } else
                        pv = p;
        }
        if ( port instanceof Port.Input ) {
            Port.Input pi = (Port.Input) port;
            if ( pv != null ) {
                Port p = pv.port;
                if ( p instanceof Port.Output ) {
                    pi.connect( (Port.Output) p );
                    return;
                }
            }
            pi.connect( null );
        } else if ( (pv != null) && (port instanceof Port.Output) ) {
            Port p = pv.port;
            if ( p instanceof Port.Input )
                ((Port.Input) p).connect( (Port.Output) port );
        }
    }

    protected static void drawLink( Graphics2D g, int x0, int y0, int x1, int y1 ) {
        int d = Math.abs( x1 - x0 ) / 2;
        CubicCurve2D.Double spline = new CubicCurve2D.Double( x0, y0, x0 + d, y0, x1 - d, y1, x1, y1 );
        g.draw( spline );
    }
}
