package ccs.rocky.nodes.ops;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.core.utils.Ports;
import ccs.rocky.persistent.Loader;
import ccs.rocky.runtime.Generatable;
import ccs.util.Iterabled;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author igel
 */
public abstract class AbstractOp extends Node implements Generatable {

    private final String caption;
    private final Port.Output output = new Port.Output( 0, this );
    private final Iterable<Port.Output> outputs = new Iterabled.Element<Port.Output>( output );
    protected final Ports<Port.Input> inputs = new Ports<Port.Input>();
    private final Generatable.Generator gen = new Generator() {
        @Override
        public void gen_inloop( MethodVisitor mv, Port.Output out ) {
            AbstractOp.this.gen_inloop( mv, out );
        }
    };

    public AbstractOp( int id, Descriptor<?> descriptor, String caption ) {
        super( id, descriptor );
        this.caption = caption;
    }

    public AbstractOp( Descriptor<?> descriptor, Loader loader, String caption ) {
        super( descriptor, loader );
        this.caption = caption;
    }

    @Override
    public final String caption() {
        return caption;
    }

    @Override
    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    public Port.Output output() {
        return output;
    }

    @Override
    public Generator generator() {
        return gen;
    }

    protected abstract void gen_inloop( MethodVisitor mv, Port.Output out );
}
