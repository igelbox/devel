package ccs.rocky.ui;

import ccs.rocky.Node.Port;
import java.awt.Color;
import java.awt.Graphics2D;

/**
 *
 * @author igel
 */
public abstract class PortView extends View {

    protected final Port port;

    public PortView( Port port ) {
        this.port = port;
    }

    public Port port() {
        return port;
    }

    @Override
    public int rx() {
        return 3;
    }

    @Override
    public int ry() {
        return 2;
    }

    @Override
    public void paint( Graphics2D g ) {
        final int x = x(), y = y(), rx = rx(), ry = ry(), w = rx * 2 + 1, h = ry * 2 + 1;
        g.setColor( PAINT_DEFAULT );
        g.fillRect( x - rx, y - ry, w, h );
        g.setColor( Color.DARK_GRAY );
        g.drawLine( x - rx + 1, y, x + rx - 1, y );
//            final String s = port.name();
//            g.setColor( Color.GRAY );
//            final FontMetrics fm = g.getFontMetrics();
//            final int _sw = fm.stringWidth( s );
//            g.drawString( s, x - 2 - _sw, y - fm.getDescent() - 2 );
    }
}
