package ccs.jrack.modules;

import ccs.jrack.Module;

/**
 *
 * @author igel
 */
public class Gain extends Module {

    private final Input in = new Input() {

        @Override
        public String name() {
            return "inp";
        }
    };
    private final Output out = new Output() {

        @Override
        public String name() {
            return "out";
        }
    };
    private float gain;

    @Override
    public int inputsCount() {
        return 1;
    }

    @Override
    public Input input( int index ) {
        return in;
    }

    @Override
    public int outputsCount() {
        return 1;
    }

    @Override
    public Output output( int index ) {
        return out;
    }

    @Override
    public void process() {
        Source s = in.source();
        Destination d = out.destination();
        for ( int i = d.samplesCount() - 1; i >= 0; i-- )
            d.set( i, s.get( i ) * gain );
    }
}
