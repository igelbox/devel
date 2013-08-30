package ccs.rocky.ui.views;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.util.Iterabled;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class DefaultNodeView extends NodeView {

    private abstract class PV<T extends Port> extends PortView<T> {

        protected final int offset;

        public PV( T port, int offset ) {
            super( port, DefaultNodeView.this );
            this.offset = offset;
        }

        @Override
        public int y() {
            return DefaultNodeView.this.y() + offset;
        }
    }

    private class PortViewIn extends PV<Port.Input> {

        public PortViewIn( Port.Input port, int offset ) {
            super( port, offset );
        }

        @Override
        public int x() {
            return DefaultNodeView.this.x() - DefaultNodeView.this.rx() - rx();
        }
    };

    private class PortViewOut extends PV<Port.Output> {

        public PortViewOut( Port.Output port, int offset ) {
            super( port, offset );
        }

        @Override
        public int x() {
            return DefaultNodeView.this.x() + DefaultNodeView.this.rx() + rx();
        }
    };

    protected class State {

        private final int ry;
        public final PortView<Port.Input>[] inputs;
        public final PortView<Port.Output>[] outputs;

        public State( Node node ) {
            {
                Collection<Port.Input> ports = new ArrayList<Port.Input>();
                for ( Port.Input p : node.inputs() )
                    ports.add( p );
                Collection<PortView<Port.Input>> views = new ArrayList<PortView<Port.Input>>();
                int offs = UNIT / 2 - ports.size() * UNIT / 2;
                for ( Port.Input p : ports ) {
                    views.add( new PortViewIn( p, offs ) );
                    offs += UNIT;
                }
                inputs = views.toArray( EMPTY );
            }
            {
                Collection<Port.Output> ports = new ArrayList<Port.Output>();
                for ( Port.Output p : node.outputs() )
                    ports.add( p );
                Collection<PortView<Port.Output>> views = new ArrayList<PortView<Port.Output>>();
                int offs = UNIT / 2 - ports.size() * UNIT / 2;
                for ( Port.Output p : ports ) {
                    views.add( new PortViewOut( p, offs ) );
                    offs += UNIT;
                }
                outputs = views.toArray( EMPTY );
            }
            ry = Math.max( Math.max( inputs.length, outputs.length ), 1 ) * UNIT / 2;
        }
    }
    private static final PortView[] EMPTY = new PortView[0];
    private State _state;

    DefaultNodeView( Node node ) {
        super( node );
    }

    protected State state() {
        if ( _state == null )
            _state = new State( node );
        return _state;
    }

    @Override
    public int rx() {
        return 16;
    }

    @Override
    public int ry() {
        return state().ry;
    }

    @Override
    public Iterable<PortView<Port.Input>> inputs() {
        return new Iterabled.Array<PortView<Port.Input>>( state().inputs );
    }

    @Override
    public Iterable<PortView<Port.Output>> outputs() {
        return new Iterabled.Array<PortView<Port.Output>>( state().outputs );
    }
}
