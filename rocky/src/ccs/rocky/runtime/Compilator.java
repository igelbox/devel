package ccs.rocky.runtime;

import ccs.rocky.core.Module;
import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import ccs.rocky.nodes.Buff;
import ccs.rocky.nodes.Var;
import ccs.util.Exceptions;
import ccs.util.Iterabled;
import java.io.FileOutputStream;
import java.util.*;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 *
 * @author igel
 */
public class Compilator extends ClassLoader {

    private static final int LV_INDEX = 2;
    private int idGen;

    public Class<? extends RtModule> compile( Module module, int samples, int samplerate ) {
        String pname = "ccs/rocky/runtime/RtModule";
        String name = String.format( "ccs.rocky.runtime.RtModule_%08X", idGen++ ), xname = name.replaceAll( "\\.", "/" );
        ClassWriter cw = new ClassWriter( ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS );
        cw.visit( Opcodes.V1_6, Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC | Opcodes.ACC_FINAL, xname, null, pname, null );
        final String c_n_v = "ccs/rocky/nodes/Var";
        final String c_src = "ccs/rocky/runtime/Source";
        final String c_snk = "ccs/rocky/runtime/Sink";
        final String c_buf = "ccs/rocky/runtime/Buffer";
        final Collection<Port.Input> sinks = new ArrayList<Port.Input>();
        for ( Node n : module )
            for ( Port.Input p : n.inputs() )
                if ( p instanceof Sink ) {
                    sinks.add( p );
                    cw.visitField( Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldSS( "snk", p ), 'L' + c_snk + ';', null, null ).visitEnd();
                }
        final Set<Object> used = new HashSet<Object>();
        for ( Port.Input i : sinks )
            used( used, i );
        Collection<Var> vars = new ArrayList<Var>();
        Collection<Buff> bufs = new ArrayList<Buff>();
        Collection<Port.Output> sources = new ArrayList<Port.Output>();
        for ( Node n : module ) {
            if ( used.contains( n ) )
                if ( n instanceof Var ) {
                    vars.add( (Var) n );
                    cw.visitField( Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldVar( n ), 'L' + c_n_v + ';', null, null ).visitEnd();
                } else if ( n instanceof Buff ) {
                    bufs.add( (Buff) n );
                    cw.visitField( Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldBuff( n ), 'L' + c_buf + ';', null, null ).visitEnd();
                }
            for ( Port.Output p : n.outputs() )
                if ( (p instanceof Source) && used.contains( p ) ) {
                    sources.add( p );
                    cw.visitField( Opcodes.ACC_PRIVATE | Opcodes.ACC_FINAL, fieldSS( "src", p ), 'L' + c_src + ';', null, null ).visitEnd();
                }
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
            for ( Var v : vars ) {
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                {
                    mv.visitVarInsn( Opcodes.ALOAD, 1 );
                    mv.visitLdcInsn( v.id() );
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_m, "findNodeById", "(I)L" + c_n + ";" );
                }
                mv.visitTypeInsn( Opcodes.CHECKCAST, c_n_v );
                mv.visitFieldInsn( Opcodes.PUTFIELD, xname, fieldVar( v ), "L" + c_n_v + ";" );
            }
            for ( Buff b : bufs ) {
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                {
                    mv.visitTypeInsn( Opcodes.NEW, c_buf );
                    mv.visitInsn( Opcodes.DUP );
                    mv.visitIntInsn( Opcodes.SIPUSH, samples );
                    mv.visitMethodInsn( Opcodes.INVOKESPECIAL, c_buf, "<init>", "(I)V" );
                }
                mv.visitFieldInsn( Opcodes.PUTFIELD, xname, fieldBuff( b ), "L" + c_buf + ";" );
            }
            Node nf = null;
            for ( Port p : Iterabled.multi( sinks, sources ) ) {
                Node n = p.node();
                if ( nf != n ) {
                    mv.visitVarInsn( Opcodes.ALOAD, 1 );
                    mv.visitLdcInsn( p.node().id() );
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_m, "findNodeById", "(I)L" + c_n + ";" );
                    mv.visitVarInsn( Opcodes.ASTORE, 2 );
                    nf = n;
                }
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                mv.visitVarInsn( Opcodes.ALOAD, 2 );
                mv.visitLdcInsn( p.id() );
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_n, "findPortById", "(I)" + c_p );
                if ( p instanceof Source ) {
                    mv.visitTypeInsn( Opcodes.CHECKCAST, c_src );
                    mv.visitFieldInsn( Opcodes.PUTFIELD, xname, fieldSS( "src", p ), "L" + c_src + ";" );
                } else if ( p instanceof Sink ) {
                    mv.visitTypeInsn( Opcodes.CHECKCAST, c_snk );
                    mv.visitFieldInsn( Opcodes.PUTFIELD, xname, fieldSS( "snk", p ), "L" + c_snk + ";" );
                }
            }
            mv.visitInsn( Opcodes.RETURN );
            mv.visitMaxs( 1, 1 );
            mv.visitEnd();
        }
        {
            Label loop = new Label();
            MethodVisitor mv = cw.visitMethod( Opcodes.ACC_PUBLIC | Opcodes.ACC_FINAL, "process", "(F)V", null, null );
            mv.visitCode();
            mv.visitIntInsn( Opcodes.SIPUSH, samples - 1 );
            mv.visitVarInsn( Opcodes.ISTORE, LV_INDEX );
            int lvidGen = LV_INDEX + 1;
            Map<Object, Integer> imap = new HashMap<Object, Integer>();
            for ( Var v : vars ) {
                int id = lvidGen++;
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD, xname, fieldVar( v ), 'L' + c_n_v + ';' );
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_n_v, "value", "()F" );
                mv.visitVarInsn( Opcodes.FSTORE, id );
                imap.put( v, id );
            }
            for ( Buff b : bufs ) {
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD, xname, fieldBuff( b ), 'L' + c_buf + ';' );
                if ( used.contains( b.max() ) ) {
                    mv.visitInsn( Opcodes.DUP );
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_buf, "max", "()F" );
                    int id = lvidGen++;
                    mv.visitVarInsn( Opcodes.FSTORE, id );
                    imap.put( b.max(), id );
                }
                if ( used.contains( b.min() ) ) {
                    mv.visitInsn( Opcodes.DUP );
                    mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_buf, "min", "()F" );
                    int id = lvidGen++;
                    mv.visitVarInsn( Opcodes.FSTORE, id );
                    imap.put( b.min(), id );
                }
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_buf, "buffer", "()[F" );
                int id = lvidGen++;
                mv.visitVarInsn( Opcodes.ASTORE, id );
                imap.put( b.input(), id );
            }
            for ( Port.Output p : sources ) {
                int id = lvidGen++;
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD, xname, fieldSS( "src", p ), "L" + c_src + ";" );
                mv.visitVarInsn( Opcodes.FLOAD, 1 );
                mv.visitMethodInsn( Opcodes.INVOKEINTERFACE, c_src, "get", "(F)[F" );
                mv.visitVarInsn( Opcodes.ASTORE, id );
                imap.put( p, id );
            }
            for ( Port.Input p : sinks ) {
                int id = lvidGen++;
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD, xname, fieldSS( "snk", p ), "L" + c_snk + ";" );
                mv.visitMethodInsn( Opcodes.INVOKEINTERFACE, c_snk, "buffer", "()[F" );
                mv.visitVarInsn( Opcodes.ASTORE, id );
                imap.put( p, id );
            }
            Map<Port.Output, Integer> omap = new HashMap<Port.Output, Integer>();
            {
                Map<Port.Output, Integer> ocnt = new HashMap<Port.Output, Integer>();
                for ( Node n : module )
                    for ( Port.Input i : n.inputs() ) {
                        Port.Output o = i.connected();
                        if ( o == null )
                            continue;
                        Integer cnt = ocnt.get( o );
                        cnt = cnt == null ? 1 : cnt + 1;
                        ocnt.put( o, cnt );
                    }
                for ( Map.Entry<Port.Output, Integer> e : ocnt.entrySet() )
                    if ( e.getValue() > 1 ) {
                        int id = lvidGen++;
                        omap.put( e.getKey(), id );
                    }
            }
            mv.visitLabel( loop );
            //begin
            for ( Buff b : bufs ) {
                Port.Input i = b.input();
                mv.visitVarInsn( Opcodes.ALOAD, imap.get( i ) );
                mv.visitVarInsn( Opcodes.ILOAD, LV_INDEX );
                gen( mv, i, imap, omap );
                mv.visitInsn( Opcodes.FASTORE );
            }
            for ( Port.Input i : sinks ) {
                mv.visitVarInsn( Opcodes.ALOAD, imap.get( i ) );
                mv.visitVarInsn( Opcodes.ILOAD, LV_INDEX );
                gen( mv, i, imap, omap );
                mv.visitInsn( Opcodes.FASTORE );
            }
            //end
            mv.visitIincInsn( LV_INDEX, -1 );
            mv.visitVarInsn( Opcodes.ILOAD, LV_INDEX );
            mv.visitJumpInsn( Opcodes.IFGE, loop );
            for ( Buff b : bufs ) {
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD, xname, fieldBuff( b ), 'L' + c_buf + ';' );
                mv.visitMethodInsn( Opcodes.INVOKEVIRTUAL, c_buf, "add", "()V" );
            }
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
            if ( lvi != null )
                if ( o.node() instanceof Buff )
                    mv.visitVarInsn( Opcodes.FLOAD, lvi );
                else {
                    mv.visitVarInsn( Opcodes.ALOAD, lvi );
                    mv.visitVarInsn( Opcodes.ILOAD, LV_INDEX );
                    mv.visitInsn( Opcodes.FALOAD );
                }
            else {
                Node node = o.node();
                for ( Port.Input p : node.inputs() )
                    gen( mv, p, imap, omap );
                if ( node instanceof ccs.rocky.nodes.Const )
                    mv.visitLdcInsn( ((ccs.rocky.nodes.Const) node).value() );
                else if ( node instanceof ccs.rocky.nodes.Var )
                    mv.visitVarInsn( Opcodes.FLOAD, imap.get( node ) );
                else if ( node instanceof ccs.rocky.nodes.ops.Sum )
                    mv.visitInsn( Opcodes.FADD );
                else if ( node instanceof ccs.rocky.nodes.ops.Sub )
                    mv.visitInsn( Opcodes.FSUB );
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
                else if ( node instanceof ccs.rocky.nodes.Buff ) {
                } else if ( node instanceof ccs.rocky.nodes.Dot ) {
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

    private static String fieldBuff( Node n ) {
        return String.format( "buff%08X", n.id() );
    }

    private static String fieldSS( String pfx, Port p ) {
        return String.format( "%s%08X_%02X", pfx, p.node().id(), p.id() );
    }

    private static void used( Set<Object> s, Port.Input i ) {
        Port.Output o = i.connected();
        if ( o == null )
            return;
        if ( !s.add( o ) )
            return;
        Node n = o.node();
        if ( !s.add( n ) )
            return;
        for ( Port.Input p : n.inputs() )
            used( s, p );
    }
}
