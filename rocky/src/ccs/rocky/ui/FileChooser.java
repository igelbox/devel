package ccs.rocky.ui;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author igel
 */
public class FileChooser {

    private static final FileFilter FF_ROCKY = new FileNameExtensionFilter( "Rocky files", "rocky" );
    private static File dir = new File( "." );

    public static File open() {
        JFileChooser fc = new JFileChooser( dir );
        fc.addChoosableFileFilter( FF_ROCKY );
        fc.setFileFilter( FF_ROCKY );
        if ( fc.showOpenDialog( null ) == JFileChooser.APPROVE_OPTION ) {
            dir = fc.getCurrentDirectory();
            return fc.getSelectedFile();
        }
        return null;
    }

    public static File save() {
        JFileChooser fc = new JFileChooser( dir );
        fc.addChoosableFileFilter( FF_ROCKY );
        fc.setFileFilter( FF_ROCKY );
        if ( fc.showSaveDialog( null ) == JFileChooser.APPROVE_OPTION ) {
            dir = fc.getCurrentDirectory();
            return fc.getSelectedFile();
        }
        return null;
    }
}
