package ccs.rocky.persistent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author igel
 */
public class Storer {

    protected static class Attribute {

        public final String key;
        public final Object value;

        public Attribute( String key, Object value ) {
            this.key = key;
            this.value = value;
        }
    }
    protected final Collection<Attribute> attributes = new ArrayList<Attribute>( 0 );

    public Storer put( String key ) {
        Storer result = new Storer();
        attributes.add( new Attribute( key, result ) );
        return result;
    }

    public void putInt( String key, int value ) {
        attributes.add( new Attribute( key, value ) );
    }

    public void putFloat( String key, double value ) {
        attributes.add( new Attribute( key, value ) );
    }

    public void putString( String key, String value ) {
        attributes.add( new Attribute( key, value ) );
    }

    private void serialize( String tag, Appendable out, String offset ) throws IOException {
        out.append( offset ).append( '<' ).append( tag );
        boolean hasCompound = false;
        for ( Attribute a : attributes ) {
            String k = a.key;
            Object v = a.value;
            if ( v instanceof Storer )
                hasCompound = true;
            else
                out.append( ' ' ).append( k ).append( "='" ).append( v.toString() ).append( '\'' );
        }
        if ( hasCompound ) {
            out.append( ">\n" );
            for ( Attribute a : attributes ) {
                String k = a.key;
                Object v = a.value;
                if ( v instanceof Storer )
                    ((Storer) v).serialize( k, out, offset + '\t' );
            }
            out.append( offset ).append( "</" ).append( tag ).append( ">\n" );
        } else
            out.append( "/>\n" );
    }

    public void serialize( String tag, Appendable out ) throws IOException {
        serialize( tag, out, "" );
    }
}
