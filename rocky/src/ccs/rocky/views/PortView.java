package ccs.rocky.views;

import ccs.rocky.core.Port;
import ccs.rocky.core.View;
import ccs.rocky.ui.Snap;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 *
 * @author igel
 */
public abstract class PortView<T extends Port> extends View implements Draggable {

    public static class Input extends PortView<Port.Input> {

        public Input( Port.Input port ) {
            super( port );
        }

        @Override
        public int x() {
            return port.node.view().x() - DefaultNodeView.RX - RX;
        }

        @Override
        public int y() {
            return port.node.view().y() + offset;
        }
    }

    public static class Output extends PortView<Port.Output> {

        public Output( Port.Output port ) {
            super( port );
        }

        @Override
        public int x() {
            return port.node.view().x() + DefaultNodeView.RX + RX;
        }

        @Override
        public int y() {
            return port.node.view().y() + offset;
        }
    }
    static final int RX = 4, RY = 4;
    public final T port;
    protected Point drag;
    int offset;

    public PortView( T port ) {
        this.port = port;
    }

//    @Override
//    public int rx() {
//        return 4;
//    }
//
//    @Override
//    public int ry() {
//        return 4;
//    }
    @Override
    public void paint( Graphics2D g, Collection<View> selected, Hits<? super View> hits ) {
        Shape sh = new Rectangle2D.Double( x() - RX, y() - RY, RX * 2 + 1, RY * 2 + 1 );
        hits.associate( sh, this );
        g.setColor( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT );
        g.fill( sh );
        g.setColor( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
        g.draw( sh );
        if ( drag != null ) {
            g.setColor( Theme.FILL_DEFAULT );
            if ( x() > port.node.view().x() )
                LinkView.draw( g, x(), y(), drag.x, drag.y, Theme.BRDR_SELECTED, Theme.FILL_SELECTED );
            else
                LinkView.draw( g, drag.x, drag.y, x(), y(), Theme.BRDR_SELECTED, Theme.FILL_SELECTED );
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
        Port p = null;
        if ( into instanceof PortView )
            p = ((PortView) into).port;
        else if ( into instanceof NodeView ) {
            NodeView nv = (NodeView) into;
            if ( port instanceof Port.Input )
                for ( Port i : nv.node.outputs() )
                    if ( p != null ) {
                        p = null;
                        break;
                    } else
                        p = i;
            if ( port instanceof Port.Output )
                for ( Port i : nv.node.inputs() )
                    if ( p != null ) {
                        p = null;
                        break;
                    } else
                        p = i;
        }
        if ( port instanceof Port.Input ) {
            Port.Input pi = (Port.Input) port;
            if ( p != null )
                if ( p instanceof Port.Output ) {
                    pi.connect( (Port.Output) p );
                    return;
                }
            pi.connect( null );
        } else if ( (p != null) && (port instanceof Port.Output) )
            if ( p instanceof Port.Input )
                ((Port.Input) p).connect( (Port.Output) port );
    }
}
