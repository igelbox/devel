package findup;

import java.io.File;

/**
 *
 * @author igel
 */
class Node {

    final File file;
    long splitPosition = Long.MAX_VALUE;

    Node( File file ) {
        this.file = file;
    }
}
