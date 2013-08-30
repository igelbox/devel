package ccs.rocky.persistent;

import ccs.util.Exceptions;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Stack;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author igel
 */
public class Loader implements Iterable<Loader.Attribute> {

    public static class Attribute {

        public final String name;
        public final Object value;

        Attribute( String name, Object value ) {
            this.name = name;
            this.value = value;
        }

        public Loader asLoader() {
            return (Loader) value;
        }

        public int asInt() {
            return Integer.valueOf( value.toString() );
        }

        public float asFloat() {
            return Float.valueOf( value.toString() );
        }

        public String asString() {
            return value.toString();
        }

        @Override
        public String toString() {
            return name + "=" + value;
        }
    }
    public static final Loader VOID = new Loader();
    protected final Collection<Attribute> attributes = new ArrayList<Attribute>( 0 );

    public Loader() {
    }

    private Loader( Attributes attrs ) {
        for ( int i = 0; i < attrs.getLength(); i++ ) {
            String k = attrs.getQName( i );
            String v = attrs.getValue( i );
            attributes.add( new Attribute( k, v ) );
        }
    }

    public Attribute findAttribute( String name ) {
        for ( Attribute a : attributes )
            if ( name.equals( a.name ) )
                return a;
        return null;
    }

    @Override
    public Iterator<Attribute> iterator() {
        return attributes.iterator();
    }

    public static Loader deserialize( InputSource in ) throws IOException {
        SAXParserFactory f = SAXParserFactory.newInstance();
        try {
            SAXParser p = f.newSAXParser();
            final Stack<Loader> st = new Stack<Loader>();
            final Loader result = new Loader();
            p.parse( in, new DefaultHandler() {
                @Override
                public void startElement( String uri, String localName, String qName, Attributes attributes ) throws SAXException {
                    Loader l;
                    if ( st.isEmpty() )
                        l = result;
                    else {
                        l = new Loader( attributes );
                        Loader p = st.peek();
                        p.attributes.add( new Attribute( qName, l ) );
                    }
                    st.push( l );
                }

                @Override
                public void endElement( String uri, String localName, String qName ) throws SAXException {
                    st.pop();
                }
            } );
            return result;
        } catch ( ParserConfigurationException e ) {
            throw Exceptions.wrap( e );
        } catch ( SAXException e ) {
            throw Exceptions.wrap( e );
        }
    }
}
