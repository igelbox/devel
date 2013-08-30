package ccs.rocky.persistent;

/**
 *
 * @author igel
 */
public abstract class Storer {

    public abstract Storer createStorer();

    public abstract void put( String key, Storer value );

    public abstract void putInt( String key, int value );

    public abstract void putString( String key, String value );
}
