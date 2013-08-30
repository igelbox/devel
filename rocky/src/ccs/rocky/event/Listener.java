package ccs.rocky.event;

import ccs.rocky.Node.Port;

/**
 *
 * @author igel
 */
public interface Listener {

    interface PortRemove extends Listener {

        void portRemoved( Port port );
    }

    interface PortConnect extends Listener {

        void portConnected( Port.Input port, Port.Output to );
    }
}
