package ccs.rocky.views;

import ccs.rocky.core.View;
import ccs.rocky.ui.Snap;
import java.awt.Point;

/**
 *
 * @author igel
 */
public interface Draggable {

    void drag( Point from, Point to, Snap snap );

    void drop( boolean ok, View into );
}
