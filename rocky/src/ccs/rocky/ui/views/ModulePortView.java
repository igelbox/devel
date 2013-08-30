package ccs.rocky.ui.views;

import ccs.rocky.core.Module;
import ccs.rocky.core.Port;
import ccs.rocky.nodes.Dot;
import ccs.util.Iterabled;

/**
 *
 * @author igel
 */
public abstract class ModulePortView extends NodeView {

    public static class In extends ModulePortView {

        private final Iterable<PortView<Port.Output>> ports;

        public In( Module.In node ) {
            super( node );
            ports = new Iterabled.Element<PortView<Port.Output>>( new PortView<Port.Output>( node.outputs().iterator().next(), this ) {

                @Override
                public int x() {
                    return In.this.x() + In.this.rx() + rx();
                }

                @Override
                public int y() {
                    return In.this.y();
                }
            } );
        }

        @Override
        public Iterable<PortView<Port.Input>> inputs() {
            return Iterabled.emptyIterable();
        }

        @Override
        public Iterable<PortView<Port.Output>> outputs() {
            return ports;
        }
    }

    public static class Out extends ModulePortView {

        private final Iterable<PortView<Port.Input>> ports;

        public Out( Module.Out node ) {
            super( node );
            ports = new Iterabled.Element<PortView<Port.Input>>( new PortView<Port.Input>( node.inputs().iterator().next(), this ) {

                @Override
                public int x() {
                    return Out.this.x() - Out.this.rx() - rx();
                }

                @Override
                public int y() {
                    return Out.this.y();
                }
            } );
        }

        @Override
        public Iterable<PortView<Port.Input>> inputs() {
            return ports;
        }

        @Override
        public Iterable<PortView<Port.Output>> outputs() {
            return Iterabled.emptyIterable();
        }
    }

    public ModulePortView( Dot node ) {
        super( node );
    }

    @Override
    public int rx() {
        return 16;
    }

    @Override
    public int ry() {
        return UNIT / 2;
    }
}
