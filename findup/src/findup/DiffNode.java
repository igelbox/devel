package findup;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author igel
 */
class DiffNode extends Node {

    static class Block {

        static final int SIZE = 8192;
        final byte[] bytes = new byte[SIZE];

        Block() {
        }

        Block( Block block ) {
            System.arraycopy( block.bytes, 0, this.bytes, 0, SIZE );
        }

        @Override
        public boolean equals( Object obj ) {
            return (obj instanceof Block) && Arrays.equals( ((Block) obj).bytes, bytes );
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode( this.bytes );
        }
    }
    final Collection<File> files = new ArrayList<File>();
    final Map<Block, DiffNode> split = new HashMap<DiffNode.Block, DiffNode>();

    DiffNode( File file ) {
        super( file );
    }
}
