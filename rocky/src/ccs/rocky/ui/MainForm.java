package ccs.rocky.ui;

import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.View;
import ccs.rocky.jack.JackModule;
import ccs.rocky.nodes.NodesFactory;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.rocky.runtime.Sink;
import ccs.util.Exceptions;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.Collection;
import javax.swing.*;
import org.xml.sax.InputSource;

/**
 *
 * @author igel
 */
public class MainForm extends JFrame {

    private class Act extends AbstractAction {

        private final Class<? extends Node> clazz;
        private final Module m;

        public Act( Class<? extends Node> clazz, Module m ) {
            super( caption( clazz ) );
            this.clazz = clazz;
            this.m = m;
        }

        @Override
        public void actionPerformed( ActionEvent e ) {
            Node n;
            try {
                n = Node.create( clazz, m.genId( clazz ), Loader.VOID );
            } catch ( Throwable t ) {
                throw Exceptions.wrap( t );
            }
            mp.addNode( n );
        }
    }

    private static String caption( Class<?> cls ) {
//        Node.Descriptor d = cls.getAnnotation( Node.Descriptor.class );
//        return d == null ? cls.getSimpleName().toLowerCase() : d.caption();
        return cls.getSimpleName().toLowerCase();
    }
//    private final Oscilloscope oscilloscope = new Oscilloscope();
    private final Oscilloscope oscilloscope = new Oscilloscope();
    private final ControlPanel cp = new ControlPanel();
    private final JackModule.ProcessListener mpl = new JackModule.ProcessListener() {
        private long last;

        @Override
        protected void processed( JackModule module ) {
//            Sink snk = module.oscilloscope.sink();
//            oscilloscope.setBuffer( snk.buffer() );
//            long t = System.currentTimeMillis();
//            if ( (t - last) < 1000 / 50 )
//                return;
//            oscilloscope.repaint();
//            cp.load( module.load() );
//            cp.repaint();
//            last = t;
        }
    };
    final ModulePanel mp;
//    ZipStorage st = new ZipStorage( new File( "module.rocky" ) );

    public MainForm() throws Throwable {
        super( "Rocky" );
        //default window properties
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setPreferredSize( new Dimension( 1024, 768 ) );
        final PropertiesPanel props = new PropertiesPanel();
        Loader loader = Loader.deserialize( new InputSource( new FileInputStream( "module.rml" ) ) );
        mp = new ModulePanel( loader ) {
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
                file.add( new AbstractAction( "Open..." ) {
                    @Override
                    public void actionPerformed( ActionEvent e ) {
//                        File f = FileChooser.open();
//                        if ( f != null )
//                            st = new ZipStorage( f );
                    }
                } ).setAccelerator( KeyStroke.getKeyStroke( 'O', InputEvent.CTRL_DOWN_MASK ) );
                file.add( new AbstractAction( "Save as..." ) {
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        File f = FileChooser.save();
                        if ( f != null )
                            if ( f.exists() )
                                if ( JOptionPane.showConfirmDialog( null, "Заменить файл", "Файл существует", JOptionPane.YES_NO_OPTION ) != JOptionPane.YES_OPTION )
                                    return; //                            st = new ZipStorage( f );
                        //                            try {
                        ////                                mp.store( st );
                        //                            } catch ( Throwable ex ) {
                        //                                throw Exceptions.wrap( ex );
                        //                            }
                    }
                } ).setAccelerator( KeyStroke.getKeyStroke( 'S', InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ) );
                file.add( new AbstractAction( "Save" ) {
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        final Charset ENCODING = Charset.forName( "UTF-8" );
                        try {
                            Storer storer = new Storer();
                            mp.module.store( storer );
                            StringBuilder tmp = new StringBuilder( "<?xml version='1.0' encoding='" + ENCODING.name() + "'?>\n" );
                            storer.serialize( "module", tmp );
                            FileOutputStream out = new FileOutputStream( "module.rml" );
                            try {
                                out.write( tmp.toString().getBytes( ENCODING ) );
                            } finally {
                                out.close();
                            }
//                            mp.store( st );
                        } catch ( Throwable ex ) {
                            throw Exceptions.wrap( ex );
                        }
                    }
                } ).setAccelerator( KeyStroke.getKeyStroke( 'S', InputEvent.CTRL_DOWN_MASK ) );
                file.add( new AbstractAction( "Exit" ) {
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        MainForm.this.dispose();
                    }
                } );
            }
            menu.add( file );
            JMenu nodes = new JMenu( "Add" );
            for ( Class<? extends Node> c : NodesFactory.CLASSES )
                nodes.add( new Act( c, mp.module() ) );
            menu.add( nodes );
            JMenu del = new JMenu( "Del" );
            {
                del.add( new AbstractAction( "Obj" ) {
                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        mp.deleteSelected();
                    }
                } );
            }
            menu.add( del );
            menu.add( new JButton( new AbstractAction( "=" ) {
                @Override
                public void actionPerformed( ActionEvent e ) {
                    mp.transform.setToIdentity();
                    mp.repaint();
                }
            } ) );
            menu.add( cp );
        }
        setJMenuBar( menu );
        oscilloscope.setPreferredSize( new Dimension( mp.getPreferredSize().width, 256 ) );
        JSplitPane sp = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, oscilloscope, mp );
        sp.setPreferredSize( new Dimension( 800, 256 ) );
        add( new JSplitPane( JSplitPane.HORIZONTAL_SPLIT, true, sp, props ) );
        add( mp );
    }

    public static void main() throws Throwable {
        MainForm form = new MainForm();
        form.pack();
        form.setVisible( true );
    }
}
