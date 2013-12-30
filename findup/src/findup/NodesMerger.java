package findup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Semaphore;

/**
 *
 * @author igel
 */
class NodesMerger implements Callable<LazyDiffNode> {

    private static class NodeStream {

        static final NodeStream[] EMPTY_ARRAY = new NodeStream[0];
        Node node;
        final InputStream input;

        NodeStream( Node node, long offset ) throws IOException {
            this.node = node;
            this.input = new FileInputStream( node.file );
            this.input.skip( offset );
        }

        void close() {
            try {
                input.close();
            } catch ( IOException e ) {
                e.printStackTrace( System.err );
            }
        }

        int read( byte[] buffer ) throws IOException {
            int n = 0, len = buffer.length;
            while ( n < len ) {
                int count = input.read( buffer, n, len - n );
                if ( count < 0 )
                    break;
                n += count;
            }
            return n;
        }
    }
    private final ExecutorService executor;
    private final Semaphore limitOpenFiles;
    private final Collection<Node> nodes;
    private final long offset;

    NodesMerger( Collection<Node> nodes, long offset, ExecutorService executor, Semaphore limitOpenFiles ) {
        this.nodes = nodes;
        this.offset = offset;
        this.executor = executor;
        this.limitOpenFiles = limitOpenFiles;
    }

    @Override
    public LazyDiffNode call() throws Exception {
        LazyDiffNode result;
        Map<DiffNode.Block, Collection<Node>> groupByBlock = null;
        long position = offset;
        NodeStream[] streams = new NodeStream[nodes.size()];
        limitOpenFiles.acquire( nodes.size() );
        try {
            int idx = 0;
            for ( Node n : nodes )
                streams[idx++] = new NodeStream( n, offset );
            DiffNode.Block fblock = new DiffNode.Block(), nblock = new DiffNode.Block();
            NodeStream fs = streams[0];
            result = new LazyDiffNode( fs.node.file );
            long minSplitPosition = Long.MAX_VALUE;
            for ( NodeStream s : streams ) {
                result.addFile( s.node.file );
                minSplitPosition = Math.min( minSplitPosition, s.node.splitPosition );
            }
            boolean run = true;
            while ( run ) {
                int n = fs.read( fblock.bytes );
                if ( n <= 0 )
                    return result;
                for ( int i = 1; i < streams.length; i++ ) {
                    NodeStream ns = streams[i];
                    if ( ns.read( nblock.bytes ) <= 0 )
                        throw new RuntimeException( "Impossible case occured" );
                    if ( (position == minSplitPosition) || !nblock.equals( fblock ) ) {
                        result.setSplitPosition( position );
                        groupByBlock = new HashMap<DiffNode.Block, Collection<Node>>();
                        for ( int j = 0; j < i; j++ )
                            putNodeToGroups( groupByBlock, streams[j].node, fblock, position );
                        putNodeToGroups( groupByBlock, ns.node, nblock, position );
                        for ( int j = i + 1; j < streams.length; j++ ) {
                            ns = streams[j];
                            if ( ns.read( nblock.bytes ) <= 0 )
                                throw new RuntimeException( "Impossible case occured" );
                            putNodeToGroups( groupByBlock, ns.node, nblock, position );
                        }
                        run = false;
                        break;
                    }
                }
                position += n;
            }
        } finally {
            for ( NodeStream ns : streams )
                if ( ns != null )
                    ns.close();
            limitOpenFiles.release( nodes.size() );
        }
        for ( Map.Entry<DiffNode.Block, Collection<Node>> e : groupByBlock.entrySet() )
            result.split.put( e.getKey(), executor.submit( new NodesMerger( e.getValue(), position, executor, limitOpenFiles ) ) );
        return result;
    }

    private static void putNodeToGroups( Map<DiffNode.Block, Collection<Node>> groups, Node node, DiffNode.Block block, long position ) {
        if ( node.splitPosition == position )
            for ( Map.Entry<DiffNode.Block, DiffNode> e : ((DiffNode) node).split.entrySet() ) {
                DiffNode.Block key = e.getKey();
                Collection<Node> group = groups.get( key );
                if ( group == null ) {
                    group = new ArrayList<Node>();
                    groups.put( key, group );
                }
                group.add( node );
            }
        else {
            Collection<Node> group = groups.get( block );
            if ( group == null ) {
                group = new ArrayList<Node>();
                groups.put( new DiffNode.Block( block ), group );
            }
            group.add( node );
        }
    }
}
