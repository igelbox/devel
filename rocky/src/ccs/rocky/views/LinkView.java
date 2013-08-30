package ccs.rocky.views;

import ccs.rocky.core.Port;
import ccs.rocky.core.View;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class LinkView extends View {

    public final Port.Output from;
    public final Port.Input to;

    public LinkView( Port.Output from, Port.Input to ) {
        this.from = from;
        this.to = to;
    }

//    public PortView<Port.Output> from() {
//        return from;
//    }
//
//    public PortView<Port.Input> to() {
//        return to;
//    }

//    @Override
//    public boolean hit( Point p ) {
//        if ( !super.hit( p ) )
//            return false;
//        Shape s = shape( from.x(), from.y(), to.x(), to.y() );
//        double[] pp = new double[2], o = null;
//        for ( PathIterator pi = s.getPathIterator( null, 1 ); !pi.isDone(); pi.next() ) {
//            int c = pi.currentSegment( pp );
//            if ( c == PathIterator.SEG_LINETO ) {
//                VMath.V2 vs = new VMath.V2( pp[0] - o[0], pp[1] - o[1] );
//                double vsl = vs.length();
//                VMath.V2 vp = new VMath.V2( (double) p.x - o[0], (double) p.y - o[1] );
//                double pl = vs.project( vp ) / vsl / vsl;
//                if ( (pl < 0) || (pl > 1) )
//                    continue;
//                VMath.V2 vsx = new VMath.V2( vs.y, -vs.x ).normalize();
//                double xl = vsx.project( vp );
////                System.out.println( pl + ":" + xl );
//                if ( Math.abs( xl ) < 3 )
//                    return true;
//            }
//            o = Arrays.copyOf( pp, pp.length );
//        }
//        return false;
//    }

    @Override
    public int x() {
        return (from.view().x() + to.view().x()) / 2;
    }

    @Override
    public int y() {
        return (from.view().y() + to.view().y()) / 2;
    }

//    @Override
//    public int rx() {
//        return Math.abs( from.x() - to.x() ) / 2;
//    }
//
//    @Override
//    public int ry() {
//        return Math.abs( from.y() - to.y() ) / 2;
//    }

    @Override
    public void paint( Graphics2D g, Collection<View> selected, Hits<? super View> hits ) {
        Color c;
        if ( selected.contains( this ) )
            c = Theme.FILL_SELECTED;
        else
            switch ( from.state() ) {
                case CONST:
                    c = Color.LIGHT_GRAY;
                    break;
                case VAR:
                    c = Color.BLUE;
                    break;
                case SIGNAL:
                    c = Color.GREEN;
                    break;
                default:
                    c = Theme.FILL_DEFAULT;
            }
//        Shape s = shape( from.x(), from.y(), to.x(), to.y() );
//        double[] pp = new double[2], o = null;
//        for ( PathIterator pi = s.getPathIterator( null, 1 ); !pi.isDone(); pi.next() ) {
//            int c = pi.currentSegment( pp );
//            if ( c == PathIterator.SEG_LINETO ) {
//                g.draw( new java.awt.geom.Line2D.Double( o[0], o[1], pp[0], pp[1] ) );
//                VMath.V2 vs = new VMath.V2( pp[0] - o[0], pp[1] - o[1] );
//                VMath.V2 vsx = new VMath.V2( vs.y, -vs.x ).normalize().scale( 10 );
//                g.draw( new java.awt.geom.Line2D.Double( o[0], o[1], o[0] + vsx.x, o[1] + vsx.y ) );
//            }
//            o = Arrays.copyOf( pp, pp.length );
//        }
//        View f = from.node.node.portView( from.port );
//        View t = to.node.node.portView( to.port );
//        draw( g, f.x(), f.y(), t.x(), t.y(), selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT, c );
        View f = from.view(), t = to.view();
        draw( g, f.x(), f.y(), t.x(), t.y(), selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT, c );
    }

    private static Shape shape( int xf, int yf, int xt, int yt ) {
        int d = Math.abs( xf - xt ) / 2;
        int df = d;
        int dt = d;
        return new CubicCurve2D.Double( xf, yf, xf + df, yf, xt - dt, yt, xt, yt );
    }

    public static void draw( Graphics2D g, int x0, int y0, int x1, int y1, Color b, Color f ) {
        Shape s = shape( x0, y0, x1, y1 );
        g.setColor( b );
        g.setStroke( Theme.STROKE3 );
        g.draw( s );
        g.setColor( f );
        g.fillOval( x0 - 2, y0 - 2, 4, 4 );
        g.fillOval( x1 - 2, y1 - 2, 4, 4 );

        g.setColor( b );
        g.setStroke( Theme.STROKE1 );
        g.drawOval( x0 - 2, y0 - 2, 4, 4 );
        g.drawOval( x1 - 2, y1 - 2, 4, 4 );

        g.setColor( f );
        g.draw( s );
    }
}
