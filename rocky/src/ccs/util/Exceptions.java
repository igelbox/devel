package ccs.util;

/**
 *
 * @author igel
 */
public class Exceptions {

    private static class Wrapper extends RuntimeException {

        public Wrapper( Throwable cause ) {
            super( cause );
        }
    }

    public static RuntimeException wrap( Throwable t ) {
        if ( t instanceof RuntimeException )
            return (RuntimeException) t;
        return new Wrapper( t );
    }

    public static Throwable unwrap( Throwable t ) {
        if ( t instanceof Wrapper )
            return t.getCause();
        return t;
    }
}
