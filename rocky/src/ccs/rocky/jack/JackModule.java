package ccs.rocky.jack;

import ccs.jack.Client;
import ccs.jack.Jack;
import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.utils.Ports;
import ccs.rocky.persistent.Loader;
import ccs.rocky.runtime.Compilator;
import ccs.rocky.runtime.RtModule;
import ccs.rocky.runtime.Sink;
import ccs.rocky.runtime.Source;
import ccs.util.Cloud;
import ccs.util.Exceptions;
import ccs.util.Iterabled;

/**
 *
 * @author igel
 */
public class JackModule extends Module {

    public static abstract class ProcessListener {

        protected void processed( JackModule module ) {
        }
    }

    public static class Capture extends Node {

        private static class Descr extends Descriptor<Capture> {

            @Override
            public String caption() {
                return "CAPT";
            }

            @Override
            public String tag() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Capture createNode( int id ) {
                return new Capture( id, this );
            }

            @Override
            public Capture loadNode( Loader loader ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean system() {
                return true;
            }
        }

        public static class SourcePort extends Port.Output implements Source {

            private final ccs.jack.Port sPort;
            private final Client client;
            public float[] buffer = new float[0];
            private int ccount;
            private ccs.jack.Port port;

            public SourcePort( int id, Node node, Client client, ccs.jack.Port sPort ) {
                super( id, node );
                this.sPort = sPort;
                this.client = client;
            }

            @Override
            protected void connected( Input p, boolean connected ) {
                if ( connected )
                    ccount++;
                else
                    ccount--;
                if ( ccount > 0 ) {
                    if ( port == null ) {
                        port = client.registerPort( "in" + id(), ccs.jack.Port.IS_INPUT );
                        if ( client.active() )
                            client.connectPorts( sPort, port );
                    }
                } else if ( port != null ) {
                    client.unregisterPort( port );
                    port = null;
                }
            }

            @Override
            public float[] get( int samples, int samplerate, float time ) {
                if ( samples > buffer.length )
                    buffer = new float[samples];
                if ( port != null )
                    port.get( buffer, samples );
                return buffer;
            }
        }
        public static final Descriptor<Capture> DESCRIPTOR = new Descr();
        private final Ports<Port.Output> outputs = new Ports<Port.Output>();

        public Capture( int id, Descriptor<?> descriptor ) {
            super( id, descriptor );
        }

        @Override
        public Iterable<Port.Output> outputs() {
            return outputs;
        }

        @Override
        public State state() {
            return State.SIGNAL;
        }
    }

    public static class Playback extends Node {

        private static class Descr extends Descriptor<Playback> {

            @Override
            public String caption() {
                return "PLAY";
            }

            @Override
            public String tag() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Playback createNode( int id ) {
                return new Playback( id, this );
            }

            @Override
            public Playback loadNode( Loader loader ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean system() {
                return true;
            }
        }

        public static class SinkPort extends Port.Input implements Sink {

            private final ccs.jack.Port sPort;
            private final Client client;
            public final float[] buffer;
            private ccs.jack.Port port;

            public SinkPort( int id, Node node, Client client, ccs.jack.Port sPort ) {
                super( id, node );
                this.sPort = sPort;
                this.client = client;
                this.buffer = new float[client.bufferSize()];
            }

            @Override
            public float[] buffer() {
                return buffer;
            }

            @Override
            public void connect( Output to ) {
                super.connect( to );
                if ( to != null ) {
                    if ( port == null ) {
                        port = client.registerPort( "out" + id(), ccs.jack.Port.IS_OUTPUT );
                        if ( client.active() )
                            client.connectPorts( port, sPort );
                    }
                } else if ( port != null ) {
                    client.unregisterPort( port );
                    port = null;
                }
            }
        }
        public static final Descriptor<Playback> DESCRIPTOR = new Descr();
        private final Ports<Port.Input> inputs = new Ports<Port.Input>();

        public Playback( int id, Descriptor<?> descriptor ) {
            super( id, descriptor );
        }

        @Override
        public Iterable<Port.Input> inputs() {
            return inputs;
        }
    }

    public static class Oscilloscope extends Node {

        private static class Descr extends Descriptor<Oscilloscope> {

            @Override
            public String caption() {
                return "OSC";
            }

            @Override
            public String tag() {
                throw new UnsupportedOperationException();
            }

