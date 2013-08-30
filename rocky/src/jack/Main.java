package jack;

//import ccs.jrack.Filter;
//import ccs.jac.Jack;
//import ccs.jac.Client;
//import ccs.jrack._filters.*;
//import ccs.jack.Client;
//import ccs.jack.Jack;
//import java.awt.*;
import ccs.rocky.ui.MainForm;
import ccs.MainFormX;
import javax.swing.JFrame;

/**
 *
 * @author igel
 */
public class Main extends JFrame {

//    static long count = 0;
//    static final Filter[] ff = new Filter[]{
//        //        new Generator( 10.0f ),
//        new Gate( 0.03f ),
//        //        new Normalize(),
//        new Clm(),
//        //        new Clamper( 0.2f ),
//        //  <<<<<<<<<<<      new Gain( 5.0f ),
//        new Blur( 5 ),
//        //        new Echo( 128 ),
//        //        new Echo( 800 ),
//        new Normalize()
//    };
//    static final Filter[] ff = new Filter[]{
//        new Gate( 0.03f ),
//        new Normalize(),
//        new Clamper( 0.2f ),
//        new Gain( 5.0f ),
//        new Blur( 10 ),
//        new Echo( 256 ),
//        new Echo( 666 ),
//        new Normalize()
//    };

//    public Main() throws HeadlessException {
//        super( "rocky" );
//        setDefaultCloseOperation( EXIT_ON_CLOSE );
//        JPanel p = new JPanel() {
//
//            void paintBuff( float[] buff, Graphics g ) {
//                int hh = getHeight() / 2;
//                float amp = 0.0f;
//                for ( float s : buff )
//                    amp = Math.max( amp, Math.abs( s ) );
//                amp = 1.0f;
//                amp /= (float) hh;
////                if ( amp == 0.0f )
////                    amp = 1.0f;
//                for ( int i = 1; i < buff.length; i++ ) {
//                    float v0 = hh - (buff[i - 1] - 0) / amp;
//                    float v1 = hh - (buff[i] - 0) / amp;
//                    g.drawLine( i - 1, (int) v0, i, (int) v1 );
//                }
//            }
//
//            @Override
//            public void paint( Graphics g ) {
//                g.setColor( Color.BLACK );
//                g.fillRect( 0, 0, getWidth(), getHeight() );
//                g.setColor( Color.BLUE );
//                g.drawLine( 0, getHeight() / 2, getWidth(), getHeight() / 2 );
//                g.setColor( Color.GREEN );
//                paintBuff( buffi, g );
//                g.setColor( Color.RED );
//                paintBuff( buffo, g );
//            }
//        };
//        p.setPreferredSize( new Dimension( c.bufferSize(), 600 ) );
//        add( p );
//        add( new JSlider( new DefaultBoundedRangeModel( 1, 1, 1, 100 ) {
//
//            @Override
//            public void setValue( int n ) {
//                super.setValue( n );
////                proc.param = (float) n / 100.0f * 0.015f;
//                proc.param = (float) Math.pow( 2.3, n );
//            }
//        } ), BorderLayout.SOUTH );
//        pack();
//        setVisible( true );
//    }
//    static float[] buffi;
//    static float[] buffo;
//    private static Client c;
//    private static Processor proc;
//    static Main main;

    public static void main( String[] args ) throws Throwable {
        MainForm.main();
//        c = new Client( "rocky" ) {
//
//            final long in = registerPort( "in", Jack.PORT_IS_INPUT );
//            final long out = registerPort( "out", Jack.PORT_IS_OUTPUT );
//            final float[] buffer = new float[bufferSize()];
//            long lr;
//
//            @Override
//            protected int process( int samples ) {
////                count++;
////                Jack.get_buffer( in, buffer, 0, samples );
////                System.arraycopy( buffer, 0, buffi, 0, samples );
////                for ( Filter f : ff )
////                    f.filter( buffer );
////                Jack.set_buffer( out, buffer, 0, samples );
////                System.arraycopy( buffer, 0, buffo, 0, samples );
//                long t = System.currentTimeMillis();
//                if ( (t - lr) > 10 ) {
//                    if ( main != null )
//                        main.repaint();
//                    lr = t;
//                }
//                Jack.get_buffer( in, proc.in.buffer, 0, samples );
////                for ( int i = 0; i < proc.in.buffer.length - 1; i++ )
////                    proc.in.buffer[i] = 0.75f*(float) Math.sin( (double) i / (double)proc.in.buffer.length * Math.PI * 5.0 );
//                proc.process();
//                Jack.set_buffer( out, proc.buffer, 0, samples );
//                System.arraycopy( proc.in.buffer, 0, buffi, 0, samples );
//                System.arraycopy( proc.buffer, 0, buffo, 0, samples );
//                return 0;
//            }
//        };
//        try {
//            proc = new Processor( c );
//            c.activate();
//            buffi = new float[c.bufferSize()];
//            buffo = new float[c.bufferSize()];
//            main = new Main();
//            Thread.sleep( 1000000 );
////            System.out.println( (double)count * c.bufferSize() / c.sampleRate() );
//        } finally {
//            c.close();
//        }
//        Client client = new Client( "rocky" );
//        System.out.println(client);
//        Thread.sleep( 1000 );
//        client.close();
    }
}
