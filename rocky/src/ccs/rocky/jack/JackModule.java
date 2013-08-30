package ccs.rocky.jack;

import ccs.jack.Client;
import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.core.utils.Ports;
import ccs.rocky.persistent.Loader;
import ccs.rocky.runtime.*;
import ccs.util.Cloud;

/**
 *
 * @author igel
 */
public class JackModule extends Module {

    public static abstract class ProcessListener {

        protected void processed( JackModule module ) {
        }
    }

//    @Node.Descriptor( caption = "CAPT", system = true )
    public static class Capture extends Node {

        public static class SourcePort extends Port.Output.FixedState implements Source {

            private final ccs.jack.Port sPort;
            private final Client client;
            public float[] buffer;
            private int ccount;
            private ccs.jack.Port port;

            public SourcePort( String id, Node node, Client client, ccs.jack.Port sPort ) {
                super( id, node, null, Port.State.SIGNAL );
                this.sPort = sPort;
                this.client = client;
                this.buffer = new float[client.bufferSize()];
            }

//            @Override
//            protected void connected( Input p, boolean connected ) {
//                if ( connected )
//                    ccount++;
//                else
//                    ccount--;
//                if ( ccount > 0 ) {
//                    if ( port == null ) {
//                        port = client.registerPort( "in" + id(), ccs.jack.Port.IS_INPUT );
//                        if ( client.active() )
//                            client.connectPorts( sPort, port );
//                    }
//                } else if ( port != null ) {
//                    client.unregisterPort( port );
//                    port = null;
//                }
//            }
            @Override
            public float[] get( float time ) {
                if ( port != null )
                    port.get( buffer, buffer.length );
                return buffer;
            }
        }
        private final Ports<Port.Output> outputs = new Ports<Port.Output>();

        public Capture() {
            super( "#capture", Loader.VOID );
        }

        @Override
        public Iterable<Port.Output> outputs() {
            return outputs;
        }
    }

//    @Node.Descriptor( caption = "PLAY", system = true )
    public static class Playback extends Node {

        public static class SinkPort extends Port.Input implements Sink {

            private final ccs.jack.Port sPort;
            private final Client client;
            public final float[] buffer;
            private ccs.jack.Port port;

            public SinkPort( String id, Node node, Client client, ccs.jack.Port sPort ) {
                super( id, node, null );
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
                        port = client.registerPort( "out" + id, ccs.jack.Port.IS_OUTPUT );
                        if ( client.active() )
                            client.connectPorts( port, sPort );
                    }
                } else if ( port != null ) {
                    client.unregisterPort( port );
                    port = null;
                }
            }
        }
        private final Ports<Port.Input> inputs = new Ports<Port.Input>();

        public Playback() {
            super( "#playback", Loader.VOID );
        }

        @Override
        public Iterable<Port.Input> inputs() {
            return inputs;
        }
    }
//    private final Client client = new Client( "Rocky" ) {
////        private final Module.Listener ml = new Module.Listener() {
////            @Override
////            protected void flow( Module module ) {
////                if ( loading )
////                    return;
////                try {
////                    recompile();
////                } catch ( Throwable e ) {
////                    throw Exceptions.wrap( e );
////                }
////            }
////        };
//        private long lastProcess;
//        private float time;
//
////        {
////            JackModule.this.listen( ml );
////        }
//
//        @Override
//        protected int process( int samples ) {
//            try {
//                long t = System.currentTimeMillis();
//                if ( runtime == null )
//                    return 0;
//                runtime.process( time );
//                time += samples / (float) client.sampleRate();
//                for ( Port.Input i : playback.inputs() ) {
//                    Playback.SinkPort s = (Playback.SinkPort) i;
//                    if ( s.port != null )
//                        s.port.set( s.buffer, s.buffer.length );
//                }
//                for ( ProcessListener l : listeners )
//                    l.processed( JackModule.this );
//                if ( lastProcess != 0 ) {
//                    long te = System.currentTimeMillis();
//                    float l = (float) (te - t) / (float) (t - lastProcess);
//                    load = load * 0.9f + l * 0.1f;
//                }
//                lastProcess = t;
//                return 0;
//            } catch ( Throwable t ) {
//                t.printStackTrace();
//                return 1;
//            }
//        }
//    };
    private final Cloud<ProcessListener> listeners = new Cloud<ProcessListener>();
    private final Capture capture;
    private final Playback playback;
    private final Compilator compilator = new Compilator();
    private RtModule runtime;
//    public final Oscilloscope oscilloscope;
    private boolean loading;
    private float load;

    public JackModule() {
//        oscilloscope = new Oscilloscope( client );
//        add( oscilloscope );
        Capture cpt = null;
        Playback plb = null;
//        for ( ccs.jack.Port p : client.findAllPorts() ) {
//            String c = p.clientName();
//            if ( "system".equals( c ) ) {
//                String[] _ = p.name().split( "_" );
//                if ( "capture".equals( _[0] ) ) {
//                    if ( cpt == null ) {
//                        cpt = new Capture();
//                        add( cpt );
//                    }
//                    Port.Output o = new Capture.SourcePort( _[1], cpt, client, p );
//                    cpt.outputs.add( o );
//                } else if ( "playback".equals( _[0] ) ) {
//                    if ( plb == null ) {
//                        plb = new Playback();
//                        add( plb );
//                    }
//                    plb.inputs.add( new Playback.SinkPort( _[1], plb, client, p ) );
//                } else
//                    System.err.println( p.fullName() );
//            } else
//                System.err.println( p.fullName() );
//        }
        this.capture = cpt;
        this.playback = plb;
//        client.activate();
    }

    private void recompile() throws Throwable {
//        Class<? extends RtModule> cls = compilator.compile( this, client.bufferSize(), client.sampleRate() );
//        runtime = cls.getDeclaredConstructor( Module.class ).newInstance( this );
        System.out.println( runtime );
    }

    @Override
    public void load( Loader loader ) {
        loading = true;
        try {
            super.load( loader );
            try {
                recompile();
            } catch ( Throwable t ) {
                throw new RuntimeException( t );
            }
        } finally {
            loading = false;
        }
    }

    public void listen( ProcessListener l ) {
        listeners.add( l );
    }

    public Capture capture() {
        return capture;
    }

    public Playback playback() {
        return playback;
    }

//    public boolean active() {
//        return client.active();
//    }
//    public void activate( boolean a ) {
//        if ( a ) {
//            client.activate();
//            for ( Port.Output o : capture.outputs() ) {
//                Capture.SourcePort p = (Capture.SourcePort) o;
//                if ( (p.port != null) )
//                    client.connectPorts( p.sPort, p.port );
//            }
//            for ( Port.Input i : playback.inputs() ) {
//                Playback.SinkPort p = (Playback.SinkPort) i;
//                if ( (p.port != null) )
//                    client.connectPorts( p.port, p.sPort );
//            }
//        } else {
//            for ( Port.Output o : capture.outputs() ) {
//                Capture.SourcePort p = (Capture.SourcePort) o;
//                if ( (p.port != null) )
//                    client.disconnectPorts( p.sPort, p.port );
//            }
//            for ( Port.Input i : playback.inputs() ) {
//                Playback.SinkPort p = (Playback.SinkPort) i;
//                if ( (p.port != null) )
//                    client.disconnectPorts( p.port, p.sPort );
//            }
//            client.deactivate();
//        }
//    }
    public float load() {
        return load;
    }
}