            @Override
            public Oscilloscope createNode( int id ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public Oscilloscope loadNode( Loader loader ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean system() {
                return true;
            }
        }

        public static class SinkPort extends Port.Input implements Sink {

            public final float[] buffer;

            public SinkPort( int id, Node node, Client client ) {
                super( id, node );
                this.buffer = new float[client.bufferSize()];
            }

            @Override
            public float[] buffer() {
                return buffer;
            }
        }
        public static final Descriptor<Oscilloscope> DESCRIPTOR = new Descr();
        public final SinkPort port;
        private final Iterable<Port.Input> inputs;

        public Oscilloscope( Client client ) {
            super( -3, DESCRIPTOR );
            port = new SinkPort( 0, this, client );
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
    private final Client client = new Client( "Rocky" ) {

        private final Compilator compilator = new Compilator();
        private final Module.Listener ml = new Module.Listener() {

            @Override
            protected void flow( Module module ) {
                Class<? extends RtModule> cls = compilator.compile( module );
                try {
                    rt = cls.getDeclaredConstructor( Module.class ).newInstance( module );
                    System.out.println( rt );
                } catch ( Throwable e ) {
                    throw Exceptions.wrap( e );
                }
            }
        };
        private final long st = Jack.getTime();
        private RtModule rt;
        private long lastProcess;

        {
            JackModule.this.listen( ml );
        }

        @Override
        protected int process( int samples ) {
            try {
                long t = System.currentTimeMillis();
                if ( rt == null )
                    return 0;
                rt.process( samples, sampleRate(), (float) (Jack.getTime() - st) / 1E6f );
                for ( Port.Input i : playback.inputs() ) {
                    Playback.SinkPort s = (Playback.SinkPort) i;
                    if ( s.port != null )
                        s.port.set( s.buffer, s.buffer.length );
                }
                for ( ProcessListener l : listeners )
                    l.processed( JackModule.this );
                if ( lastProcess != 0 ) {
                    long te = System.currentTimeMillis();
                    float l = (float) (te - t) / (float) (t - lastProcess);
                    load = load * 0.9f + l * 0.1f;
                }
                lastProcess = t;
                return 0;
            } catch ( Throwable t ) {
                t.printStackTrace();
                return 1;
            }
        }
    };
    private final Cloud<ProcessListener> listeners = new Cloud<ProcessListener>();
    private final Capture capture;
    private final Playback playback;
    public final Oscilloscope oscilloscope;
    private float load;

    public JackModule() {
        oscilloscope = new Oscilloscope( client );
        add( oscilloscope );
        Capture cpt = null;
        Playback plb = null;
        for ( ccs.jack.Port p : client.findAllPorts() ) {
            String c = p.clientName();
            if ( "system".equals( c ) ) {
                String[] _ = p.name().split( "_" );
                if ( "capture".equals( _[0] ) ) {
                    if ( cpt == null ) {
                        cpt = Capture.DESCRIPTOR.createNode( -1 );
                        add( cpt );
                    }
                    Port.Output o = new Capture.SourcePort( cpt.outputs.size(), cpt, client, p );
                    cpt.outputs.add( o );
                } else if ( "playback".equals( _[0] ) ) {
                    if ( plb == null ) {
                        plb = Playback.DESCRIPTOR.createNode( -2 );
                        add( plb );
                    }
                    plb.inputs.add( new Playback.SinkPort( plb.inputs.size(), plb, client, p ) );
                } else
                    System.err.println( p.fullName() );
            } else
                System.err.println( p.fullName() );
        }
        this.capture = cpt;
        this.playback = plb;
        client.activate();
    }

    public void listen( ProcessListener l ) {
        listeners.add( l );
    }

    @Override
    public void remove( Node node ) {
        if ( node == capture )
            return;
        if ( node == playback )
            return;
        super.remove( node );
    }

    public Capture capture() {
        return capture;
    }

    public Playback playback() {
        return playback;
    }

    public boolean active() {
        return client.active();
    }

    public void activate( boolean a ) {
        if ( a ) {
            client.activate();
            for ( Port.Output o : capture.outputs() ) {
                Capture.SourcePort p = (Capture.SourcePort) o;
                if ( (p.port != null) )
                    client.connectPorts( p.sPort, p.port );
            }
            for ( Port.Input i : playback.inputs() ) {
                Playback.SinkPort p = (Playback.SinkPort) i;
                if ( (p.port != null) )
                    client.connectPorts( p.port, p.sPort );
            }
        } else {
            for ( Port.Output o : capture.outputs() ) {
                Capture.SourcePort p = (Capture.SourcePort) o;
                if ( (p.port != null) )
                    client.disconnectPorts( p.sPort, p.port );
            }
            for ( Port.Input i : playback.inputs() ) {
                Playback.SinkPort p = (Playback.SinkPort) i;
                if ( (p.port != null) )
                    client.disconnectPorts( p.port, p.sPort );
            }
            client.deactivate();
        }
    }

    public float load() {
        return load;
    }
}
