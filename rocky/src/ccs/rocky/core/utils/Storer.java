package ccs.rocky.core.utils;

import ccs.rocky.core.Storable;
import java.io.IOException;
import java.io.Writer;
import java.util.Stack;

/**
 *
 * @author igel
 */
public class Storer {

    private final Writer w;
    private int offset;
    private Stack<String> tags = new Stack<String>();

    public Storer( Writer w ) {
        this.w = w;
    }

    private void write( String s ) {
        try {
            w.write( s );
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    private String tabs() {
        char[] r = new char[offset];
        for ( int i = 0; i < offset; i++ )
            r[i] = '\t';
        return new String( r );
    }

    private void tago( String name ) {
        tags.push( name );
        write( tabs() + "<" + name + ">\n" );
        offset++;
    }

    private void tagc() {
        String t = tags.pop();
        offset--;
        write( tabs() + "</" + t + ">\n" );
    }

    public void put( String attribute, String value ) {
        tago( attribute );
        tagc();
    }

    public void store( String clazz, Storable storable ) {
        tago( clazz );
        storable.store( this );
        tagc();
    }
}
