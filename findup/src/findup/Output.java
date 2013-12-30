package findup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * @author igel
 */
class Output {

    private final PrintStream out;

    Output( PrintStream out ) {
        this.out = out;
        out.println( "<root>" );
    }

    Output( File out ) throws IOException {
        String encoding = "UTF-8";
        this.out = new PrintStream( new FileOutputStream( out ), true, encoding );
        this.out.append( "<?xml version=\"1.0\" encoding=\"" ).append( encoding ).println( "\"?>" );
        this.out.println( "<root>" );
    }

    void flush() {
        out.println( "</root>" );
    }

    void appendDuplicates( Iterable<File> files ) {
        out.println( "\t<duplicates>" );
        for ( File f : files ) {
            String fname = f.getPath();
            fname = fname.replaceAll( "<", "&lt;" );
            fname = fname.replaceAll( ">", "&gt;" );
            fname = fname.replaceAll( "&", "&amp;" );
            out.append( "\t\t<file>" ).append( fname ).println( "</file>" );
        }
        out.println( "\t</duplicates>" );
    }
}
