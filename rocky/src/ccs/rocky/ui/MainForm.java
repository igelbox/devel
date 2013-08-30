package ccs.rocky.ui;

import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.utils.Storer;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.Writer;
import javax.swing.*;

/**
 *
 * @author igel
 */
public class MainForm extends JFrame {

    private static class Act extends AbstractAction {

        private final Class<?> cls;
        private final Module m;

        public Act( Class<?> cls, Module m ) {
            super( cls.getSimpleName() );
            this.cls = cls;
            this.m = m;
        }

        @Override
        public void actionPerformed( ActionEvent e ) {
            try {
                Node n = (Node) cls.newInstance();
                m.add( n );
            } catch ( Throwable t ) {
                throw new RuntimeException( t );
            }
        }
    }
    private final Class<?>[] classes = new Class<?>[]{
        ccs.rocky.nodes.Dot.class,
        ccs.rocky.nodes.ops.Abs.class,
        ccs.rocky.nodes.ops.Log.class,
        ccs.rocky.nodes.ops.Mul.class,
        ccs.rocky.nodes.ops.Neg.class,
        ccs.rocky.nodes.ops.Sig.class,
        ccs.rocky.nodes.ops.Sum.class,
        ccs.rocky.nodes.ops.Div.class,
        ccs.rocky.nodes.ops.Const.class, };
    public Point dragFrom, dragTo;

    public MainForm() throws Throwable {
        super( "Rocky" );
        //default window properties
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setPreferredSize( new Dimension( 1024, 768 ) );
        final Module module = new Module();
        final ModulePanel mp = new ModulePanel( module );
        add( mp );
        //main menu
        JMenuBar menu = new JMenuBar();
        {
            JMenu file = new JMenu( "File" );
            {
                file.add( new AbstractAction( "Exit" ) {

                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        MainForm.this.dispose();
                    }
                } );
            }
            menu.add( file );
            JMenu nodes = new JMenu( "Add" );
            for ( Class<?> c : classes )
                nodes.add( new Act( c, module ) );
            menu.add( nodes );
            JMenu del = new JMenu( "Del" );
            {
                del.add( new AbstractAction( "Obj" ) {

                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        mp.doDelete();
                    }
                } );
            }
            menu.add( del );
            JMenu help = new JMenu( "Help" );
            {
                help.add( new JMenuItem( "About" ) );
            }
            menu.add( help );
        }
        setJMenuBar( menu );
        Writer w = new FileWriter( "store" );
        try {
            Storer st = new Storer( w );
            st.store( "module", module );
        } finally {
            w.close();
        }
    }

    public static void main() throws Throwable {
        MainForm form = new MainForm();
        form.pack();
        form.setVisible( true );
    }
}
