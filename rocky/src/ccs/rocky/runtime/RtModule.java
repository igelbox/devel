package ccs.rocky.runtime;

import ccs.rocky.core.Module;

/**
 *
 *  @author igel
 */
public abstract class RtModule {

    protected final Module module;

    public RtModule( Module module ) {
        this.module = module;
    }

    public abstract void process( float time );
}
