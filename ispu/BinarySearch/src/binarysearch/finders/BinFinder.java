package binarysearch.finders;

import binarysearch.*;
import binarysearch.testers.*;

/**
 * Алгоритм двоичного поиска в последовательности, отсортированной по возрастанию ключей
 * @author igel
 */
public class BinFinder implements TestedFinder {

    protected Element data[];
    public int last_op_count;

    /**
     * Осуществляет поиск элемента по заданному ключу
     * @param key - ключ поиска
     * @return Element, если таковой был найден, иначе null
     */
    public Element findByKey(int key) {
        last_op_count = 0;
        if (data == null)
            return null;
        int l = 0;
        int r = data.length;
        while (l <= r) {
            int m = (r + l) / 2;
            int k = data[m].key;
            last_op_count++;
            if (key == k)
                return data[m];
            if (key < k) {
                r = m - 1;
            } else {
                l = m + 1;
            }
        }
        return null;
    }

    public void init(Element[] test_data) {
        this.data = test_data;
    }

    public int getTestCount() {
        return (int) Math.pow(data.length, 0.5) * 32 + 1;
    }

    public int getLastOpCount() {
        return last_op_count;
    }
}
