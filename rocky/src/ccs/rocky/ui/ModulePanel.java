package ccs.rocky.ui;

import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.View;
import ccs.rocky.jack.JackModule;
import ccs.rocky.persistent.Loader;
import ccs.rocky.views.Draggable;
import ccs.rocky.views.LinkView;
import ccs.rocky.views.NodeView;
import ccs.rocky.views.PortView;
import ccs.util.Iterabled;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import javax.swing.JComponent;

/**
 *
 * @author igel
 */
public class ModulePanel extends JComponent {

    private final MouseAdapter mouseAdapter = new MouseAdapter() {
        private Point o;

        @Override
        public void mousePressed( MouseEvent e ) {
            super.mousePressed( e );
            o = e.getPoint();
            for ( View v : selected ) {
                Shape s = hits.get( v );
                if ( (s != null) && s.contains( o ) )
                    return;
            }
            selected.clear();
            java.util.List<View> sel = new ArrayList<View>();
            for ( Map.Entry<View, Shape> _ : hits.entrySet() )
                if ( _.getValue().contains( o ) )
                    sel.add( _.getKey() );
            if ( sel.isEmpty() )
                selection = new Rectangle( o );
            else {
                View v = sel.get( sel.size() - 1 );
                selected.add( v );
                ModulePanel.this.onSelectionChanged( selected );
            }
            ModulePanel.this.repaint();
        }

        @Override
        public void mouseDragged( MouseEvent e ) {
            super.mouseDragged( e );
            Point p = e.getPoint();
            if ( selection == null ) {
                for ( View v : selected )
                    if ( v instanceof Draggable )
                        ((Draggable) v).drag( o, p, snap );
            } else
                selection.setFrameFromDiagonal( o, p );
            ModulePanel.this.repaint();
        }

        @Override
        public void mouseReleased( MouseEvent e ) {
            super.mouseReleased( e );
            Point p = e.getPoint();
            if ( selection == null ) {
                java.util.List<View> into = new ArrayList<View>();
                for ( Map.Entry<View, Shape> _ : hits.entrySet() )
                    if ( _.getValue().contains( p ) ) {
                        into.add( _.getKey() );
                        break;
                    }
                for ( View v : selected )
                    if ( v instanceof Draggable )
                        ((Draggable) v).drop( true, into.isEmpty() ? null : into.get( into.size() - 1 ) );
                updateLinks();
            } else {
                selection.setFrameFromDiagonal( o, p );
                selected.clear();
                for ( Map.Entry<View, Shape> _ : hits.entrySet() )
                    if ( selection.contains( _.getValue().getBounds2D() ) )
                        selected.add( _.getKey() );
                selection = null;
                ModulePanel.this.onSelectionChanged( selected );
            }
            ModulePanel.this.repaint();
        }

        @Override
        public void mouseWheelMoved( MouseWheelEvent e ) {
            Point2D p0 = inverseTransform( e.getPoint() );
            transform.translate( -p0.getX(), -p0.getY() );
            double wr = e.getWheelRotation();
            double s = Math.pow( 1.33, -wr );
            transform.scale( s, s );
            Point2D p1 = inverseTransform( e.getPoint() );
            transform.translate( p1.getX() - p0.getX(), p1.getY() - p0.getY() );
            ModulePanel.this.repaint();
        }

        private Point2D inverseTransform( Point2D p ) {
            try {
                return transform.inverseTransform( p, null );
            } catch ( NoninvertibleTransformException t ) {
                throw new RuntimeException( t );
            }
        }
    };
    final AffineTransform transform = new AffineTransform();
    private Rectangle selection;
    private final Snap snap = new Snap( 8 );
    public final Module module;
    private final Collection<View> selected = new HashSet<View>();
    private final Map<Port.Input, LinkView> links = new HashMap<Port.Input, LinkView>();
    private final Map<View, Shape> hits = new LinkedHashMap<View, Shape>();

    public ModulePanel( Loader loader ) throws IOException {
        this.module = new JackModule();
        this.module.load( loader );
        addMouseListener( mouseAdapter );
        addMouseMotionListener( mouseAdapter );
        addMouseWheelListener( mouseAdapter );
        setDoubleBuffered( false );
        for ( Node n : module )
            if ( Module.isSystemNode( n ) )
                if ( !n.inputs().iterator().hasNext() ) {
                    n.x = 50;
                    n.y = 50;
                } else if ( !n.outputs().iterator().hasNext() ) {
                    n.x = 500;
                    n.y = 50;
                }
        for ( Node n : module )
            for ( Port.Input p : n.inputs() )
                if ( p.connected() != null )
                    links.put( p, new LinkView( p.connected(), p ) );
        int mx = 0, my = 0;
        for ( Node n : module ) {
            mx = Math.max( mx, n.x );
            my = Math.max( my, n.y );
        }
        setPreferredSize( new Dimension( mx, my ) );
    }

    public void addNode( Node node ) {
        module.add( node );
        node.x = 300;
        node.y = 300;
        repaint();
    }

    private void updateLinks() {
        for ( Iterator<Map.Entry<Port.Input, LinkView>> i = links.entrySet().iterator(); i.hasNext(); )
            if ( i.next().getKey().connected() == null )
                i.remove();
        for ( Node n : module )
            for ( Port.Input i : n.inputs() )
                if ( (i.connected() != null) && (links.get( i ) == null) )
                    links.put( i, new LinkView( i.connected(), i ) );
    }

    public Module module() {
        return module;
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D gg = (Graphics2D) g;
        gg.setTransform( transform );
        gg.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.drawImage( bg( getWidth(), getHeight() ), 0, 0, null );

        hits.clear();
        View.Hits<View> hts = new View.Hits<View>() {
            @Override
            public void associate( Shape area, View object ) {
                hits.put( object, area );
            }
        };
        for ( Node n : module )
            n.view().paint( gg, selected, hts );
        for ( View n : links.values() )
            n.paint( gg, selected, hts );

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
//        for ( Node n : nodes.keySet() )
//            n.paint( gg );
    }

    public void deleteSelected() {
        for ( View v : selected )
            if ( v instanceof NodeView ) {
                NodeView nv = (NodeView) v;
                Node n = nv.node;
                module.remove( n );
                for ( Port.Input p : n.inputs() )
                    links.remove( p );
            } else if ( v instanceof LinkView ) {
                Port.Input i = ((LinkView) v).to;
                i.connect( null );
                links.remove( i );
            }
        selected.clear();
        repaint();
    }
    private BufferedImage _bg;

    private BufferedImage bg( int w, int h ) {
        if ( (_bg == null) || (_bg.getWidth() != w) || (_bg.getHeight() != h) ) {
            _bg = new BufferedImage( w, h, BufferedImage.TYPE_BYTE_GRAY );
            Graphics2D g = _bg.createGraphics();
            try {
                Color c = getBackground();
                g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g.setColor( c );
                g.fillRect( 0, 0, w, h );
//                g.setColor( c.darker().darker() );
//                for ( int x = 0; x < w; x += snap.step )
//                    for ( int y = 0; y < h; y += snap.step )
//                        g.drawLine( x, y, x, y );
            } finally {
                g.dispose();
            }
        }
        return _bg;
    }

    protected void onSelectionChanged( Collection<View> selection ) {
    }
}
