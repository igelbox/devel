package ccs.rocky.views;

import ccs.rocky.core.View;
import ccs.rocky.nodes.Dot;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class DotNodeView extends NodeView {

    private static final int RX = 8, RY = 8;

    public DotNodeView( Dot node ) {
        super( node );
    }

    @Override
    public void paint( Graphics2D g, Collection<View> selected, Hits<? super View> hits ) {
        Shape sh = new Ellipse2D.Double( x() - RX, y() - RY, RX * 2, RY * 2 );
        hits.associate( sh, this );
        g.setColor( selected.contains( this ) ? Theme.FILL_SELECTED : Theme.FILL_DEFAULT );
        g.fill( sh );
        g.setColor( selected.contains( this ) ? Theme.BRDR_SELECTED : Theme.BRDR_DEFAULT );
        g.draw( sh );
    }

    @Override
    public void invalidate( int flags ) {
    }
}
