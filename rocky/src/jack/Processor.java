package jack;

import ccs.jack.Client;
import ccs.jrack.Function;
import ccs.jrack.functions.*;

/**
 *
 * @author igel
 */
public class Processor {

    public static class Input extends Function.Arg.Back {

        public final float[] buffer, bufferb;
        private int sample;

        public Input( int bs ) {
            buffer = new float[bs];
            bufferb = new float[bs];
        }

        @Override
        public float get() {
            return buffer[sample];
        }

        @Override
        public float get( int backOffset ) {
            int idx = sample - backOffset;
            if ( idx < 0 ) {
                idx += bufferb.length;
                return idx < 0 ? 0 : bufferb[idx];
            } else
                return buffer[idx];
        }
    }
    private final Function f;
    public final Input in;
    public final float[] buffer;
    public float param;

    public Processor( Client client ) {
        in = new Input( client.bufferSize() );
        buffer = new float[client.bufferSize()];
//        Function _ = new Delay( client, in, new Function.Arg.Const( 0.001f ) );
//        f = new Gain( new Function.Arg.Func( _ ), new Function.Arg.Const( 3.0f ) );
        Function.Arg p = new Function.Arg() {

            @Override
            public float get() {
                return param;
            }
        };
//        f = new Amplitude( client, in, p );
        f = new Gain( new Function.Arg.Func( new Overdrive( in, p ) ), new Function.Arg.Const( 0.5f ) );
//        f = new Overdrive( in, p );
//        f = new Gain( new Function.Arg.Func( new Overdrive( in, p ) ), new Function.Arg.Func( new Amplitude( client, in, new Function.Arg.Const( 0.015f ) ) ) );
//        f = new Compressor( client, in, p );
    }

    public void process() {
        for ( int i = buffer.length - 1; i >= 0; i-- ) {
            in.sample = i;
            buffer[i] = f.apply();
        }
        System.arraycopy( in.buffer, 0, in.bufferb, 0, buffer.length );
    }
}
