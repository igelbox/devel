package binarysearch.sorters;

import binarysearch.*;
import binarysearch.testers.*;

/**
 * Алгоритм сортировки простым слиянием
 * @author igel
 */
public class MergeSorter implements TestedSorter {
    protected int last_op_count;
    public Element[] data;

    public void init(Element[] test_data) {
        data = test_data;
    }

    public int getTestCount() {
        return (int) Math.pow(data.length, 0.5) * 4 + 1;
    }

    public int getLastOpCount() {
        return last_op_count;
    }

    /**
     * Сортирует заданную последовательность в порядке возрастания ключей
     */
    public void sort() {
        last_op_count = 0;
        sort_r( 0, data.length-1 );
    }

    /**
     * Сортирует подпоследовтельность заданную координатами начала и конца последовательность в порядке возрастания ключей
     * @param l - координата начала
     * @param r - координата конца
     */
    protected void sort_r( int l, int r ) {
        last_op_count++;
        if ( r == l )
            return;
        int mid = (l + r) / 2;
        sort_r( l, mid );
        sort_r( mid+1, r );
        Element[] new_data = new Element[r-l+1];
        int p1 = l, p2 = mid+1, pN = 0;
        while ( (p1 <= mid) && (p2 <= r) ) {
            if ( data[p1].key < data[p2].key )
                new_data[pN++] = data[p1++];
            else
                new_data[pN++] = data[p2++];
            last_op_count+=2;
        }
        while ( p1 <= mid ) {
            new_data[pN++] = data[p1++];
            last_op_count++;
        }
        while ( p2 <= r ) {
            new_data[pN++] = data[p2++];
            last_op_count++;
        }
        for ( Element e: new_data ) {
            data[l++] = e;
            last_op_count++;
        }
    }

}
