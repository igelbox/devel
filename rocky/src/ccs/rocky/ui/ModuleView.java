package ccs.rocky.ui;

import ccs.rocky.Node;
import ccs.rocky.Node.Port;
import ccs.rocky.Module;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import javax.swing.JComponent;

/**
 *
 * @author igel
 */
public class ModuleView extends JComponent {

    private final MouseAdapter mouseAdapter = new MouseAdapter() {

        private Point o;

        private View selectPoint( Point p ) {
            for ( View c : components )
                if ( c.hit( p ) )
                    return c;
            for ( View c : views )
                if ( c.hit( p ) )
                    return c;
            return null;
        }

        @Override
        public void mousePressed( MouseEvent e ) {
            super.mousePressed( e );
            o = e.getPoint();
            View cv = selectPoint( o );
            if ( (cv == null) || !selected.contains( cv ) ) {
                selected.clear();
                if ( cv != null )
                    selected.add( cv );
            }
            if ( cv == null )
                selection = new Rectangle( o );
            ModuleView.this.repaint();
        }

        @Override
        public void mouseReleased( MouseEvent e ) {
            super.mouseReleased( e );
            if ( selection == null )
                return;
            Point p = e.getPoint();
            selection.setSize( p.x - selection.x, p.y - selection.y );
            Rectangle s = new Rectangle( selection );
            if ( s.width < 0 ) {
                s.x += s.width;
                s.width = -s.width;
            }
            if ( s.height < 0 ) {
                s.y += s.height;
                s.height = -s.height;
            }
            selected.clear();
            for ( View c : components )
                if ( c.hit( s ) )
                    selected.add( c );
            for ( View c : views )
                if ( c.hit( s ) )
                    selected.add( c );
            selection = null;
            ModuleView.this.repaint();
        }

        @Override
        public void mouseDragged( MouseEvent e ) {
            super.mouseDragged( e );
            Point p = e.getPoint();
            if ( selection != null )
                selection.setSize( p.x - selection.x, p.y - selection.y );
            else {
                int dx = p.x - o.x, dy = p.y - o.y;
//                for ( View c : selected ) {
//                    c.x( c.x() + dx );
//                    c.y( c.y() + dy );
//                }
            }
            o = p;
            ModuleView.this.repaint();
        }
    };

    private abstract class ModulePortView extends PortView {

        private int y;

        public ModulePortView( int y, Node.Port port ) {
            super( port );
            this.y = y;
        }

        @Override
        public int y() {
            return y;
        }

//        @Override
//        public void y( int y ) {
//            this.y = y;
//        }
        @Override
        public int rx() {
            return 5;
        }

        @Override
        public int ry() {
            return 3;
        }
    }

    private class ModuleInputPortView extends ModulePortView {

        public ModuleInputPortView( int y, Port port ) {
            super( y, port );
        }

        @Override
        public int x() {
            return rx();
        }
    }

    private class ModuleOutputPortView extends ModulePortView {

        public ModuleOutputPortView( int y, Port port ) {
            super( y, port );
        }

        @Override
        public int x() {
            return getWidth() - rx();
        }
    }
    private Rectangle selection;
    private final Collection<ComponentView> components = new ArrayList<ComponentView>();
    private final Collection<View> views = new ArrayList<View>();
    private final Collection<View> selected = new HashSet<View>();
    private Map<Node.Port, PortView> portMap = new IdentityHashMap<Node.Port, PortView>();

    public ModuleView( Module module ) {
        setPreferredSize( new Dimension( 640, 480 ) );
        addMouseListener( mouseAdapter );
        addMouseMotionListener( mouseAdapter );
        setDoubleBuffered( false );
        {
            int yy = 0;
            for ( Node.Port.Output o : module.moduleInputs() )
                portMap.put( o, new ModuleInputPortView( yy += 100, o ) );
        }
        int xx = 0;
        for ( Node c : module ) {
            ComponentView cv = new ComponentView( c );
            cv.cx = xx += 100;
            cv.cy = 100;
            components.add( cv );
//            for ( int i = 0; i < c.outputs().length; i++ )
//                portMap.put( c.outputs()[i], cv.createOutputPortView( i ) );
//            for ( int i = 0; i < c.inputs().length; i++ )
//                portMap.put( c.inputs()[i], cv.createInputPortView( i ) );
        }
//        for ( ComponentView cv : components )
//            for ( Component.Port.Input i : cv.component.inputs() ) {
//                Component.Port.Output o = i.connectedTo();
//                if ( o == null )
//                    continue;
//                PortView from = portMap.get( o );
//                PortView to = portMap.get( i );
//                LinkView lv = new LinkView( from, to );
//                views.add( lv );
//            }
//        {
//            int yy = 0;
//            for ( Component.Port.Input i : module.moduleOutputs() ) {
//                PortView pv = new ModuleOutputPortView( yy += 100, i );
//                portMap.put( i, pv );
//                Component.Port.Output o = i.connectedTo();
//                if ( o == null )
//                    continue;
//                PortView from = portMap.get( o );
//                LinkView lv = new LinkView( from, pv );
//                views.add( lv );
//            }
//        }
        views.addAll( portMap.values() );
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D gg = (Graphics2D) g;
//        gg.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        int w = getWidth(), h = getHeight();
        g.setColor( Color.BLACK );
        g.fillRect( 0, 0, w, h );
        g.setColor( Color.DARK_GRAY );
        for ( int x = 0; x < w; x += 10 )
            for ( int y = 0; y < h; y += 10 )
                g.drawLine( x, y, x, y );
//        for ( int i = 0; i < components.length - 1; i++ )
//            for ( int j = 0; j < components.length - 1; j++ )
//                if ( i != j ) {
//                    paintLink( gg, components[j], components[i] );
//                    break;
//                }
        for ( ComponentView cv : components )
            cv.paint( gg );
        for ( View v : views )
            v.paint( gg );

        if ( selection != null ) {
            g.setColor( Color.GRAY );
            Rectangle s = new Rectangle( selection );
            if ( s.width < 0 ) {
                s.x += s.width;
                s.width = -s.width;
            }
            if ( s.height < 0 ) {
                s.y += s.height;
                s.height = -s.height;
            }
            gg.draw( s );
        }
    }
}
