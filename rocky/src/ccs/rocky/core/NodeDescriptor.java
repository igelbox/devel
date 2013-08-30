package ccs.rocky.core;

import ccs.rocky.persistent.Loader;

/**
 *
 * @author igel
 */
public abstract class NodeDescriptor<T extends Node> {

    /** Краткое имя узла */
    public abstract String caption();

    public abstract String tag();

    public abstract T createNode();

    public abstract T loadNode( Loader loader );
}
