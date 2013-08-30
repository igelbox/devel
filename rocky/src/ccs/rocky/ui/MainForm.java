package ccs.rocky.ui;

//import java.awt.BorderLayout;
import ccs.rocky.Module;
import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

/**
 *
 * @author igel
 */
public class MainForm extends JFrame {

    public Point dragFrom, dragTo;

    public MainForm() {
        super( "Rocky" );
        //default window properties
        setDefaultCloseOperation( EXIT_ON_CLOSE );
        setPreferredSize( new Dimension( 1024, 768 ) );

        //main menu
        JMenuBar menu = new JMenuBar();
        {
            JMenu file = new JMenu( "File" );
            {
                JMenuItem exit = new JMenuItem( new AbstractAction( "Exit" ) {

                    @Override
                    public void actionPerformed( ActionEvent e ) {
                        MainForm.this.dispose();
                    }
                } );
                file.add( exit );
            }
            menu.add( file );
            JMenu help = new JMenu( "Help" );
            {
                JMenuItem about = new JMenuItem( "About" );
                help.add( about );
            }
            menu.add( help );
        }
        setJMenuBar( menu );

        add( new ModuleView( new Module() {

            @Override
            public String caption() {
                return "test module";
            }
        } ) );
    }

    public static void main() {
        MainForm form = new MainForm();
        form.pack();
        form.setVisible( true );
    }
}
