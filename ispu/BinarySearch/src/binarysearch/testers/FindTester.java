package binarysearch.testers;

import binarysearch.*;

/**
 * Тестер алгоритмов поиска
 * @author igel
 */
public class FindTester extends AbstractTester {

    public FindTester(int data_size) {
        super(data_size);
    }

    /**
    * Инициализирует случайную последовательность данных с возрастающими ключами
    */
    @Override
    protected void reinitData() {
        int last_key = 0;
        String last_val = "";
        for (int i = 0; i < data.length; i++) {
            last_key += (int) (25 * Math.random()) + 1;
            last_val += Integer.toHexString(last_key);
            data[i] = new Element(last_key, last_val);
        }
    }

    @Override
    protected int oneTest(Tested tested) throws Exception {
        TestedFinder tf = (TestedFinder) tested;
        int p = (int) (data.length * Math.random());
        Element el = tf.findByKey(data[p].key);
        if (el == null) {
            throw new Exception("Элемент не найден");
        }
        if (!el.val.contentEquals(data[p].val)) {
            throw new Exception("Найден неверный элемент");
        }
        return tested.getLastOpCount();
    }
}
