package findup;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 *
 * @author igel
 */
public class Main {

    public static void main( String[] args ) throws Throwable {
//        args = "/home/igel/tmp --minsz 1".split( " " );
        try {
            final Arguments arguments = Arguments.parse( args );
            Output output = arguments.outFileName != null
                    ? new Output( arguments.outFileName )
                    : new Output( System.out );
            int cpuCores = Runtime.getRuntime().availableProcessors();
            ExecutorService executor = Executors.newFixedThreadPool( cpuCores );
            try {
                Map<Long, Collection<File>> groupFilesBySize = new HashMap<Long, Collection<File>>();
                {
                    DirectoryScanner.FileFilter fileFilter = new DirectoryScanner.FileFilter() {
                        Mask mask = arguments.fileMask == null ? Mask.ALL : Mask.WILDCARD( arguments.fileMask );

                        @Override
                        public File accept( File dir, String name ) {
                            File file = new File( dir, name );
                            return file.canRead() && (file.isDirectory() || ((file.length() >= arguments.minFileSize) && mask.accept( name )))
                                    ? file
                                    : null;

                        }
                    };
                    DirectoryScanner scanner = new DirectoryScanner( arguments.root, fileFilter, executor );
                    Iterator<File> files = scanner.call();
                    while ( files.hasNext() ) {
                        File f = files.next();
                        Long key = f.length();
                        Collection<File> group = groupFilesBySize.get( key );
                        if ( group == null )
                            groupFilesBySize.put( key, group = new ArrayList<File>() );
                        group.add( f );
                    }
                }
                final int MAX_OPEN_FILES = 256;
                Semaphore limitOpenFiles = new Semaphore( MAX_OPEN_FILES );
                for ( Map.Entry<Long, Collection<File>> e : groupFilesBySize.entrySet() ) {
                    Collection<File> group = e.getValue();
                    if ( group.size() < 2 )
                        continue;
                    if ( e.getKey() == 0 ) {
                        output.appendDuplicates( group );
                        continue;
                    }
                    Queue<Node> nodes = new ArrayDeque<Node>();
                    for ( File f : group )
                        nodes.offer( new Node( f ) );
                    Collection<Node> toMerge = new ArrayList<Node>( MAX_OPEN_FILES );
                    Node node;
                    while ( (node = nodes.poll()) != null ) {
                        toMerge.add( node );
                        if ( toMerge.size() == MAX_OPEN_FILES ) {
                            NodesMerger m = new NodesMerger( toMerge, 0, executor, limitOpenFiles );
                            nodes.offer( m.call().resolve() );
                            toMerge.clear();
                        }
                    }
                    NodesMerger m = new NodesMerger( toMerge, 0, executor, limitOpenFiles );
                    scanNodeForDuplicates( m.call().resolve(), output );
                }
            } finally {
                executor.shutdown();
            }
            output.flush();
        } catch ( AppError e ) {
            System.err.println( "error: " + e.getMessage() );
            printHelp( System.err );
        }
    }

    static void scanNodeForDuplicates( DiffNode node, Output output ) {
        if ( !node.split.isEmpty() )
            for ( DiffNode n : node.split.values() )
                scanNodeForDuplicates( n, output );
        else if ( node.files.size() > 1 )
            output.appendDuplicates( node.files );
    }

    static void printHelp( PrintStream out ) {
        out.println( "usage: findup <root directory> [--out <output file name>] [--mask <file name mask>] [--minsz <minimal file size in bytes>]" );
    }
}