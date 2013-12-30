package findup;

/**
 *
 * @author igel
 */
abstract class Mask {

    private static class Wildcard extends Mask {

        final String[] parts;
        final boolean freeTail;

        Wildcard( String[] parts, boolean freeTail ) {
            this.parts = parts;
            this.freeTail = freeTail;
        }

        @Override
        boolean accept( String s ) {
            int idx = 0;
            boolean first = true;
            for ( String part : parts ) {
                idx = s.indexOf( part, idx );
                if ( idx < 0 )
                    return false;
                if ( first ) {
                    if ( idx > 0 )
                        return false;
                    first = false;
                }
                idx += part.length();
            }
            return freeTail || (idx == s.length()) || s.endsWith( parts[parts.length - 1] );
        }
    }
    static final Mask ALL = new Mask() {
        @Override
        public boolean accept( String s ) {
            return true;
        }
    };

    static Mask WILDCARD( String pattern ) {
        String[] parts = pattern.split( "\\*" );
        return parts.length == 0 ? ALL : new Wildcard( parts, pattern.endsWith( "*" ) );
    }

    abstract boolean accept( String s );
}
