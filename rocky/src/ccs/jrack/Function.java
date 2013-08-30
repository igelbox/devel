package ccs.jrack;

/**
 *
 * @author igel
 */
public abstract class Function {

    public static abstract class Arg {

        public static abstract class Back extends Arg {

            public abstract float get( int backOffset );
        }

        public static class Const extends Arg.Back {

            private final float value;

            public Const( float value ) {
                this.value = value;
            }

            @Override
            public float get() {
                return value;
            }

            public float get( int backOffset ) {
                return get();
            }
        }

        public static class Func extends Arg {

            private final Function f;

            public Func( Function f ) {
                this.f = f;
            }

            @Override
            public float get() {
                return f.apply();
            }
        }

        public abstract float get();
    }

    public abstract float apply();
}
