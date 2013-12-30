package binarysearch.testers;

import binarysearch.*;

/**
 * Абстрактный интерфейс тестируемых алгоритмов
 * @author igel
 */
public interface Tested {

    /**
     * Инициализация данных
     */
    void init(Element[] test_data);

    /**
     * @return Необходимое кол-во тестов для усреднения результатов
     */
    int getTestCount();

    /**
     * @return Кол-во операций выполненных алгоритмом при последнем запросе
     */
    int getLastOpCount();
}
