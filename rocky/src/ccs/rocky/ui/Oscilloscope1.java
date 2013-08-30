package ccs.rocky.ui;

import ccs.util.FFT;
import java.awt.*;
import java.util.Arrays;
import javax.swing.JComponent;

/**
 *
 * @author igel
 */
public class Oscilloscope1 extends JComponent {

    private static class Tail {

        private static class Buffer {

            private final float[] data;
            private boolean calced;
            private float min, max;

            public Buffer( float[] data ) {
                this.data = data;
            }

            private void upcalc() {
                if ( calced )
                    return;
                float mn = Float.MAX_VALUE, mx = -mn;
                for ( float v : data ) {
                    mn = Math.min( mn, v );
                    mx = Math.max( mx, v );
                }
                min = mn;
                max = mx;
                calced = true;
            }
        }
        private Buffer[] buffers = new Buffer[0];
        private int samples;

        public void append( float[] buffer ) {
            int d = samples >= 2048 ? 0 : 1;
            Buffer[] tmp = new Buffer[buffers.length + d];
            if ( d == 0 )
                samples -= buffers[buffers.length - 1].data.length;
            for ( int i = tmp.length - 1; i >= 1; i-- )
                tmp[i] = buffers[i - 1];
            tmp[0] = new Buffer( buffer );
            samples += buffer.length;
            buffers = tmp;
        }
    }
    private static final Color C_BOUND = Color.DARK_GRAY;
    private static final Color C_ZERO = Color.getHSBColor( 0, 0, 0.125f );
    private static final Color C_SIGNAL = Color.getHSBColor( 2f / 3f, 1, 0.75f );
    private static final float[] MRK = new float[]{ 1f, 2.5f, 5f };
    private long last;
    private final Tail tail = new Tail();
    private float min = -1, max = 1, amp = 2;
    private int offs;
    private volatile boolean calcNeed = false;

    public void setBuffer( float[] buffer ) {
        tail.append( Arrays.copyOf( buffer, buffer.length ) );
        calcNeed = true;
    }

    private void calc() {
        calcNeed = false;
        long t = System.currentTimeMillis();
        Tail.Buffer[] bfs = tail.buffers;
        float mn = Float.MAX_VALUE, mx = -mn;
        for ( Tail.Buffer b : bfs ) {
            b.upcalc();
            mn = Math.min( mn, b.min );
            mx = Math.max( mx, b.max );
        }
        if ( mn > 0f )
            mn = 0f;
        if ( mx < 0f )
            mx = 0f;
        float d = mx - mn;
        if ( d < 0.1f )
            d = 0.1f;
        mn -= d * 0.1f;
        mx += d * 0.1f;
        float dt = (float) (t - last) / 1E3f;
        if ( (mn < min) || (mx > max) )
            dt *= 10f;
//        dt *= 2f;
        if ( dt > 1f )
            dt = 1f;
        if ( Float.isNaN( min ) || Float.isInfinite( min ) )
            min = mn;
        else
            min = min * (1f - dt) + mn * dt;
        if ( Float.isNaN( max ) || Float.isInfinite( max ) )
            max = mx;
        else
            max = max * (1f - dt) + mx * dt;
        amp = max - min;
        int samples = 0;
        for ( Tail.Buffer b : bfs )
            samples += b.data.length;
        float[] buffer = new float[samples];
        int bofs = samples;
        for ( Tail.Buffer b : bfs ) {
            bofs -= b.data.length;
            int i = 0;
            for ( float v : b.data )
                buffer[bofs + (i++)] = v;
            if ( bofs == 0 )
                break;
        }
        float[] fft = FFT.fft( buffer );
        if ( fft != null ) {
            float ma = -1;
            int mi = 0;
            for ( int i = 0; i < fft.length / 2; i += 2 ) {
                float re = fft[i], im = fft[i + 1];
                float a = re * re + im * im;
                if ( a > ma ) {
                    ma = a;
                    mi = i;
                }
            }
            if ( mi > 0 ) {
                float re = fft[mi], im = fft[mi + 1];
                float a = (float) Math.sqrt( re * re + im * im );
                re /= a;
                im /= a;
                float am = (float) (Math.atan2( re, im ) / Math.PI);
                offs = (int) (buffer.length / mi * am);
            } else
                offs = 0;
        }
        last = t;
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D gg = (Graphics2D) g;
        int w = getWidth(), h = getHeight(), ch = h / 2;
        g.setColor( Color.BLACK );
        g.fillRect( 0, 0, w, h );
        if ( calcNeed )
            calc();
        ((Graphics2D) g).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g.setColor( C_ZERO );
        {
            int y = project( 0, h );
            g.drawLine( 0, y, w, y );
        }
        drawMarkers( 0.01f, gg );
        drawMarkers( -0.01f, gg );
        ((Graphics2D) g).setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        int ox = 0, oy = ch, xp = w + offs;
        g.setColor( C_SIGNAL );
        for ( Tail.Buffer b : tail.buffers ) {
            xp -= b.data.length;
            if ( xp > w )
                continue;
            for ( int i = b.data.length - 1; i >= 0; i-- ) {
                float v = b.data[i];
                int x = xp + i;
                int y = project( v, h );
                if ( ox > 0 )
                    g.drawLine( ox, oy, x, y );
                ox = x;
                oy = y;
            }
            if ( xp < 0 )
                break;
        }
    }

    private int project( float y, int h ) {
        return h - (int) ((y - min) / amp * h);
    }

    private void drawMarker( float v, Graphics2D g, Color c ) {
        g.setColor( c );
        FontMetrics fm = g.getFontMetrics();
        String s = String.format( "%+.2f", v );
        int x = fm.stringWidth( s );
        int y = project( v, getHeight() );
        g.drawString( s, 0, y - fm.getDescent() + fm.getHeight() / 2 );
        g.drawLine( x + 2, y, getWidth(), y );
    }

    private void drawMarkers( float m, Graphics2D g ) {
        if ( (m < 0) && (Float.isNaN( min ) || Float.isInfinite( min )) )
            return;
        if ( (m > 0) && (Float.isNaN( max ) || Float.isInfinite( max )) )
            return;
        float lf = 0f, llf = 0f;
        int idx = 0;
        while ( true ) {
            float f = m * MRK[idx];
            if ( (f < min) || (max < f) )
                break;
            idx++;
            if ( idx == MRK.length ) {
                idx = 0;
                m *= 10f;
            }
            llf = lf;
            lf = f;
        }
        drawMarker( lf, g, C_BOUND );
        drawMarker( llf, g, C_ZERO );
    }
}
