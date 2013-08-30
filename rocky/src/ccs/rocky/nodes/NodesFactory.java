package ccs.rocky.nodes;

import ccs.rocky.core.Node;

/**
 *
 * @author igel
 */
public class NodesFactory {

    public static final Node.Descriptor<?>[] DESCRIPTORS = new Node.Descriptor<?>[]{
        ccs.rocky.nodes.Const.DESCRIPTOR,
        ccs.rocky.nodes.Var.DESCRIPTOR,
        ccs.rocky.nodes.Dot.DESCRIPTOR,
        ccs.rocky.nodes.Buff.DESCRIPTOR,
        ccs.rocky.nodes.ops.Abs.DESCRIPTOR,
        ccs.rocky.nodes.ops.Inv.DESCRIPTOR,
        ccs.rocky.nodes.ops.Div.DESCRIPTOR,
        ccs.rocky.nodes.ops.Mod.DESCRIPTOR,
        ccs.rocky.nodes.ops.Log.DESCRIPTOR,
        ccs.rocky.nodes.ops.Exp.DESCRIPTOR,
        ccs.rocky.nodes.ops.Mul.DESCRIPTOR,
        ccs.rocky.nodes.ops.Pow.DESCRIPTOR,
        ccs.rocky.nodes.ops.Sig.DESCRIPTOR,
        ccs.rocky.nodes.ops.Sum.DESCRIPTOR,
        ccs.rocky.nodes.ops.Sub.DESCRIPTOR,
        ccs.rocky.nodes.ops.Sin.DESCRIPTOR,
        ccs.rocky.core.Module.In.DESCRIPTOR,
        ccs.rocky.core.Module.Out.DESCRIPTOR
    };

    public static Node.Descriptor<?> findDescriptor( String tag ) {
        for ( Node.Descriptor<?> d : DESCRIPTORS )
            if ( tag.equals( d.tag() ) )
                return d;
        throw new IllegalArgumentException();
    }
}
