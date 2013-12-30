package findup;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * @author igel
 */
class DirectoryScanner implements Callable<Iterator<File>> {

    interface FileFilter {

        File accept( File directory, String fileName );
    }

    @Override
    public Iterator<File> call() throws Exception {
        String[] fileNames = directory.list();
        if ( fileNames == null )
            throw new IOException( "Can`t list directory '" + directory + "'" );
        final Collection<File> files = new ArrayList<File>( fileNames.length );
        final Collection<Future<Iterator<File>>> futures = new ArrayList<Future<Iterator<File>>>();
        for ( String fname : fileNames ) {
            File f = filter.accept( directory, fname );
            if ( f == null )
                continue;
            if ( f.isDirectory() )
                futures.add( DirectoryScanner.this.executor.submit( new DirectoryScanner( f, filter, executor ) ) );
            else
                files.add( f );
        }
        return new Iterator<File>() {
            Iterator<File> iterator = files.iterator();
            Iterator<Future<Iterator<File>>> fiterator = futures.iterator();

            @Override
            public boolean hasNext() {
                while ( !iterator.hasNext() ) {
                    if ( !fiterator.hasNext() )
                        return false;
                    try {
                        iterator = fiterator.next().get();
                    } catch ( ExecutionException e ) {
                        throw new RuntimeException( e );//wrap
                    } catch ( InterruptedException e ) {
                        throw new RuntimeException( e );//wrap
                    }
                }
                return true;
            }

            @Override
            public File next() {
                return iterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
    private final ExecutorService executor;
    private final File directory;
    private final FileFilter filter;

    DirectoryScanner( File directory, FileFilter filter, ExecutorService executor ) {
        this.directory = directory;
        this.filter = filter;
        this.executor = executor;
    }
}
