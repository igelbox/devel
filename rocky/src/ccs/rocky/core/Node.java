package ccs.rocky.core;

import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.rocky.views.DefaultNodeView;
import ccs.util.Iterabled;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;

/**
 * Абстрактный узел графа обработки сигнала
 *
 * @author igel
 */
public abstract class Node/* extends View*/ {

    @Target( ElementType.METHOD )
    @Retention( RetentionPolicy.RUNTIME )
    public @interface Param {
    }

    @Target( ElementType.TYPE )
    @Retention( RetentionPolicy.RUNTIME )
    public @interface Descr {

        String caption();
    }

    public static abstract class View extends ccs.rocky.core.View {

        public static final int INVALIDATE_ALL = 0xFFFFFFFF;
        public final Node node;

        public View( Node node ) {
            this.node = node;
        }

        public abstract void invalidate( int flags );

        @Override
        public int x() {
            return node.x;
        }

        @Override
        public int y() {
            return node.y;
        }
    }

    public static <T extends Node> T create( Class<T> cls, String id, Loader loader ) throws InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        return cls.getConstructor( String.class, Loader.class ).newInstance( id, loader );
    }
    private View _view;
    public final String id;
    public int x, y;

    public Node( String id, Loader loader ) {
        this.id = id;
        Loader.Attribute a = loader.findAttribute( "x" );
        if ( a != null )
            this.x = a.asInt();
        a = loader.findAttribute( "y" );
        if ( a != null )
            this.y = a.asInt();
    }

    protected View createView() {
        return new DefaultNodeView( this );
    }

    public final View view() {
        View r = _view;
        if ( r == null )
            _view = r = createView();
        return r;
    }

    /** Перечень входных портов */
    public Iterable<Port.Input> inputs() {
        return Iterabled.emptyIterable();
    }

    /** Перечень выходных портов */
    public Iterable<Port.Output> outputs() {
        return Iterabled.emptyIterable();
    }

    /** Перечень всех портов */
    public Iterable<Port> ports() {
        return Iterabled.multi( inputs(), outputs() );
    }

    public void store( Storer storer ) {
        storer.putInt( "x", x );
        storer.putInt( "y", y );
        for ( Port.Input i : inputs() ) {
            Port.Output o = i.connected;
            if ( o != null ) {
                Storer link = storer.put( "link" );
                link.putString( "port", i.id );
                link.putString( "from", o.node.id + '.' + o.id );
            }
        }
    }

    public Port port( String id ) {
        for ( Port p : inputs() )
            if ( id.equals( p.id ) )
                return p;
        for ( Port p : outputs() )
            if ( id.equals( p.id ) )
                return p;
        return null;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + '(' + id + ')';
    }
//    //<editor-fold defaultstate="collapsed" desc="VIEW implementation">
//    private static final Port[] EMPTY = new Port[0];
//
//    protected class UIState {
//
//        private abstract class PortView extends ccs.rocky.core.View {
//
//            private final int offset;
//
//            public PortView( int offset ) {
//                this.offset = offset;
//            }
//
//            @Override
//            protected int rx() {
//                return 4;
//            }
//
//            @Override
//            protected int ry() {
//                return 4;
//            }
//
//            @Override
//            public int y() {
//                return y - uiState().ry + offset;
//            }
//
//            @Override
//            public void paint( Graphics2D g, Collection<ccs.rocky.core.View> selected ) {
//                int x0 = x() - rx(), y0 = y() - ry(), w = rx() * 2 + 1, h = ry() * 2 + 1;
//                Shape s = new Rectangle2D.Float( x0, y0, w, h );
//                g.setPaint( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT );
//                g.fill( s );
//                g.setPaint( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
//                g.draw( s );
//            }
//        }
//
//        private class PortViewIn extends PortView {
//
//            public PortViewIn( int offset ) {
//                super( offset );
//            }
//
//            @Override
//            public int x() {
//                return x - 16 - 4;
//            }
//
//            @Override
//            public void paint( Graphics2D g, Collection<ccs.rocky.core.View> selected ) {
//                super.paint( g, selected );
//            }
//        }
//
//        private class PortViewOut extends PortView {
//
//            public PortViewOut( int offset ) {
//                super( offset );
//            }
//
//            @Override
//            public int x() {
//                return x + 16 + 4;
//            }
//
//            @Override
//            public void paint( Graphics2D g, Collection<ccs.rocky.core.View> selected ) {
//                super.paint( g, selected );
//            }
//        }
//        private final int ry;
//        private final Port[] CCI, UCI;
//        private final Port[] CCO, UCO;
//        private final Map<Port, PortView> portViews = new IdentityHashMap<Port, PortView>( 1 );
//
//        public UIState() {
//            Collection<Port> cci = new ArrayList<Port>( 1 );
//            Collection<Port> uci = new ArrayList<Port>( 1 );
//            Collection<Port> cco = new ArrayList<Port>( 1 );
//            Collection<Port> uco = new ArrayList<Port>( 1 );
//            for ( Port i : inputs() )
//                (Strings.isVoid( i.caption ) ? uci : cci).add( i );
//            for ( Port i : outputs() )
//                (Strings.isVoid( i.caption ) ? uco : cco).add( i );
//            CCI = cci.toArray( EMPTY );
//            UCI = uci.toArray( EMPTY );
//            CCO = cco.toArray( EMPTY );
//            UCO = uco.toArray( EMPTY );
//            ry = 16 * (Math.max( CCI.length, UCO.length ) + Math.max( UCI.length, CCO.length ));
//            int oi = 16;
//            for ( Port i : CCI ) {
//                portViews.put( i, new PortViewIn( oi ) );
//                oi += 32;
//            }
//            for ( Port i : UCI ) {
//                portViews.put( i, new PortViewIn( oi ) );
//                oi += 32;
//            }
//            int oo = 16;
//            for ( Port i : UCO ) {
//                portViews.put( i, new PortViewOut( oo ) );
//                oo += 32;
//            }
//            for ( Port i : CCO ) {
//                portViews.put( i, new PortViewOut( oo ) );
//                oo += 32;
//            }
//        }
//    }
//    private UIState _uiState;
//
//    protected UIState uiState() {
//        UIState r = _uiState;
//        if ( r == null )
//            _uiState = r = new UIState();
//        return r;
//    }
//

    protected String caption() {
        Class<?> c = getClass();
        Node.Descr d = c.getAnnotation( Node.Descr.class );
        if ( d != null )
            return d.caption();
        return c.getSimpleName().toLowerCase();
    }
