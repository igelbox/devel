package binarysearch.testers;

import binarysearch.*;

/**
 * Интерфейт тестируемых алгоритмов поиска
 * @author igel
 */
public interface TestedFinder extends Tested {

    /**
     * @param Ключ
     * @return Element - элемент данных соответствующий заданному ключу, или null, если таковой не найден
     */
    Element findByKey(int key);
}
