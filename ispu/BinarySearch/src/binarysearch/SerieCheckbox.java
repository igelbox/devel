package binarysearch;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JCheckBox;
import org.jfree.chart.renderer.AbstractRenderer;

/**
 *
 * @author igel
 */
public class SerieCheckbox extends JCheckBox {

    protected AbstractRenderer rend;
    protected int serIdx;

    public SerieCheckbox(String name, AbstractRenderer renderer, int serieIdx) {
        super(name, true);
        rend = renderer;
        serIdx = serieIdx;
        setAction(
                new AbstractAction(name) {

                    public void actionPerformed(ActionEvent arg0) {
                        rend.setSeriesVisible(serIdx, isSelected());
                    }
                });
    }
}
