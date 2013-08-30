package ccs.rocky.persistent;

import java.io.*;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import org.xml.sax.InputSource;

/**
 *
 * @author igel
 */
public class ZipStorage extends Storage {

    private final Charset ENCODING = Charset.forName( "UTF-8" );
    private final File file;
    private byte[] module, view;

    public ZipStorage( File file ) {
        this.file = file;
    }

    @Override
    public Loader nodes() throws IOException {
        if ( module == null )
            module = read( "nodes.xml" );
        return module == null ? new Loader() : Loader.deserialize( new InputSource( new ByteArrayInputStream( module ) ) );
    }

    @Override
    public void nodes( Storer s ) throws IOException {
        StringBuilder tmp = new StringBuilder( "<?xml version='1.0' encoding='" + ENCODING.name() + "'?>\n" );
        s.serialize( "module", tmp );
        module = tmp.toString().getBytes( ENCODING );
    }

    @Override
    public Loader view() throws IOException {
        if ( view == null )
            view = read( "view.xml" );
        return view == null ? new Loader() : Loader.deserialize( new InputSource( new ByteArrayInputStream( view ) ) );
    }

    @Override
    public void view( Storer s ) throws IOException {
        StringBuilder tmp = new StringBuilder( "<?xml version='1.0' encoding='" + ENCODING.name() + "'?>\n" );
        s.serialize( "view", tmp );
        view = tmp.toString().getBytes( ENCODING );
    }

    @Override
    public void flush() throws IOException {
        ZipOutputStream w = new ZipOutputStream( new FileOutputStream( file ) );
        try {
            if ( module != null ) {
                ZipEntry e = new ZipEntry( "nodes.xml" );
                w.putNextEntry( e );
                w.write( module );
            }
            if ( view != null ) {
                ZipEntry e = new ZipEntry( "view.xml" );
                w.putNextEntry( e );
                w.write( view );
            }
            w.finish();
        } finally {
            w.close();
        }
    }

    private byte[] read( String name ) throws IOException {
        if ( !file.exists() )
            return null;
        ZipFile f = new ZipFile( file );
        try {
            ZipEntry e = f.getEntry( name );
            InputStream in = f.getInputStream( e );
            ByteArrayOutputStream o = new ByteArrayOutputStream( (int) e.getSize() );
            byte[] buff = new byte[4096];
            int l;
            while ( (l = in.read( buff )) >= 0 )
                o.write( buff, 0, l );
            return o.toByteArray();
        } finally {
            f.close();
        }
    }
}
