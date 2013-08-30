package ccs.rocky.ui.util;

import java.awt.Component;
import javax.swing.JScrollPane;

/**
 *
 * @author igel
 */
public class ScrollPane extends JScrollPane {

    public ScrollPane( Component view ) {
        super( view );
        setBorder( null );
    }
}
