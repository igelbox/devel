package ccs.rocky.ui;

import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.jack.JackModule;
import ccs.rocky.nodes.NodesFactory;
import ccs.rocky.persistent.ZipStorage;
import ccs.rocky.runtime.Compilator;
import ccs.rocky.runtime.RtModule;
import ccs.rocky.runtime.Sink;
import ccs.rocky.runtime.Source;
import ccs.rocky.ui.views.View;
import ccs.util.Exceptions;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import javax.swing.*;

/**
 *
 * @author igel
 */
public class MainForm extends JFrame {

    private static class Act extends AbstractAction {

        private final Node.Descriptor<?> descriptor;
        private final Module m;

        public Act( Node.Descriptor<?> descriptor, Module m ) {
            super( descriptor.caption() );
            this.descriptor = descriptor;
            this.m = m;
        }

        @Override
        public void actionPerformed( ActionEvent e ) {
            Node n = descriptor.createNode( m.genId() );
            m.add( n );
        }
    }
    private final Oscilloscope oscilloscope = new Oscilloscope();
    private final JLabel load = new JLabel();
    private final JackModule.ProcessListener mpl = new JackModule.ProcessListener() {

        private long last;

        @Override
        protected void processed( JackModule module ) {
            long t = System.currentTimeMillis();
            if ( (t - last) < 1000 / 50 )
                return;
            Sink snk = module.oscilloscope.sink();
            oscilloscope.setBuffer( snk.buffer() );
            oscilloscope.repaint();
            load.setText( String.format( "load: %.2f%%", module.load() ) );
            last = t;
        }
    };

    public MainForm() throws Throwable {
        super( "Rocky" );
        //default window properties
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setPreferredSize( new Dimension( 1024, 768 ) );
        ZipStorage st = new ZipStorage( new File( "module.rocky" ) );
        final PropertiesPanel props = new PropertiesPanel();
        final ModulePanel mp = new ModulePanel( st ) {

            @Override
            protected void onSelectionChanged( Collection<View> selection ) {
                View selected = selection.isEmpty() ? null : selection.iterator().next();
                props.setObject( selected == null ? null : selected );
            }
        };
        ((JackModule) mp.module()).listen( mpl );
        //main menu
        JMenuBar menu = new JMenuBar();
        {
            JMenu file = new JMenu( "File" );
            {
                file.add( new AbstractAction( "Save" ) {

                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        try {
                            mp.store();
                        } catch ( Throwable ex ) {
                            throw Exceptions.wrap( ex );
                        }
                    }
                } );
                file.add( new AbstractAction( "Exit" ) {

                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        MainForm.this.dispose();
                    }
                } );
            }
            menu.add( file );
            JMenu nodes = new JMenu( "Add" );
            for ( Node.Descriptor<?> d : NodesFactory.DESCRIPTORS )
                nodes.add( new Act( d, mp.module() ) );
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
        }
        setJMenuBar( menu );
        JToolBar tool = new JToolBar( "Run" );
        {
            final JToggleButton run = new JToggleButton();
            run.setAction( new AbstractAction( "Run" ) {

                @Override
                public void actionPerformed( ActionEvent e ) {
                    JackModule m = (JackModule) mp.module();
                    m.activate( run.isSelected() );
                    run.setSelected( m.active() );
                    run.setText( run.isSelected() ? "Stop" : "Run" );
                    load.setVisible( m.active() );
                }
            } );
//            tool.add( run );
            tool.add( load );
        }
        add( tool, BorderLayout.NORTH );
        oscilloscope.setPreferredSize( new Dimension( 1000, 256 ) );
        JSplitPane sp = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, oscilloscope, mp );
        sp.setPreferredSize( new Dimension( 800, 256 ) );
        add( new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, sp, props ) );
    }

    public static void main() throws Throwable {
        MainForm form = new MainForm();
        form.pack();
        form.setVisible( true );
    }
}
