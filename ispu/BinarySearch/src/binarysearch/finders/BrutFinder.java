package binarysearch.finders;

import binarysearch.*;
import binarysearch.testers.*;

/**
 * Алгоритм двоичного поиска в последовательности, отсортированной по возрастанию ключей
 * @author igel
 */
public class BrutFinder implements TestedFinder {

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
        for ( int i = 0; i < data.length; i++ ) {
            last_op_count++;
            if ( data[i].key == key )
                return data[i];
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
