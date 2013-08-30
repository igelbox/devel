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

    public static abstract class Single extends AbstractOp {

        private final Port.Input input = new Port.Input( "in", this, null );

        public Single( String id, Loader loader ) {
            super( id, loader );
            inputs.add( input );
        }
    }

    public static abstract class Double extends AbstractOp {

        private final Port.Input inputX = new Port.Input( "inX", this, null );
        private final Port.Input inputY = new Port.Input( "inY", this, null );

        public Double( String id, Loader loader ) {
            super( id, loader );
            inputs.add( inputX );
            inputs.add( inputY );
        }
    }
    private final Port.Output output = new Port.Output( "out", this, null );
    private final Iterable<Port.Output> outputs = new Iterabled.Element<Port.Output>( output );
    protected final Ports<Port.Input> inputs = new Ports<Port.Input>();
    private final Generatable.Generator gen = new Generator() {
        @Override
        public void gen_inloop( MethodVisitor mv, Port.Output out ) {
            AbstractOp.this.gen_inloop( mv, out );
        }
    };

    public AbstractOp( String id, Loader loader ) {
        super( id, loader );
    }

    @Override
    public Iterable<Port.Input> inputs() {
        return inputs;
    }

    @Override
    public Iterable<Port.Output> outputs() {
        return outputs;
    }

    @Override
    public Generator generator() {
        return gen;
    }

    protected abstract void gen_inloop( MethodVisitor mv, Port.Output out );
}
