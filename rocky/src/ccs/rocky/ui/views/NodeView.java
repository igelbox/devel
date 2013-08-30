package ccs.rocky.ui.views;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.ui.Snap;
import java.awt.*;
import java.util.Collection;

/**
 *
 * @author igel
 */
public abstract class NodeView extends View implements View.Draggable {

    private class DragState {

        private final int sx, sy;

        public DragState( int sx, int sy ) {
            this.sx = sx;
            this.sy = sy;
        }
    }
    protected static final Font FONT_TITLE = Font.decode( Font.MONOSPACED + "-BOLD" );
    protected static final int UNIT = 16;
    protected final Node node;
    private DragState drag;
    public int x, y;

    NodeView( Node node ) {
        this.node = node;
    }

    public Node node() {
        return node;
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
    public void paint( Graphics2D g, Collection<View> selected ) {
        g.setFont( FONT_TITLE );
        FontMetrics fm = g.getFontMetrics();
        int x0 = x() - rx(), y0 = y() - ry(), w = rx() * 2 + 1, h = ry() * 2 + 1;
        int co = h / 2 + fm.getHeight() / 2 - fm.getDescent();
        g.setPaint( new GradientPaint( x0, y0, Color.LIGHT_GRAY, x0, y0 + 16, selected.contains( this ) ? PAINT_SELECTED : PAINT_DEFAULT ) );
        g.fillRoundRect( x0, y0, w, h, 3, 3 );
        {
            final String caption = node.caption();
            final int sw = fm.stringWidth( caption );
            g.setColor( Color.LIGHT_GRAY );
            final int cox = x() - sw / 2, coy = y0 + co;
            g.drawString( caption, cox + 1, coy + 1 );
            g.setColor( Color.BLACK );
            g.drawString( caption, cox, coy );
        }
        for ( View v : inputs() )
            v.paint( g, selected );
        for ( View v : outputs() )
            v.paint( g, selected );
    }

    @Override
    public boolean select( Point p, Collection<View> selection ) {
        boolean f = false;
        for ( View v : inputs() )
            f |= v.select( p, selection );
        for ( View v : outputs() )
            f |= v.select( p, selection );
        return f || super.select( p, selection );
    }

    @Override
    public boolean select( Rectangle r, Collection<View> selection ) {
        if ( hit( r ) )
            return super.select( r, selection );
        boolean f = false;
        for ( View v : inputs() )
            f |= v.select( r, selection );
        for ( View v : outputs() )
            f |= v.select( r, selection );
        return f;
    }

    @Override
    public void drag( Point from, Point to, Snap snap ) {
        if ( drag == null )
            drag = new DragState( x, y );
        this.x = snap.snap( (to.x - from.x) + drag.sx );
        this.y = snap.snap( (to.y - from.y) + drag.sy );
    }

    @Override
    public void drop( boolean ok, View into ) {
        if ( !ok ) {
            this.x = drag.sx;
            this.y = drag.sy;
        }
        drag = null;
    }

    public abstract Iterable<PortView<Port.Input>> inputs();

    public abstract Iterable<PortView<Port.Output>> outputs();
}
