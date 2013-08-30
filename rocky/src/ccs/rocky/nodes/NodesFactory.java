package ccs.rocky.nodes;

import ccs.rocky.core.Node;

/**
 *
 * @author igel
 */
public class NodesFactory {

    public static final Class<? extends Node>[] CLASSES = new Class[]{
        ccs.rocky.nodes.Var.class,
        ccs.rocky.nodes.Dot.class,
        ccs.rocky.nodes.Buff.class,
        ccs.rocky.nodes.Timer.class,
        ccs.rocky.nodes.Osc.class,
        ccs.rocky.nodes.Test.class,
        ccs.rocky.nodes.ops.Abs.class,
        ccs.rocky.nodes.ops.Inv.class,
        ccs.rocky.nodes.ops.Div.class,
        ccs.rocky.nodes.ops.Mod.class,
        ccs.rocky.nodes.ops.Log.class,
        ccs.rocky.nodes.ops.Exp.class,
        ccs.rocky.nodes.ops.Mul.class,
        ccs.rocky.nodes.ops.Pow.class,
        ccs.rocky.nodes.ops.Sig.class,
        ccs.rocky.nodes.ops.Sum.class,
        ccs.rocky.nodes.ops.Sub.class,
        ccs.rocky.nodes.ops.Sin.class };
}
