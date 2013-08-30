package ccs.rocky.nodes.gen;

import ccs.rocky.nodes.*;
import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.rocky.runtime.Source;
import ccs.util.Iterabled;

/**
 *
 * @author igel
 */
public class Sine extends Node {

    private static class Descr extends Descriptor<Sine> {

        @Override
        public String caption() {
            return "sin";
        }

        @Override
        public String tag() {
            return "sin";
        }

        @Override
        public Sine createNode( int id ) {
            return new Sine( id, this );
        }

        @Override
        public Sine loadNode( Loader loader ) {
            return new Sine( this, loader );
        }
    }

    private static class P extends Port.Output implements Source {

        private float[] buffer = new float[0];

        public P( Node node ) {
            super( 0, node );
        }

        @Override
        public float[] get( int samples, int samplerate, float time ) {
            if ( samples > buffer.length )
                buffer = new float[samples];
            for ( int i = 0; i < samples; i++ )
                buffer[i] = (float) Math.sin( 10.0 * (time + (double) i / (double) samplerate) * Math.PI * 2.0 );
            return buffer;
        }
    }
    public static final Descriptor<Sine> DESCRIPTOR = new Descr();
    private final Port.Output output = new P( this );

    public Sine( int id, Descriptor<?> descriptor ) {
        super( id, descriptor );
    }

    public Sine( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader );
    }

    public Port.Output output() {
        return output;
    }

    @Override
    public Iterable<Output> outputs() {
        return new Iterabled.Element<Port.Output>( output );
    }

    @Override
    public State state() {
        return State.SIGNAL;
    }
}
