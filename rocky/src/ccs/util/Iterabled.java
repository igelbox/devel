package ccs.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 *
 * @author igel
 */
public class Iterabled {

    public static <T> Iterable<T> multi( final Iterable<? extends T>... it ) {
        return new Iterable<T>() {

            @Override
            public Iterator<T> iterator() {
                return new Iterator<T>() {

                    private int next = 1;
                    private Iterator<? extends T> i = it[0].iterator();

                    @Override
                    public boolean hasNext() {
                        while ( !i.hasNext() && (next < it.length) )
                            i = it[next++].iterator();
                        return i.hasNext();
                    }

                    @Override
                    public T next() {
                        return i.next();
                    }

                    @Override
                    public void remove() {
                        i.remove();
                    }
                };
            }
        };
    }
    private static Iterator<?> EMPTY_ITERATOR = new Iterator<Object>() {

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Object next() {
            throw new NoSuchElementException();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    };

    @SuppressWarnings( "unchecked" )
    public static <T> Iterator<T> emptyIterator() {
        return (Iterator<T>) EMPTY_ITERATOR;
    }
    private static Iterable<?> EMPTY_ITERABLE = new Iterable<Object>() {

        @Override
        public Iterator<Object> iterator() {
            return emptyIterator();
        }
    };

    @SuppressWarnings( "unchecked" )
    public static <T> Iterable<T> emptyIterable() {
        return (Iterable<T>) EMPTY_ITERABLE;
    }

    public static class Array<T> implements Iterable<T> {

        public static class Iterator<T> implements java.util.Iterator<T> {

            private final T[] array;
            private int index;

            public Iterator( T[] array ) {
                this.array = array;
            }

            @Override
            public boolean hasNext() {
                return index < array.length;
            }

            @Override
            public T next() {
                if ( !hasNext() )
                    throw new NoSuchElementException();
                return array[index++];
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
        private final T[] array;

        public Array( T[] array ) {
            this.array = array;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>( array );
        }
    }

    public static class Element<T> implements Iterable<T> {

        public static class Iterator<T> implements java.util.Iterator<T> {

            private final T element;
            private boolean nexted;

            public Iterator( T element ) {
                this.element = element;
            }

            @Override
            public boolean hasNext() {
                return !nexted;
            }

            @Override
            public T next() {
                if ( nexted )
                    throw new NoSuchElementException();
                nexted = true;
                return element;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
        private final T element;

        public Element( T element ) {
            this.element = element;
        }

        @Override
        public Iterator<T> iterator() {
            return new Iterator<T>( element );
        }
    }
}