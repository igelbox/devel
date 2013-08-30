package ccs.rocky.core;

import ccs.rocky.core.Port.Input;
import ccs.rocky.core.Port.Output;
import ccs.rocky.core.utils.Ports;
import ccs.rocky.persistent.Loader;
import ccs.rocky.persistent.Storer;
import ccs.util.Exceptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;

/**
 *
 * @author igel
 */
public class Module implements Iterable<Node> {

    private final Collection<Node> nodes = new ArrayList<Node>();
    private final Collection<String> usedIds = new HashSet<String>();
    protected final Ports<Port.Input> inputs = new Ports<Port.Input>();
    protected final Ports<Port.Output> outputs = new Ports<Port.Output>();
    private int idGen;

    public Module() {
    }

    public Node node( String id ) {
        for ( Node n : nodes )
            if ( id.equals( n.id ) )
                return n;
        return null;
    }

    public void load( Loader loader ) {
        for ( Loader.Attribute a : loader )
            if ( "node".equals( a.name ) ) {
                Loader l = a.asLoader();
                String type = l.findAttribute( "type" ).asString();
                String id = l.findAttribute( "id" ).asString();
                Node n;
                try {
                    Class<? extends Node> cls = (Class<? extends Node>) Class.forName( type );
                    n = Node.create( cls, id, l );
                } catch ( Throwable e ) {
                    throw Exceptions.wrap( e );
                }
                add( n );
            }
        for ( Loader.Attribute a : loader )
            if ( "node".equals( a.name ) ) {
                Loader n = a.asLoader();
                String dnid = n.findAttribute( "id" ).asString();
                for ( Loader.Attribute na : n )
                    if ( "link".equals( na.name ) ) {
                        Loader l = na.asLoader();
                        String dpid = l.findAttribute( "port" ).asString();
                        String[] did = l.findAttribute( "from" ).asString().split( "\\." );
                        String snid = did[0];
                        String spid = did[1];
                        Node sn = node( snid );
                        if ( sn == null )
                            continue;
                        Port.Output sp = (Port.Output) sn.port( spid );
                        if ( sp == null )
                            continue;
                        Node dn = node( dnid );
                        if ( dn == null )
                            continue;
                        Port.Input dp = (Port.Input) dn.port( dpid );
                        if ( dp == null )
                            continue;
                        dp.connect( sp );
                    }
            }
        Loader.Attribute a = loader.findAttribute( "layout" );
        if ( a != null ) {
            Loader l = a.asLoader();
            
        }
    }

    public String genId( Class<?> cls ) {
        String n;
        do
            n = String.format( "%s_%x", cls.getSimpleName().toLowerCase(), idGen++ );
        while ( usedIds.contains( n ) );
        usedIds.add( n );
        return n;
    }

    public void add( Node node ) {
        nodes.add( node );
        usedIds.add( node.id );
    }

    public void remove( Node node ) {
        nodes.remove( node );
    }

    @Override
    public Iterator<Node> iterator() {
        return nodes.iterator();
    }

    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    public void store( Storer storer ) {
//        storer.putInt( "version", 1 );
        for ( Node n : nodes ) {
            if ( isSystemNode( n ) )
                continue;
            Storer s = storer.put( "node" );
            s.putString( "id", n.id );
            s.putString( "type", n.getClass().getName() );
            n.store( s );
        }
    }

    public static boolean isSystemNode( Node n ) {
//        Node.Descriptor d = n.getClass().getAnnotation( Node.Descriptor.class );
//        return d == null ? false : d.system();
        return false;
    }
}
