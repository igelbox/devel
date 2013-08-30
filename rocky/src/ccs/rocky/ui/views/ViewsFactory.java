package ccs.rocky.ui.views;

import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.nodes.Dot;

/**
 *
 * @author igel
 */
public class ViewsFactory {

    public NodeView createView( Node node ) {
        if ( node instanceof Dot ) {
            if ( node instanceof Module.In )
                return new ModulePortView.In( (Module.In) node );
            if ( node instanceof Module.Out )
                return new ModulePortView.Out( (Module.Out) node );
            return new DotNodeView( (Dot) node );
        }
        return new DefaultNodeView( node );
    }

    public LinkView createLink( PortView<Port.Output> from, PortView<Port.Input> to ) {
        return new LinkView( from, to );
    }
}
