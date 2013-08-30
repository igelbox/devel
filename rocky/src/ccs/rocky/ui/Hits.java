package ccs.rocky.ui;

import java.awt.Shape;

/**
 *
 * @author igel
 */
public abstract class Hits<T> {

    public static final Hits<?> VOID = new Hits<Object>() {
        @Override
        public void associate( Shape area, Object object ) {
        }
    };

    public abstract void associate( Shape area, T object );
}
