package ccs.rocky.runtime;

import ccs.rocky.core.Node;
import ccs.rocky.core.Port;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 *
 * @author igel
 */
public interface Generatable {

    interface Locals {

        String clazz();

        String fieldName( Node n );

        int sampleVar();

        int timeVar();

        int newVar();
    }

    Generator generator();

    abstract static class Generator {

        public abstract static class Fieldable extends Generator {

            protected final void pushNode( MethodVisitor mv, Locals locals, Node n ) {
                mv.visitVarInsn( Opcodes.ALOAD, 0 );
                mv.visitFieldInsn( Opcodes.GETFIELD, locals.clazz(), locals.fieldName( n ), Type.getDescriptor( n.getClass() ) );
            }

            @Override
            public void gen_prolog( MethodVisitor mv, Locals locals, int samples, int samplerate ) {
            }
        }

        public void gen_prolog( MethodVisitor mv, Locals locals, int samples, int samplerate ) {
        }

        /**
         * На стеке:
         * вход: значения для всех подключённых входов
         * выход: значение для заданного выхода
         */
        public abstract void gen_inloop( MethodVisitor mv, Port.Output out );
    }
}
