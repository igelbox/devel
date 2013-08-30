package ccs.rocky.runtime;

import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.util.Exceptions;
import ccs.util.Iterabled;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
public class Compilator extends ClassLoader {

    private static final int LV_INDEX = 4;
    private int idGen;

    public Class<? extends RtModule> compile( Module module ) {
        String pname = "ccs/rocky/runtime/RtModule";
        String name = String.format( "ccs.rocky.runtime.RtModule_%08X", idGen++ ), xname = name.replaceAll( "\\.", "/" );
        ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS );
        cw.visit( Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, xname, null, pname, null );
        final String c_n_v = "ccs/rocky/nodes/Var";
        final String c_src = "ccs/rocky/runtime/Source";
        final String c_snk = "ccs/rocky/runtime/Sink";
        for ( Node n : module ) {
            if ( n instanceof ccs.rocky.nodes.Var )
                cw.visitField( Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldVar( n ), "L" + c_n_v + ";", null, null ).visitEnd();
            for ( Port p : Iterabled.multi( n.inputs(), n.outputs() ) )
                if ( p instanceof Source )
                    cw.visitField( Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldSS( "src", p ), "L" + c_src + ";", null, null ).visitEnd();
                else if ( p instanceof Sink )
                    cw.visitField( Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldSS( "snk", p ), "L" + c_snk + ";", null, null ).visitEnd();
        }
        {
            final String c_m = "ccs/rocky/core/Module";
            final String c_n = "ccs/rocky/core/Node";
            final String c_p = "Lccs/rocky/core/Port;";
            MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC, "<init>", "(L" + c_m + ";)V", null, null );
            mv.visitCode();
            mv.visitVarInsn( Opcodes.ALOAD, 0 );
            mv.visitVarInsn( Opcodes.ALOAD, 1 );
            mv.visitMethodInsn( Opcodes.INVOKESPECIAL, pname, "<init>", "(L" + c_m + ";)V" );
            for ( Node n : module ) {
                if ( n instanceof ccs.rocky.nodes.Var ) {
                    mv.visitVarInsn( Opcodes.ALOAD, 0 );
                    {
                        mv.visitVarInsn( Opcodes.ALOAD, 1 );
                        mv.visitLdcInsn( n.id() );
                        mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_m, "findNodeById", "(I)L" + c_n + ";" );
                    }
                    mv.visitTypeInsn( Opcodes.CHECKCAST, c_n_v );
                    mv.visitFieldInsn( Opcodes.PUTFIELD, xname, fieldVar( n ), "L" + c_n_v + ";" );
                }
                boolean nf = false;
                for ( Port p : Iterabled.multi( n.inputs(), n.outputs() ) )
                    if ( (p instanceof Source) || (p instanceof Sink) ) {
                        if ( !nf ) {
                            {
                                mv.visitVarInsn( Opcodes.ALOAD, 1 );
                                mv.visitLdcInsn( n.id() );
                                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_m, "findNodeById", "(I)L" + c_n + ";" );
                            }
                            mv.visitVarInsn( Opcodes.ASTORE, 2 );
                            nf = true;
                        }
                        mv.visitVarInsn( Opcodes.ALOAD, 0 );
                        {
                            mv.visitVarInsn( Opcodes.ALOAD, 2 );
                            mv.visitLdcInsn( p.id() );
                            mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_n, "findPortById", "(I)" + c_p );
                        }
                        if ( p instanceof Source ) {
                            mv.visitTypeInsn( Opcodes.CHECKCAST, c_src );
                            mv.visitFieldInsn( Opcodes.PUTFIELD, xname, fieldSS( "src", p ), "L" + c_src + ";" );
                        } else if ( p instanceof Sink ) {
                            mv.visitTypeInsn( Opcodes.CHECKCAST, c_snk );
                            mv.visitFieldInsn( Opcodes.PUTFIELD, xname, fieldSS( "snk", p ), "L" + c_snk + ";" );
                        }
                    }
            }
            mv.visitInsn( Opcodes.RETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }
        {
            Label loop = new Label();
            MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "process", "(IIF)V", null, null );
            mv.visitCode();
            mv.visitInsn( Opcodes.ICONST_0 );
            mv.visitVarInsn( Opcodes.ISTORE, LV_INDEX );
            int lvidGen = LV_INDEX + 1;
            Map<Object, Integer> imap = new HashMap<Object, Integer>();
            for ( Node n : module ) {
                if ( n instanceof ccs.rocky.nodes.Var ) {
                    int id = lvidGen++;
                    mv.visitVarInsn( Opcodes.ALOAD, 0 );
                    mv.visitFieldInsn( Opcodes.GETFIELD, xname, fieldVar( n ), "L" + c_n_v + ";" );
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_n_v, "value", "()F" );
                    mv.visitVarInsn( Opcodes.FSTORE, id );
                    imap.put( n, id );
                }
                for ( Port p : Iterabled.multi( n.inputs(), n.outputs() ) )
                    if ( p instanceof Source ) {
                        int id = lvidGen++;
                        mv.visitVarInsn( Opcodes.ALOAD, 0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD, xname, fieldSS( "src", p ), "L" + c_src + ";" );
                        mv.visitVarInsn( Opcodes.ILOAD, 1 );
                        mv.visitVarInsn( Opcodes.ILOAD, 2 );
                        mv.visitVarInsn( Opcodes.FLOAD, 3 );
                        mv.visitMethodInsn( Opcodes.INVOKEINTERFACE, c_src, "get", "(IIF)[F" );
                        mv.visitVarInsn( Opcodes.ASTORE, id );
                        imap.put( p, id );
                    } else if ( p instanceof Sink ) {
                        int id = lvidGen++;
                        mv.visitVarInsn( Opcodes.ALOAD, 0 );
                        mv.visitFieldInsn( Opcodes.GETFIELD, xname, fieldSS( "snk", p ), "L" + c_snk + ";" );
                        mv.visitMethodInsn( Opcodes.INVOKEINTERFACE, c_snk, "buffer", "()[F" );
                        mv.visitVarInsn( Opcodes.ASTORE, id );
                        imap.put( p, id );
                    }
            }
            Map<Port.Output, Integer> omap = new HashMap<Port.Output, Integer>();
            {
                Map<Port.Output, Integer> ocnt = new HashMap<Port.Output, Integer>();
                for ( Node n : module ) {
                    if ( n instanceof ccs.rocky.core.Module.In )
                        continue;
                    for ( Port.Input i : n.inputs() ) {
                        Port.Output o = i.connected();
                        if ( (o == null) || (o.node() instanceof ccs.rocky.core.Module.In) )
                            continue;
                        Integer cnt = ocnt.get( o );
                        cnt = cnt == null ? 1 : cnt + 1;
                        ocnt.put( o, cnt );
                    }
                }
                for ( Map.Entry<Port.Output, Integer> e : ocnt.entrySet() )
                    if ( e.getValue() > 1 ) {
                        int id = lvidGen++;
                        omap.put( e.getKey(), id );
                    }
            }
            mv.visitLabel( loop );
            //begin
            for ( Node n : module )
                for ( Port.Input i : n.inputs() )
                    if ( i instanceof Sink ) {
                        mv.visitVarInsn( Opcodes.ALOAD, imap.get( i ) );
                        mv.visitVarInsn( Opcodes.ILOAD, LV_INDEX );
                        gen( mv, i, imap, omap );
                        mv.visitInsn( Opcodes.FASTORE );
                    }
            //end
            mv.visitIincInsn( LV_INDEX, 1 );
            mv.visitVarInsn( Opcodes.ILOAD, LV_INDEX );
            mv.visitVarInsn( Opcodes.ILOAD, 1 );
            mv.visitJumpInsn( Opcodes.IF_ICMPLT, loop );
            mv.visitInsn( Opcodes.RETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }
        byte[] data = cw.toByteArray();
        storeClass( data, "X.class" );
        return (Class<? extends RtModule>) defineClass( name, data, 0, data.length );
    }

    private void gen( MethodVisitor mv, Port.Input i, Map<Object, Integer> imap, Map<Port.Output, Integer> omap ) {
        Port.Output o = i.connected();
        if ( o == null ) {
            mv.visitLdcInsn( 0.0f );
            return;
        }
        Integer lvo = omap.get( o );
        if ( (lvo == null) || (lvo > 0) ) {
            Integer lvi = imap.get( o );
            if ( lvi != null ) {
                mv.visitVarInsn( Opcodes.ALOAD, lvi );
                mv.visitVarInsn( Opcodes.ILOAD, LV_INDEX );
                mv.visitInsn( Opcodes.FALOAD );
            } else {
                Node node = o.node();
                for ( Port.Input p : node.inputs() )
                    gen( mv, p, imap, omap );
                if ( node instanceof ccs.rocky.nodes.Const )
                    mv.visitLdcInsn( ((ccs.rocky.nodes.Const) node).value() );
                else if ( node instanceof ccs.rocky.nodes.Var )
                    mv.visitVarInsn( Opcodes.FLOAD, imap.get( node ) );
                else if ( node instanceof ccs.rocky.nodes.ops.Sum )
                    mv.visitInsn( Opcodes.FADD );
                else if ( node instanceof ccs.rocky.nodes.ops.Mul )
                    mv.visitInsn( Opcodes.FMUL );
                else if ( node instanceof ccs.rocky.nodes.ops.Div )
                    mv.visitInsn( Opcodes.FDIV );
                else if ( node instanceof ccs.rocky.nodes.ops.Sig )
                    mv.visitMethodInsn( Opcodes.INVOKESTATIC, "java/lang/Math", "signum", "(F)F" );
                else if ( node instanceof ccs.rocky.nodes.ops.Abs )
                    mv.visitMethodInsn( Opcodes.INVOKESTATIC, "java/lang/Math", "abs", "(F)F" );
                else if ( node instanceof ccs.rocky.nodes.ops.Pow )
                    mv.visitMethodInsn( Opcodes.INVOKESTATIC, "ccs/rocky/runtime/FMath", "pow", "(FF)F" );
                else if ( node instanceof ccs.rocky.nodes.ops.Log )
                    mv.visitMethodInsn( Opcodes.INVOKESTATIC, "ccs/rocky/runtime/FMath", "log", "(F)F" );
                else if ( node instanceof ccs.rocky.nodes.ops.Exp )
                    mv.visitMethodInsn( Opcodes.INVOKESTATIC, "ccs/rocky/runtime/FMath", "exp", "(F)F" );
                else if ( node instanceof ccs.rocky.nodes.ops.Inv )
                    mv.visitMethodInsn( Opcodes.INVOKESTATIC, "ccs/rocky/runtime/FMath", "inv", "(F)F" );
                else if ( node instanceof ccs.rocky.nodes.Dot ) {
                } else
                    throw new UnsupportedOperationException( node.getClass().getName() );
            }
            if ( lvo != null ) {
                mv.visitInsn( Opcodes.DUP );
                mv.visitVarInsn( Opcodes.FSTORE, lvo );
                omap.put( o, -lvo );
            }
        } else
            mv.visitVarInsn( Opcodes.FLOAD, -lvo );
    }

    private static void storeClass( byte[] data, String name ) {
        try {
            FileOutputStream o = new FileOutputStream( name );
            try {
                o.write( data );
            } finally {
                o.close();
            }
        } catch ( Throwable e ) {
            throw Exceptions.wrap( e );
        }
    }

    private static String fieldVar( Node n ) {
        return String.format( "var%08X", n.id() );
    }

    private static String fieldSS( String pfx, Port p ) {
        return String.format( "%s%08X_%02X", pfx, p.node().id(), p.id() );
    }
}
