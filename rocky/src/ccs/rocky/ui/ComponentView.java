package ccs.rocky.ui;

import ccs.rocky.Node;
import ccs.rocky.Node.Port;
import java.awt.*;

/**
 *
 * @author igel
 */
public class ComponentView extends View {

    private abstract class PortView extends ccs.rocky.ui.PortView {

        protected final int offset;

        public PortView( Port port, int offset ) {
            super( port );
            this.offset = offset;
        }

        @Override
        public int y() {
            return ComponentView.this.y() + offset;
        }
    }

    private class PortViewIn extends PortView {

        public PortViewIn( Port port, int offset ) {
            super( port, offset );
        }

        @Override
        public int x() {
            return ComponentView.this.x() - ComponentView.this.rx() - rx();
        }
    };

    private class PortViewOut extends PortView {

        public PortViewOut( Port port, int offset ) {
            super( port, offset );
        }

        @Override
        public int x() {
            return ComponentView.this.x() + ComponentView.this.rx() + rx();
        }
    };
    private static final int DEFAULT_CAPTION_HEIGHT = 16;
    private static final int DEFAULT_PORT_GAP = 16;
    private static final Font FONT_TITLE = Font.decode( Font.MONOSPACED + "-BOLD" );
    private PortView[] _inps, _outs;
    public final Node component;
    public int cx, cy;

    public ComponentView( Node component ) {
        this.component = component;
        update();
    }

    @Override
    public int x() {
        return cx;
    }

    @Override
    public int y() {
        return cy;
    }

    @Override
    public int rx() {
        return 16;
    }

    @Override
    public int ry() {
        return Math.max( component.inputs().length, component.outputs().length ) * DEFAULT_PORT_GAP / 2;
    }

    public final void update() {
        Node.Port.Input[] inp = component.inputs();
        Node.Port.Output[] out = component.outputs();
        int h = ry() * 2;
        _inps = new PortViewIn[inp.length];
        {
            int[] offs = computeOffsets( inp, h );
            for ( int i = 0; i < _inps.length; i++ )
                _inps[i] = new PortViewIn( inp[i], offs[i] );
        }
        _outs = new PortViewOut[out.length];
        {
            int[] offs = computeOffsets( out, h );
            for ( int i = 0; i < _outs.length; i++ )
                _outs[i] = new PortViewOut( out[i], offs[i] );
        }
    }

    private int[] computeOffsets( Node.Port[] ports, int height ) {
        int[] result = new int[ports.length];
        if ( ports.length < 1 )
            return result;
        int d = height / ports.length, hh = height / 2;
        int yy = d / 2;
        for ( int i = 0; i < ports.length; i++ ) {
            result[i] = yy - hh;
            yy += d;
        }
        return result;
    }

    @Override
    public void paint( Graphics2D g ) {
        g.setFont( FONT_TITLE );
        FontMetrics fm = g.getFontMetrics();
        int x0 = x() - rx(), y0 = y() - ry(), w = rx() * 2 + 1, h = ry() * 2 + 1;
        int co = h / 2 + fm.getHeight() / 2 - fm.getDescent();
        g.setPaint( new GradientPaint( x0, y0, Color.LIGHT_GRAY, x0, y0 + DEFAULT_CAPTION_HEIGHT, PAINT_DEFAULT ) );
        g.fillRoundRect( x0, y0, w, h, 3, 3 );
        {
            final String caption = component.caption();
            final int sw = fm.stringWidth( caption );
            g.setColor( Color.LIGHT_GRAY );
            final int cox = x() - sw / 2, coy = y0 + co;
            g.drawString( caption, cox + 1, coy + 1 );
            g.setColor( Color.BLACK );
            g.drawString( caption, cox, coy );
        }
        for ( View v : _inps )
            v.paint( g );
        for ( View v : _outs )
            v.paint( g );
    }
}
