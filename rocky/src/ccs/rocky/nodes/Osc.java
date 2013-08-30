package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;
import ccs.rocky.runtime.Sink;
import ccs.util.Iterabled;

/**
 *
 * @author igel
 */
public class Osc extends Node {

    public static class SinkPort extends Port.Input implements Sink {

        public final float[] buffer;

        public SinkPort( String id, Node node ) {
            super( id, node, null );
            this.buffer = new float[1024/*client.bufferSize()*/];
        }

        @Override
        public float[] buffer() {
            return buffer;
        }
    }
    public final SinkPort port;
    private final Iterable<Port.Input> inputs;

    public Osc( String id, Loader loader ) {
        super( id, loader );
        port = new SinkPort( "in", this );
        inputs = new Iterabled.Element<Port.Input>( port );
    }

    @Override
    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    public SinkPort sink() {
        return port;
    }
}
