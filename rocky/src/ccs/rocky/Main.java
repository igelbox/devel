package ccs.rocky;

import ccs.jack.Client;
import ccs.rocky.ui.MainForm;

/**
 *
 * @author igel
 */
public class Main {

    static String a2s( float[] a ) {
        StringBuilder tmp = new StringBuilder();
        for ( float f : a )
            tmp.append( f ).append( ' ' );
        return tmp.toString();
    }

    public static void main( String[] args ) throws Throwable {
//        Client c = new Client( "rocky" );
//        try {
//            for ( String p : c.getAllPorts() )
//                System.out.println( "P: " + p );
//        } finally {
//            c.close();
//        }
        MainForm.main();
    }
}
