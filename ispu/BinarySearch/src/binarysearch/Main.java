package binarysearch;

import binarysearch.finders.*;
import binarysearch.testers.*;
import binarysearch.sorters.*;
import java.awt.*;
import javax.swing.*;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.xy.*;
import org.jfree.ui.tabbedui.VerticalLayout;

/**
 *
 * @author igel
 */
public class Main extends JFrame {

    public Main(Tested[] testeds, int max_size) throws Exception {
        super("Тест алгоритмов");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel west = new JPanel(new VerticalLayout());
        add(west, BorderLayout.WEST);
        JProgressBar progress = new JProgressBar(0, max_size);
        add(progress, BorderLayout.SOUTH);

        DeviationRenderer rend = new DeviationRenderer(true, false);
        YIntervalSeriesCollection dataset = new YIntervalSeriesCollection();
        NumberAxis axY = new NumberAxis("Кол-во операций");
        XYPlot plot = new XYPlot(dataset, new NumberAxis("Размер последовательности"), axY, rend);
        rend.setBaseFillPaint(Color.GRAY);
        JFreeChart chart = new JFreeChart("Зависимость количества операций от размера последовательности", plot);
        ChartPanel panel = new ChartPanel(chart);
        add(panel);

        for (Tested t : testeds) {
            String name = t.getClass().getSimpleName();
            YIntervalSeries serie = new YIntervalSeries(name);
            dataset.addSeries(serie);
            int serId = dataset.getSeriesCount() - 1;
            west.add(new SerieCheckbox(name, rend, serId));
        }
        pack();
        setVisible(true);
        
        float max = 0;
        for (int i = 1; i <= max_size; i += 4) {
            AbstractTester tester = new SortTester(i);
            for (int t = 0; t < testeds.length; t++) {
                TestResult res = tester.test(testeds[t]);
                dataset.getSeries(t).add(res.size, res.mean, res.max, res.min);
                max = Math.max(max, res.max);
            }
            axY.setRange(0, max + 1);
            progress.setValue(i);
        }
        progress.setValue(max_size);
    }

    public static void main(String[] args) throws Exception {
        new Main(new Tested[]{new MergeSorter()}, 256);
    }
}
