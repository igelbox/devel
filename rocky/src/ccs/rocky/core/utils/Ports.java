package ccs.rocky.core.utils;

import ccs.rocky.core.Port;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author igel
 */
public class Ports<T extends Port> implements Iterable<T> {

//    private final Notificator.PortRemoval not;
    private final Collection<T> ports = new ArrayList<T>();

//    public Ports( PortRemoval not ) {
//        this.not = not;
//    }
    public boolean add( T port ) {
        return ports.add( port );
    }

    public boolean remove( T port ) {
        boolean r = ports.remove( port );
//        if ( r )
//            not.portRemoved( port );
        return r;
    }

    @Override
    public Iterator<T> iterator() {
        return ports.iterator();
    }
}
