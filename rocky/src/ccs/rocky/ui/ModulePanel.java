package ccs.rocky.ui;

import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Input;
import ccs.rocky.core.Port.Output;
import ccs.rocky.ui.views.*;
import ccs.util.Iterabled;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
            for ( View v : selected )
                if ( v.hit( o ) )
                    return;
            selected.clear();
            java.util.List<View> sel = new ArrayList<View>();
            for ( View v : allViews )
                v.select( o, sel );
            if ( sel.isEmpty() )
                selection = new Rectangle( o );
            else {
                View v = sel.get( sel.size() - 1 );
                selected.add( v );
                if ( v instanceof NodeView ) {
                    NodeView nv = (NodeView) v;
                    Node n = nv.node();
                    nodes.remove( n );
                    nodes.put( n, nv );
                }
            }
            ModulePanel.this.repaint();
        }

        @Override
        public void mouseDragged( MouseEvent e ) {
            super.mouseDragged( e );
            Point p = e.getPoint();
            if ( selection == null ) {
                for ( View v : selected )
                    if ( v instanceof View.Draggable )
                        ((View.Draggable) v).drag( o, p, snap );
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
                for ( View v : allViews )
                    if ( v.select( p, into ) )
                        break;
                for ( View v : selected )
                    if ( v instanceof View.Draggable )
                        ((View.Draggable) v).drop( true, into.isEmpty() ? null : into.get( into.size() - 1 ) );
            } else {
                selection.setFrameFromDiagonal( o, p );
                selected.clear();
                for ( View v : allViews )
                    v.select( selection, selected );
                selection = null;
            }
            ModulePanel.this.repaint();
        }
    };
    private Rectangle selection;
    private final ViewsFactory vf = new ViewsFactory();
    private final Snap snap = new Snap( 8 );
    private final Module module;
    private final Map<Node, NodeView> nodes = new LinkedHashMap<Node, NodeView>();
    private final Collection<View> selected = new HashSet<View>();
    private final Map<Port, PortView> ports = new HashMap<Port, PortView>();
    private final Map<Port.Input, LinkView> links = new HashMap<Port.Input, LinkView>();
    private final Iterable<View> allViews = Iterabled.multi( nodes.values(), links.values() );
    private final Port.Input.Listener pl = new Port.Input.Listener() {

        @Override
        protected void notifyConnected( Input port, Output to ) {
            links.remove( port );
            if ( to == null )
                return;
            PortView<Port.Output> o = ports.get( to );
            PortView<Port.Input> i = ports.get( port );
            LinkView l = vf.createLink( o, i );
            links.put( port, l );
        }
    };
    private final Module.Listener ml = new Module.Listener() {

        @Override
        protected void node( Node node, NodeOp op ) {
            switch ( op ) {
                case ADD: {
                    NodeView v = vf.createView( node );
                    nodes.put( node, v );
                    for ( PortView<Port.Input> p : v.inputs() ) {
                        Port.Input i = p.port();
                        ports.put( i, p );
                        i.listen( pl );
                    }
                    for ( PortView<Port.Output> p : v.outputs() )
                        ports.put( p.port(), p );
                    v.x = 300;
                    v.y = 300;
                    ModulePanel.this.repaint();
                    break;
                }
                case DEL: {
                    NodeView v = nodes.remove( node );
                    if ( v == null )
                        break;
                    for ( PortView<Port.Input> p : v.inputs() ) {
                        Port.Input i = p.port();
                        ports.remove( i );
                        i.unlisten( pl );
                        links.remove( i );
                    }
                    for ( PortView<Port.Output> p : v.outputs() )
                        ports.remove( p.port() );
                    ModulePanel.this.repaint();
                    break;
                }
            }
        }
    };

    public ModulePanel( Module module ) {
        this.module = module;
        module.listen( ml );
        setPreferredSize( new Dimension( 640, 480 ) );
        addMouseListener( mouseAdapter );
        addMouseMotionListener( mouseAdapter );
        setDoubleBuffered( false );
        Random r = new Random( 0 );
        for ( Node n : module ) {
            NodeView v = vf.createView( n );
            nodes.put( n, v );
            for ( PortView<Port.Input> p : v.inputs() ) {
                Port.Input i = p.port();
                ports.put( i, p );
                i.listen( pl );
            }
            for ( PortView<Port.Output> p : v.outputs() )
                ports.put( p.port(), p );
            v.x = r.nextInt( 600 );
            v.y = r.nextInt( 400 );
        }
        for ( Node n : module )
            for ( Port.Input p : n.inputs() )
                if ( p.connected() != null ) {
                    PortView i = ports.get( p );
                    PortView o = ports.get( p.connected() );
                    LinkView l = vf.createLink( o, i );
                    links.put( p, l );
                }
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D gg = (Graphics2D) g;
        gg.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.drawImage( bg( getWidth(), getHeight() ), 0, 0, null );

        for ( View n : links.values() )
            n.paint( gg, selected );
        for ( View n : nodes.values() )
            n.paint( gg, selected );

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

    public void doDelete() {
        for ( View v : selected )
            if ( v instanceof NodeView )
                module.remove( ((NodeView) v).node() );
            else if ( v instanceof LinkView )
                ((LinkView) v).to().port().connect( null );
        repaint();
    }
    private BufferedImage _bg;

    private BufferedImage bg( int w, int h ) {
        if ( (_bg == null) || (_bg.getWidth() != w) || (_bg.getHeight() != h) ) {
            _bg = new BufferedImage( w, h, BufferedImage.TYPE_BYTE_GRAY );
            Graphics2D g = _bg.createGraphics();
            try {
                g.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
                g.setColor( Color.BLACK );
                g.fillRect( 0, 0, w, h );
                g.setColor( Color.DARK_GRAY );
                for ( int x = 0; x < w; x += snap.step )
                    for ( int y = 0; y < h; y += snap.step )
                        g.drawLine( x, y, x, y );
            } finally {
                g.dispose();
            }
        }
        return _bg;
    }
}
