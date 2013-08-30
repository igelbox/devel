package ccs.rocky.views;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.View;
import ccs.util.Iterabled;
import ccs.util.Strings;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class DefaultNodeView extends NodeView {

//    private abstract class PV<T extends Port> extends PortView<T> {
//
//        protected final int offset;
//
//        public PV( T port, int offset ) {
//            super( port, DefaultNodeView.this );
//            this.offset = offset;
//        }
//
//        @Override
//        public int y() {
//            return DefaultNodeView.this.y() + offset;
//        }
//    }
//
//    private class PortViewIn extends PV<Port.Input> {
//
//        public PortViewIn( Port.Input port, int offset ) {
//            super( port, offset );
//        }
//
//        @Override
//        public int x() {
//            return DefaultNodeView.this.x() - DefaultNodeView.RX - RX;
//        }
//
//        @Override
//        public void paint( Graphics2D g, Collection<View> selected ) {
//            super.paint( g, selected );
//            final int x = x(), y = y(), rx = RX, ry = RY, h = ry * 2 + 3;
//            if ( port.caption != null ) {
//                GlyphVector gv = Theme.FONT_TITLE_P.createGlyphVector( Theme.FRC, port.caption );
//                Rectangle2D vb = gv.getVisualBounds();
//                int w = (int) vb.getWidth() + 4;
//                g.setColor( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT.brighter() );
//                g.fillRect( x + rx, y - ry - 1, w, h );
//                g.setColor( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
//                g.drawRect( x + rx, y - ry - 1, w, h );
//                float cox = x + rx + 1;
//                float coy = y - ry - 1 + h / 2 + (float) vb.getHeight() / 2;
//                Shape cs = gv.getOutline( cox, coy );
//                g.setColor( Color.BLACK );
//                g.fill( cs );
//            }
//        }
//    };
//
//    private class PortViewOut extends PV<Port.Output> {
//
//        public PortViewOut( Port.Output port, int offset ) {
//            super( port, offset );
//        }
//
//        @Override
//        public int x() {
//            return DefaultNodeView.this.x() + DefaultNodeView.RX + RX;
//        }
//
//        @Override
//        public void paint( Graphics2D g, Collection<View> selected ) {
//            super.paint( g, selected );
//            final int x = x(), y = y(), rx = RX, ry = RY, h = ry * 2 + 3;
//            if ( port.caption != null ) {
//                GlyphVector gv = Theme.FONT_TITLE_P.createGlyphVector( Theme.FRC, port.caption );
//                Rectangle2D vb = gv.getVisualBounds();
//                int w = (int) vb.getWidth() + 4;
////            g.setColor( Color.WHITE );
//                g.setColor( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT.brighter() );
//                g.fillRect( x - rx - w + 1, y - ry - 1, w, h );
//                g.setColor( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
//                g.drawRect( x - rx - w + 1, y - ry - 1, w, h );
//                float cox = x - rx - (float) vb.getWidth() - 1;
//                float coy = y - ry - 1 + h / 2 + (float) vb.getHeight() / 2;
//                Shape cs = gv.getOutline( cox, coy );
//                g.setColor( Color.BLACK );
//                g.fill( cs );
//            }
//        }
//    };
    protected class State {

        private final int ry;
        private final Shape caption;

        public State() {
            Collection<Port> cci = new ArrayList<Port>( 1 );
            Collection<Port> uci = new ArrayList<Port>( 1 );
            Collection<Port> cco = new ArrayList<Port>( 1 );
            Collection<Port> uco = new ArrayList<Port>( 1 );
            for ( Port i : node.inputs() )
                (Strings.isVoid( i.caption ) ? uci : cci).add( i );
            for ( Port i : node.outputs() )
                (Strings.isVoid( i.caption ) ? uco : cco).add( i );
            ry = 16 * (Math.max( cci.size(), uco.size() ) + Math.max( uci.size(), cco.size() ));
            int oi = 16 - ry;
            for ( Port i : cci ) {
                ((PortView) i.view()).offset = oi;
                oi += 32;
            }
            for ( Port i : uci ) {
                ((PortView) i.view()).offset = oi;
                oi += 32;
            }
            int oo = 16 - ry;
            for ( Port i : uco ) {
                ((PortView) i.view()).offset = oo;
                oo += 32;
            }
            for ( Port i : cco ) {
                ((PortView) i.view()).offset = oo;
                oo += 32;
            }
            caption = Theme.FONT_TITLE.createGlyphVector( Theme.FRC, caption() ).getOutline();
        }
    }
    static final int RX = 32;
    private State _state;

    public DefaultNodeView( Node node ) {
        super( node );
    }

    private String caption() {
        Class<?> c = node.getClass();
        Node.Descr d = c.getAnnotation( Node.Descr.class );
        if ( d != null )
            return d.caption();
        return c.getSimpleName().toLowerCase();
    }

    protected State state() {
        if ( _state == null )
            _state = new State();
        return _state;
    }

    @Override
    public void paint( Graphics2D g, Collection<View> selected, Hits<? super View> hits ) {
        final State s = state();
        Shape sh = new RoundRectangle2D.Double( x() - RX, y() - s.ry, RX * 2 + 1, s.ry * 2 + 1, 10, 10 );
        hits.associate( sh, this );
        g.setPaint( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT );
        g.fill( sh );
        g.setPaint( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
        g.draw( sh );
        Rectangle2D vb = s.caption.getBounds2D();
        double ox = x() - vb.getMinX() - vb.getWidth() / 2;
        double oy = y() - vb.getMinY() - vb.getHeight() / 2;
        g.translate( ox, oy );
        try {
            g.setColor( Color.BLACK );
            g.fill( s.caption );
        } finally {
            g.translate( -ox, -oy );
        }
        for ( Port p : node.ports() )
            p.view().paint( g, selected, hits );
    }

//    @Override
//    public Iterable<PortView<Port.Input>> inputs() {
//        return new Iterabled.Array<PortView<Port.Input>>( state().inputs );
//    }
//
//    @Override
//    public Iterable<PortView<Port.Output>> outputs() {
//        return new Iterabled.Array<PortView<Port.Output>>( state().outputs );
//    }
    @Override
    public void invalidate( int flags ) {
        _state = null;
    }
}
