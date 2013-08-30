package ccs;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author igel
 */
public class MainFormX extends JFrame {

    private final Collection<View> views = new ArrayList<View>();
    private final Collection<View> selection = new HashSet<View>();
    private final MouseAdapter ma = new MouseAdapter() {

        private Point pp;

        @Override
        public void mousePressed( MouseEvent e ) {
            super.mousePressed( e );
            pp = e.getPoint();
            for ( View v : selection )
                if ( v.hit( pp ) )
                    return;
            selection.clear();
            for ( View v : views )
                v.select( pp, selection );
            if ( selection.isEmpty() )
                selrect = new Rectangle( pp );
            mv.repaint();
        }

        @Override
        public void mouseMoved( MouseEvent e ) {
            super.mouseMoved( e );
            mp = e.getPoint();
            mv.repaint();
        }

        @Override
        public void mouseDragged( MouseEvent e ) {
            super.mouseMoved( e );
            Point p = e.getPoint();
            if ( selrect != null )
                selrect.setFrameFromDiagonal( p, pp );
            else {
                int dx = p.x - pp.x, dy = p.y - pp.y;
                for ( View v : selection )
                    if ( v instanceof View.Draggable ) {
                        View.Draggable d = (View.Draggable) v;
                        d.x( v.x() + dx );
                        d.y( v.y() + dy );
                    }
                pp = p;
            }
            mp = p;
            mv.repaint();
        }

        @Override
        public void mouseReleased( MouseEvent e ) {
            super.mouseReleased( e );
            Point p = e.getPoint();
            if ( selrect != null ) {
                selrect.setFrameFromDiagonal( p, pp );
                selection.clear();
                for ( View v : views )
                    v.select( selrect, selection );
                selrect = null;
            }
            pp = null;
            mv.repaint();
        }
    };
    private JComponent mv;
    private Rectangle selrect;
    private Point mp;

    public MainFormX() throws Throwable {
        super( "Rocky" );
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        views.add( new TestView( 100, 100 ) );
        views.add( new TestView( 200, 150 ) );
        views.add( new TestView( 300, 50 ) );
        mv = new JComponent() {

            @Override
            public void paint( Graphics g ) {
                Graphics2D gg = (Graphics2D) g;
                gg.setColor( Color.DARK_GRAY );
                gg.fill( gg.getClip() );
                for ( View v : views )
                    v.paint( gg, selection );
                if ( selrect != null ) {
                    gg.setColor( Color.CYAN );
                    gg.draw( selrect );
                }
                if ( mp != null ) {
                    gg.setColor( Color.red );
                    gg.drawLine( mp.x - 9, mp.y, mp.x + 9, mp.y );
                    gg.drawLine( mp.x, mp.y - 9, mp.x, mp.y + 9 );
                }
            }
        };
        mv.setPreferredSize( new Dimension( 800, 600 ) );
        mv.addMouseListener( ma );
        mv.addMouseMotionListener( ma );
        add( mv );
    }

    public static void main() throws Throwable {
        JFrame f = new MainFormX();
        f.pack();
        f.setVisible( true );
    }
}
