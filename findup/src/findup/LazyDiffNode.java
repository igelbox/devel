package findup;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author igel
 */
class LazyDiffNode {

    private final DiffNode node;
    final Map<DiffNode.Block, Future<LazyDiffNode>> split = new HashMap<DiffNode.Block, Future<LazyDiffNode>>();

    LazyDiffNode( File file ) {
        node = new DiffNode( file );
    }

    void setSplitPosition( long pos ) {
        node.splitPosition = pos;
    }

    void addFile( File f ) {
        node.files.add( f );
    }

    DiffNode resolve() throws InterruptedException, ExecutionException {
        for ( Map.Entry<DiffNode.Block, Future<LazyDiffNode>> e : split.entrySet() )
            node.split.put( e.getKey(), e.getValue().get().resolve() );
        split.clear();
        return node;
    }
}
