package ccs.rocky.persistent;

import java.io.Flushable;
import java.io.IOException;

/**
 *
 * @author igel
 */
public abstract class Storage implements Flushable {

    public abstract Loader nodes() throws IOException;

    public abstract Loader view() throws IOException;

    public abstract void nodes( Storer s ) throws IOException;

    public abstract void view( Storer s ) throws IOException;

    @Override
    public abstract void flush() throws IOException;
}
