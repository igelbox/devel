package ccs.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author igel
 */
public class Cloud<T> implements Iterable<T> {

    private final Collection<Reference<T>> items = new ArrayList<Reference<T>>();

    public boolean add( T elem ) {
        for ( T e : this )
            if ( elem.equals( e ) )
                return false;
        return items.add( new WeakReference<T>( elem ) );
    }

    public boolean remove( T elem ) {
        for ( Iterator<T> i = this.iterator(); i.hasNext(); )
            if ( i.next() == elem ) {
                i.remove();
                return true;
            }
        return false;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {

            T next;
            Iterator<Reference<T>> i = items.iterator();

            @Override
            public boolean hasNext() {
                while ( (next == null) && i.hasNext() ) {
                    next = i.next().get();
                    if ( next == null )
                        i.remove();
                }
                return next != null;
            }

            @Override
            public T next() {
                T r = next;
                next = null;
                return r;
            }

            @Override
            public void remove() {
                i.remove();
            }
        };
    }
}
