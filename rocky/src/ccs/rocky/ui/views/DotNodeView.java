package ccs.rocky.ui.views;

import ccs.rocky.core.Port;
import ccs.rocky.nodes.Dot;
import ccs.util.Iterabled;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class DotNodeView extends NodeView {

    private class PV<T extends Port> extends PortView<T> {

        public PV( T port, NodeView node ) {
            super( port, node );
        }

        @Override
        public int x() {
            return DotNodeView.this.x();
        }

        @Override
        public int y() {
            return DotNodeView.this.y();
        }
    }
    private final PortView<Port.Input> input;
    private final Iterable<PortView<Port.Input>> inputs;
    private final PortView<Port.Output> output;
    private final Iterable<PortView<Port.Output>> outputs;

    public DotNodeView( Dot node ) {
        super( node );
        input = new PV<Port.Input>( node.input(), this );
        inputs = new Iterabled.Element<PortView<Port.Input>>( input );
        output = new PV<Port.Output>( node.output(), this );
        outputs = new Iterabled.Element<PortView<Port.Output>>( output );
    }

    @Override
    public int rx() {
        return 4;
    }

    @Override
    public int ry() {
        return 4;
    }

    @Override
    public void paint( Graphics2D g, Collection<View> selected ) {
        g.setColor( selected.contains( this ) ? PAINT_SELECTED : PAINT_DEFAULT );
        int _x = x(), _y = y(), _rx = rx(), _ry = ry();
        g.fillOval( _x - _rx, _y - _ry, _rx * 2 + 1, _ry * 2 + 1 );
    }

    @Override
    public boolean select( Point p, Collection<View> selection ) {
        return hit( p ) && selection.add( this );
    }

    @Override
    public boolean select( Rectangle r, Collection<View> selection ) {
        return hit( r ) && selection.add( this );
    }

    @Override
    public Iterable<PortView<Port.Input>> inputs() {
        return inputs;
    }

    @Override
    public Iterable<PortView<Port.Output>> outputs() {
        return outputs;
    }
}
