package binarysearch.testers;

import binarysearch.*;

/**
 * Тестер алгоритмов.
 * @author igel
 */
public abstract class AbstractTester {

    protected Element[] data;

    public AbstractTester( int data_size ) {
        data = new Element[data_size];
    }

    /**
     * Переинициализирует данные
     */
    protected abstract void reinitData();

    /**
     * Выполняет один тест из множества
     * @return кол-во операций выполненных за тест
     * @throws Exception
     */
    protected abstract int oneTest(Tested tested) throws Exception;

    /**
     * Выполняет множество тестов
     * @return TestResult - максимальное, минимальное и среднее значения кол-ва операций
     * @throws Exception
     */
    public TestResult test(Tested tested) throws Exception {
        float mean = 0;
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        for (int i = 0; i < 64; i++) {
            reinitData();
            tested.init(data);
            for (int j = 0; j < tested.getTestCount(); j++) {
                int last_op_count = oneTest(tested);
                mean += last_op_count;
                max = Math.max(max, last_op_count);
                min = Math.min(min, last_op_count);
            }
        }
        mean /= (float) (tested.getTestCount()*64);
        return new TestResult(data.length, mean, max, min);
    }
}
