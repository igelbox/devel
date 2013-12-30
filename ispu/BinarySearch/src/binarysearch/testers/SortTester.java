package binarysearch.testers;

import binarysearch.*;

/**
 * Тестер алгоритмов сортировки
 * @author igel
 */
public class SortTester extends AbstractTester {

    public SortTester(int data_size) {
        super(data_size);
    }

    /**
    * Инициализирует случайную последовательность данных со случайными ключами
    */
    @Override
    protected void reinitData() {
        for (int i = 0; i < data.length; i++) {
            int key = (int) (256 * Math.random());
            data[i] = new Element(key, Integer.toHexString(key));
        }
    }

    @Override
    protected int oneTest(Tested tested) throws Exception {
        TestedSorter ts = (TestedSorter) tested;
        ts.sort();
        return tested.getLastOpCount();
    }
}
