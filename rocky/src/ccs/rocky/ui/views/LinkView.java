package ccs.rocky.ui.views;

import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Input;
import ccs.rocky.core.Port.Output;
import ccs.util.VMath;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.PathIterator;
import java.util.Arrays;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class LinkView extends View {

    private final PortView<Port.Output> from;
    private final PortView<Port.Input> to;

    public LinkView( PortView<Output> from, PortView<Input> to ) {
        this.from = from;
        this.to = to;
    }

    public PortView<Port.Output> from() {
        return from;
    }

    public PortView<Port.Input> to() {
        return to;
    }

    @Override
    public boolean hit( Point p ) {
        if ( !super.hit( p ) )
            return false;
        Shape s = shape();
        double[] pp = new double[2], o = null;
        for ( PathIterator pi = s.getPathIterator( null, 1 ); !pi.isDone(); pi.next() ) {
            int c = pi.currentSegment( pp );
            if ( c == PathIterator.SEG_LINETO ) {
                VMath.V2 vs = new VMath.V2( pp[0] - o[0], pp[1] - o[1] );
                double vsl = vs.length();
                VMath.V2 vp = new VMath.V2( (double) p.x - o[0], (double) p.y - o[1] );
                double pl = vs.project( vp ) / vsl / vsl;
                if ( (pl < 0) || (pl > 1) )
                    continue;
                VMath.V2 vsx = new VMath.V2( vs.y, -vs.x ).normalize();
                double xl = vsx.project( vp );
//                System.out.println( pl + ":" + xl );
                if ( Math.abs( xl ) < 2 )
                    return true;
            }
            o = Arrays.copyOf( pp, pp.length );
        }
        return false;
    }

    @Override
    public int x() {
        return (from.x() + to.x()) / 2;
    }

    @Override
    public int y() {
        return (from.y() + to.y()) / 2;
    }

    @Override
    public int rx() {
        return Math.abs( from.x() - to.x() ) / 2;
    }

    @Override
    public int ry() {
        return Math.abs( from.y() - to.y() ) / 2;
    }

    @Override
    public void paint( Graphics2D g, Collection<View> selected ) {
        g.setColor( selected.contains( this ) ? PAINT_SELECTED : PAINT_DEFAULT );
//        Shape s = shape();
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
        g.draw( shape() );
    }

    private Shape shape() {
        int xf, yf = from.y();
        if ( from.node() instanceof DotNodeView )
            xf = from.x();
        else
            xf = from.x() + from.rx();
        int xt, yt = to.y();
        if ( to.node() instanceof DotNodeView )
            xt = to.x();
        else
            xt = to.x() - to.rx();
        int d = Math.abs( xf - xt ) / 2;
        int df = (from.node() instanceof DotNodeView) ? 0 : d;
        int dt = (to.node() instanceof DotNodeView) ? 0 : d;
        return new CubicCurve2D.Double( xf, yf, xf + df, yf, xt - dt, yt, xt, yt );
    }
}
