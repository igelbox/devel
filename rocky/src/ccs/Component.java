package ccs;

import ccs.util.Cloud;

/**
 *
 * @author igel
 */
public class Component {

    public class Port {
    }

    public interface Listener {

        enum Op {

            ADD, DEL, CONNECT
        }

        void portChanged( Port port, Op mod );
    }
    private final Cloud<Listener> listeners = new Cloud<Listener>();

    public void registerListener( Listener listener ) {
        listeners.add( listener );
    }
}
