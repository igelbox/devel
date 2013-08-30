package ccs.rocky.ui;

//import ccs.rocky.Node.Port.Input;
//import ccs.rocky.Node.Port.Output;
//import ccs.rocky.event.Listener;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.CubicCurve2D;

/**
 *
 * @author igel
 */
public class LinkView extends View {

//    private final Listener.PortConnect l = new Listener.PortConnect() {
//
//        @Override
//        public void portConnected( Input port, Output to ) {
//            if ( (to == null) )
//        }
//    };
    private final PortView from, to;

    public LinkView( PortView from, PortView to ) {
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean hit( Point p ) {
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
    public void paint( Graphics2D g ) {
        int xf = from.x() + from.rx(), yf = from.y(), xt = to.x() - to.rx(), yt = to.y();
        int d = Math.abs( xf - xt ) / 2;
        CubicCurve2D.Double spline = new CubicCurve2D.Double( xf, yf, xf + d, yf, xt - d, yt, xt, yt );
        g.setColor( PAINT_DEFAULT );
        g.draw( spline );
    }
}
