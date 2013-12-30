package binarysearch.finders;

import binarysearch.testers.*;
import binarysearch.*;

/**
 * Алгоритм хэш-поиска с линейной стратегией разрешения коллизий
 * @author igel
 */
public class HashFinder implements TestedFinder {

    protected int last_op_count;
    protected Element[] data = new Element[1];

    /**
     * Добавляет новый элемент данных
     * @param Element - добавляемый элемент
     */
    public void add(Element el) {
        Integer _key = new Integer(el.key);
        int hash = _key.hashCode() % data.length;
        int idx = hash;
        int stp = (hash + data.length - 1) % data.length;
        while ((data[idx] != null) && (idx != stp)) {
            idx = (idx + 1) % data.length;
        }
        if (data[idx] == null) {
            data[idx] = el;
        } else {
            grow(el);
        }
    }

    /**
     * Осуществляет поиск элемента по заданному ключу
     * @param key - ключ поиска
     * @return Element, если таковой был найден, иначе null
     */
    public Element findByKey(int key) {
        last_op_count = 1;
        Integer _key = new Integer(key);
        int hash = _key.hashCode() % data.length;
        int idx = hash;
        int stp = (hash + data.length - 1) % data.length;
        while ((idx != stp) && (data[idx] != null) && (data[idx].key != key)) {
            last_op_count++;
            idx = (idx + 1) % data.length;
        }
        if ((data[idx] == null) || (data[idx].key != key)) {
            return null;
        }
        return data[idx];
    }

    /**
     * Увеличивает размер хэш-пространства, переразмещает элементы в новом хэш-пространстве
     */
    protected void grow(Element el) {
        Element[] old_data = data.clone();
        data = new Element[old_data.length + 1];
        for (Element e : old_data) {
            add(e);
        }
        add(el);
    }

    public void init(Element[] test_data) {
        data = new Element[1];
        for (Element e : test_data)
            add(e);
    }

    public int getTestCount() {
        return (int) Math.pow(data.length, 0.5) * 128 + 1;
    }

    public int getLastOpCount() {
        return last_op_count;
    }
}
