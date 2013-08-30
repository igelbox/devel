package ccs.rocky.nodes;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.Port.Output;
import ccs.rocky.core.utils.Ports;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.rocky.runtime.Generatable;
import ccs.util.Iterabled;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author igel
 */
public class Buff extends Node implements Generatable {

    private final Port.Input input = new Port.Input( "in", this, null );
    private final Iterable<Port.Input> inputs = new Iterabled.Element<Port.Input>( input );
    private final Ports<Port.Output> outputs = new Ports<Port.Output>();
    private final Port.Output max = new Port.Output( "max", this, null );
    private final Port.Output min = new Port.Output( "min", this, null );
    private final Port.Output val = new Port.Output( "out", this, null );
    private final Port.Output amp = new Port.Output( "amp", this, null );
    private final Generatable.Generator gen = new Generatable.Generator.Fieldable() {
        int n, s, sr;

        @Override
        public void gen_prolog( MethodVisitor mv, Locals locals, int samples, int samplerate ) {
            pushNode( mv, locals, Buff.this );
            n = locals.newVar();
            s = locals.sampleVar();
            sr = samplerate;
            mv.visitInsn( Opcodes.DUP );
            mv.visitVarInsn( Opcodes.ASTORE, n );
            mv.visitLdcInsn( sr );
            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, Type.getInternalName( Buff.class ), "rt_buffer", "(I)[F" );
            inArr = locals.newVar();
            mv.visitVarInsn( Opcodes.ASTORE, inArr );
        }

        @Override
        public void gen_inloop( MethodVisitor mv, Output out ) {
            mv.visitVarInsn( Opcodes.ALOAD, n );
            mv.visitVarInsn( Opcodes.ILOAD, s );
            mv.visitLdcInsn( sr );
            if ( out == min )
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, Type.getInternalName( Buff.class ), "rt_min", "(II)F" );
            else if ( out == max )
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, Type.getInternalName( Buff.class ), "rt_max", "(II)F" );
            else if ( out == val )
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, Type.getInternalName( Buff.class ), "rt_val", "(II)F" );
            else if ( out == amp )
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, Type.getInternalName( Buff.class ), "rt_amp", "(II)F" );
        }
    };

    private static class B {

        final float[] data;
        final float min, max, amp;

        public B( int sz ) {
            data = new float[sz];
            this.min = this.max = this.amp = 0;
        }

        public B( float[] src ) {
            data = new float[src.length];
            System.arraycopy( src, 0, data, 0, data.length );
            float mn = Float.MAX_VALUE, mx = -mn, am = 0;
            for ( float f : data ) {
                mn = Math.min( mn, f );
                mx = Math.max( mx, f );
                am += Math.abs( f );
            }
            this.max = mx;
            this.min = mn;
            this.amp = am / data.length;
        }
    }
    private final float[] buffer = new float[512], obuffer = new float[512];
    private B[] buffers;
    private int inArr;
    private float delay;

    public Buff( String id, Loader loader ) {
        super( id, loader );
        Loader.Attribute a = loader.findAttribute( "delay" );
        if ( a != null )
            this.delay = a.asFloat();
        outputs.add( max );
        outputs.add( min );
        outputs.add( val );
        outputs.add( amp );
    }

    @Override
    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    public Port.Output max() {
        return max;
    }

    public Port.Output min() {
        return min;
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    @Param
    public float delay() {
        return delay;
    }

    public void delay( float delay ) {
        this.delay = delay;
    }

    @Override
    public void store( Storer storer ) {
        super.store( storer );
        storer.putFloat( "delay", delay );
    }

    @Override
    public Generator generator() {
        return gen;
    }

    public float[] rt_buffer( int samplerate ) {
        int c = (int) (delay * samplerate / buffer.length) + 2;
        if ( (buffers == null) || (buffers.length != c) ) {
            buffers = new B[c];
            for ( int i = 0; i < c; i++ )
                buffers[i] = new B( buffer.length );
        }
        return buffer;
    }

    public void rt_begin() {
//        System.arraycopy( buffer, 0, obuffer, 0, buffer.length );
    }

    public void rt_frame() {
        for ( int i = buffers.length - 1; i > 0; i-- )
            buffers[i] = buffers[i - 1];
        buffers[0] = new B( buffer );
    }

    private float get( int s ) {
        int i = 0;
        B b = buffers[i];
        while ( s < 0 ) {
            b = buffers[++i];
            s += b.data.length;
        }
        return b.data[s];
    }

    public float rt_val( int s, int sr ) {
        s -= (int) (delay * sr);
        return get( s );
    }

    public float rt_min( int s, int sr ) {
        int s0 = s - (int) (delay * sr);
        int bi = 0;
        B b = buffers[bi];
        float r = b.data[s];
        if ( s0 < 0 ) {
            for ( int i = s - 1; i >= 0; i-- )
                r = Math.min( r, b.data[i] );
            b = buffers[++bi];
            while ( s0 < -b.data.length ) {
                r = Math.min( r, b.min );
                s0 += b.data.length;
                b = buffers[++bi];
            }
            s0 += b.data.length;
            for ( int i = b.data.length - 1; i >= s0; i-- )
                r = Math.min( r, b.data[i] );
        } else
            for ( int i = s - 1; i >= s0; i-- )
                r = Math.min( r, b.data[i] );
        return r;
    }

    public float rt_max( int s, int sr ) {
        int s0 = s - (int) (delay * sr);
        int bi = 0;
        B b = buffers[bi];
        float r = b.data[s];
        if ( s0 < 0 ) {
            for ( int i = s - 1; i >= 0; i-- )
                r = Math.max( r, b.data[i] );
            b = buffers[++bi];
            while ( s0 < -b.data.length ) {
                r = Math.max( r, b.max );
                s0 += b.data.length;
                b = buffers[++bi];
            }
            s0 += b.data.length;
            for ( int i = b.data.length - 1; i >= s0; i-- )
                r = Math.max( r, b.data[i] );
        } else
            for ( int i = s - 1; i >= s0; i-- )
                r = Math.max( r, b.data[i] );
        return r;
    }

    public float rt_amp( int s, int sr ) {
        int s0 = s - (int) (delay * sr);
        int bi = 0;
        B b = buffers[bi];
        float r = 0;
        int c = 0;
        if ( s0 < 0 ) {
            for ( int i = s; i >= 0; i-- )
                r += Math.abs( b.data[i] );
            c += (s + 1);
            b = buffers[++bi];
            while ( s0 < -b.data.length ) {
                int l = b.data.length;
                r += Math.abs( b.amp ) * l;
                c += l;
                s0 += l;
                b = buffers[++bi];
            }
            s0 += b.data.length;
            for ( int i = b.data.length - 1; i >= s0; i-- )
                r += Math.abs( b.data[i] );
            c += (b.data.length - s0);
        } else {
            for ( int i = s - 1; i >= s0; i-- )
                r += Math.abs( b.data[i] );
            c += (s - s0);
        }
        return r / c;
    }

    public int inArrLocalNo( Port.Input i ) {
        return inArr;
    }
}
