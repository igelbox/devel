package ccs.rocky.ui;

import ccs.util.FFT;
import java.awt.*;
import javax.swing.JComponent;

/**
 *
 * @author igel
 */
public class Oscilloscope extends JComponent {

    private static final Color C_BOUND = Color.DARK_GRAY;
    private static final Color C_ZERO = Color.getHSBColor( 0, 0, 0.125f );
    private static final Color C_SIGNAL = Color.getHSBColor( 2f / 3f, 1, 0.75f );
    private static final float[] MRK = new float[]{ 1f, 2.5f, 5f };
    private long last;
    private float[] buffer;
    private float min = -1, max = 1, amp = 2;
    private int offs;
    private volatile boolean calcNeed = false;

    public void setBuffer( float[] buffer ) {
        this.buffer = buffer;
        calcNeed = true;
    }

    private void calc() {
        calcNeed = false;
        long t = System.currentTimeMillis();
        float mn = Float.MAX_VALUE, mx = -mn;
        for ( float v : buffer ) {
            mn = Math.min( mn, v );
            mx = Math.max( mx, v );
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
            dt *= 3f;
        dt *= 2f;
        if ( dt > 1f )
            dt = 1f;
        min = min * (1f - dt) + mn * dt;
        max = max * (1f - dt) + mx * dt;
        amp = max - min;
        float[] fft = FFT.fft( buffer );
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
            System.out.println( mi + ":" + offs );
        } else
            offs = 0;
        last = t;
    }

    @Override
    public void paint( Graphics g ) {
        Graphics2D gg = (Graphics2D) g;
        int w = getWidth(), h = getHeight(), ch = h / 2;
        g.setColor( Color.BLACK );
        g.fillRect( 0, 0, w, h );
        final float[] buff = buffer;
        if ( buff == null )
            return;
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
        int l = buff.length;
        int ox = 0, oy = ch;
        g.setColor( C_SIGNAL );
        for ( int i = 0; i < l; i++ ) {
            float v = buff[i];
            int x = (int) ((float) (i + offs) / (float) (l - 1) * w);
            int y = project( v, h );
            if ( i > 0 )
                g.drawLine( ox, oy, x, y );
            ox = x;
            oy = y;
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
