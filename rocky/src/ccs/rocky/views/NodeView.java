package ccs.rocky.views;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.View;
import ccs.rocky.ui.Snap;
import java.awt.*;
import java.util.Collection;

/**
 *
 * @author igel
 */
public abstract class NodeView extends Node.View implements Draggable {

    private class DragState {

        private final int sx, sy;

        public DragState( int sx, int sy ) {
            this.sx = sx;
            this.sy = sy;
        }
    }
    private DragState drag;

    NodeView( Node node ) {
        super( node );
    }

//    @Override
//    public boolean select( Point p, Collection<View> selection ) {
//        boolean f = false;
//        for ( View v : inputs() )
//            f |= v.select( p, selection );
//        for ( View v : outputs() )
//            f |= v.select( p, selection );
//        return f || super.select( p, selection );
//    }
//
//    @Override
//    public boolean select( Rectangle r, Collection<View> selection ) {
//        if ( hit( r ) )
//            return super.select( r, selection );
//        boolean f = false;
//        for ( View v : inputs() )
//            f |= v.select( r, selection );
//        for ( View v : outputs() )
//            f |= v.select( r, selection );
//        return f;
//    }

    @Override
    public void drag( Point from, Point to, Snap snap ) {
        if ( drag == null )
            drag = new DragState( node.x, node.y );
        node.x = snap.snap( (to.x - from.x) + drag.sx );
        node.y = snap.snap( (to.y - from.y) + drag.sy );
    }

    @Override
    public void drop( boolean ok, View into ) {
        if ( !ok ) {
            node.x = drag.sx;
            node.y = drag.sy;
        }
        drag = null;
    }

//    public abstract Iterable<PortView<Port.Input>> inputs();
//
//    public abstract Iterable<PortView<Port.Output>> outputs();
}
