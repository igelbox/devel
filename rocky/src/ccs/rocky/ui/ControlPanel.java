package ccs.rocky.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author igel
 */
public class ControlPanel extends JPanel {

    private static class JMeter extends JComponent {

        private float load;

        public JMeter() {
            setPreferredSize( new Dimension( 64, 16 ) );
            setToolTipText( "Load average" );
        }

        @Override
        public void paint( Graphics g ) {
            int w = getWidth(), h = getHeight(), r = w - 1, b = h - 1;
            g.setColor( Color.BLACK );
            g.fillRect( 0, 0, w, h );
            g.setColor( Color.getHSBColor( (1f - load) / 3f, 1, 1 ) );
            g.fillRect( 0, 0, (int) (w * load), h );
            g.setColor( Color.GRAY );
            g.drawLine( 0, b, 0, 0 );
            g.drawLine( 0, 0, r, 0 );
            g.setColor( Color.WHITE );
            g.drawLine( r, 0, r, b );
            g.drawLine( r, b, 0, b );
        }

        public void load( float load ) {
            this.load = load;
        }
    }
    private final JMeter meter = new JMeter();

    public ControlPanel() {
        super( new FlowLayout( FlowLayout.RIGHT ) );
        setOpaque( false );
//        add( load );
        add( meter );
    }

    public void load( float l ) {
        meter.load( l );
    }

    @Override
    public void paint( Graphics g ) {
        super.paintChildren( g );
    }
}
