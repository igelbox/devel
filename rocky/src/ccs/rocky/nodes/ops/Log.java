package ccs.rocky.nodes.ops;

import ccs.rocky.core.Port;
import ccs.rocky.persistent.Loader;

/**
 *
 * @author igel
 */
public class Log extends AbstractOp {

    private static class Descr extends Descriptor<Log> {

        @Override
        public String caption() {
            return "log";
        }

        @Override
        public String tag() {
            return "log";
        }

        @Override
        public Log createNode( int id ) {
            return new Log( id, this );
        }

        @Override
        public Log loadNode( Loader loader ) {
            return new Log( this, loader );
        }
    }
    public static final Descriptor<Log> DESCRIPTOR = new Descr();
    private final Port.Input input = new Port.Input( 0, this );

    public Log( int id, Descriptor<?> descriptor ) {
        super( id, descriptor, "log" );
        inputs.add( input );
    }

    public Log( Descriptor<?> descriptor, Loader loader ) {
        super( descriptor, loader, "log" );
        inputs.add( input );
    }

    public Port.Input input() {
        return input;
    }
}
