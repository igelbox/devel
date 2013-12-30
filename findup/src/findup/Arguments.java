package findup;

import java.io.File;

/**
 *
 * @author igel
 */
class Arguments {

    File root;
    long minFileSize;
    String fileMask;
    File outFileName;

    static Arguments parse( String[] args ) {
        if ( args.length < 1 )
            throw new AppError( "root directory is not specified" );
        Arguments result = new Arguments();
        result.root = new File( args[0] );
        if ( !result.root.exists() )
            throw new AppError( "root directory '" + result.root + "' is not exists" );
        if ( !result.root.isDirectory() )
            throw new AppError( "root directory '" + result.root + "' is not a directory" );
        for ( int i = 1; i < args.length; i++ ) {
            String key = args[i];
            i++;
            if ( key.equals( "--out" ) ) {
                if ( i >= args.length )
                    throw new AppError( "file path required for argument '--out'" );
                result.outFileName = new File( args[i] );
            } else if ( key.equals( "--mask" ) ) {
                if ( i >= args.length )
                    throw new AppError( "value required for argument '--mask'" );
                result.fileMask = args[i];
            } else if ( key.equals( "--minsz" ) ) {
                if ( i >= args.length )
                    throw new AppError( "integer value required for argument '--minsz'" );
                try {
                    result.minFileSize = Long.parseLong( args[i] );
                } catch ( NumberFormatException e ) {
                    throw new AppError( "integer value required for argument '--minsz'" );
                }
            }
        }
        return result;
    }
}
