package ccs.rocky.runtime;

import ccs.rocky.core.Port;
import org.objectweb.asm.MethodVisitor;

/**
 *
 * @author igel
 */
public interface Generatable {

    interface Locals {

        int sampleVar();

        int timeVar();

        int newVar();
    }

    Generator generator();

    abstract static class Generator {

        public abstract static class Fieldable extends Generator {

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
