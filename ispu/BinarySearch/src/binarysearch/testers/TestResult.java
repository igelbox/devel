package binarysearch.testers;

/**
 * Результат выполнения теста. Содержит максимальное, минимальное и среднее значения кол-ва операций
 * @author igel
 */
public class TestResult {
    public int size;
    public float mean, max, min;

    public TestResult( int sz, float mean, float max, float min ) {
        size = sz;
        this.mean = mean;
        this.max = max;
        this.min = min;
    }
}