//
//    @Override
//    public int x() {
//        return x;
//    }
//
//    @Override
//    public int y() {
//        return y;
//    }
//
//    @Override
//    protected int rx() {
//        return 32;
//    }
//
//    @Override
//    protected int ry() {
//        return uiState().ry;
//    }
//
//    @Override
//    public void paint( Graphics2D g, Collection<ccs.rocky.core.View> selected ) {
//        final UIState us = uiState();
//        final int rx = 32, ry = us.ry;
//        int x0 = x - rx, y0 = y - ry, w = rx * 2 + 1, h = ry * 2 + 1;
//        {
//            final int r = 10;
//            Shape s = new RoundRectangle2D.Float( x0, y0, w, h, r, r );
//            g.setPaint( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT );
//            g.fill( s );
//            g.setPaint( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
//            g.draw( s );
//            final String caption = caption();
//            GlyphVector gv = Theme.FONT_TITLE.createGlyphVector( Theme.FRC, caption );
//            Rectangle2D vb = gv.getVisualBounds();
//            double ox = x - vb.getX() - vb.getWidth() / 2;
//            double oy = y - vb.getY() - vb.getHeight() / 2;
//            Shape ss = new RoundRectangle2D.Double( vb.getX() - 2, vb.getY() - 2, vb.getWidth() + 4, vb.getHeight() + 4, 4, 4 );
//            g.translate( ox, oy );
//            try {
//                g.setPaint( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT.brighter() );
//                g.fill( ss );
//                g.setPaint( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
//                g.draw( ss );
//                Shape cs = gv.getOutline();
//                g.setColor( Color.BLACK );
//                g.fill( cs );
//            } finally {
//                g.translate( -ox, -oy );
//            }
//        }
//        for ( UIState.PortView i : us.portViews.values() )
//            i.paint( g, selected ); //            int offs = us.offsets.get( i );
//        //            GlyphVector gv = Theme.FONT_TITLE_P.createGlyphVector( Theme.FRC, i.caption == null ? "" : i.caption );
//        //            Rectangle2D vb = gv.getVisualBounds();
//        //            int _w = (int) vb.getWidth() + 4;
//        //            Shape s = new Rectangle2D.Float( x0 - 8, y0 + offs - 4, 8 + _w, 8 );
//        //            g.setPaint( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT );
//        //            g.fill( s );
//        //            g.setPaint( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
//        //            g.draw( s );
//        //            float cox = x0 + 1;
//        //            float coy = y0 + offs + (float) vb.getHeight() / 2;
//        //            Shape cs = gv.getOutline( cox, coy );
//        //            g.setColor( Color.BLACK );
//        //            g.fill( cs );
//        //            offs += 32;
////        for ( Port i : outputs() ) {
////            int offs = us.offsets.get( i );
////            GlyphVector gv = Theme.FONT_TITLE_P.createGlyphVector( Theme.FRC, i.caption == null ? "" : i.caption );
////            Rectangle2D vb = gv.getVisualBounds();
////            int _w = (int) vb.getWidth() + 4;
////            Shape s = new Rectangle2D.Float( x0 + w - _w, y0 + offs - 4, 8 + _w, 8 );
////            g.setPaint( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT );
////            g.fill( s );
////            g.setPaint( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
////            g.draw( s );
////            float cox = x0 + w - _w + 1;
////            float coy = y0 + offs + (float) vb.getHeight() / 2;
////            Shape cs = gv.getOutline( cox, coy );
////            g.setColor( Color.BLACK );
////            g.fill( cs );
////            offs += 32;
////        }
//    }
//
//    public ccs.rocky.core.View portView( Port p ) {
//        return uiState().portViews.get( p );
//    }
//    //</editor-fold>
}
