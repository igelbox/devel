package ccs.rocky.ui;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;

/**
 *
 *  @author igel
 */
public class Oscilloscope extends JComponent {

    private float[] buffer;

    public void setBuffer( float[] buffer ) {
        this.buffer = buffer;
    }

    @Override
    public void paint( Graphics g ) {
        int w = getWidth(), h = getHeight(), ch = h / 2;
        g.setColor( Color.BLACK );
        g.fillRect( 0, 0, w, h );
        final float[] buff = buffer;
        if ( buff == null )
            return;
        float min = Float.MAX_VALUE, max = -min;
        for ( float v : buff ) {
            min = Math.min( min, v );
            max = Math.max( max, v );
        }
        if ( min > 0 )
            min = 0;
        if ( max < 0 )
            max = 0;
        float rl = max - min;
        if ( rl < 0.1f )
            rl = 0.1f;
        min -= rl * 0.1;
        max += rl * 0.1;
        rl = max - min;
        int c = h - (int) ((0 - min) / rl * h);
        g.setColor( Color.DARK_GRAY );
        g.drawLine( 0, c, w, c );
        int l = buff.length, ox = 0, oy = ch;
        g.setColor( Color.RED );
        for ( int i = 0; i < l; i++ ) {
            float v = buff[i];
            int x = (int) ((float) i / (float) (l - 1) * w);
            int y = h - (int) ((v - min) / rl * h);
            if ( i > 0 )
                g.drawLine( ox, oy, x, y );
            ox = x;
            oy = y;
        }
    }
}
