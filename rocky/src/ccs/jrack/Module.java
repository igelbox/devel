package ccs.jrack;

/**
 *
 * @author igel
 */
public abstract class Module {

    public abstract class Source {

        public abstract float get( int sample );
    }

    public abstract class Destination {

        public abstract int samplesCount();

        public abstract void set( int sample, float value );
    }

    public abstract class Pin {

        public abstract String name();
    }

    public abstract class Input extends Pin {

        protected Source source;

        public void connect( Source source ) {
            this.source = source;
        }

        public Source source() {
            return source;
        }
    }

    public abstract class Output extends Pin {

        protected Destination destination;

        public void connect( Destination source ) {
            this.destination = destination;
        }

        public Destination destination() {
            return destination;
        }
    }

    public int inputsCount() {
        return 0;
    }

    public Input input( int index ) {
        return null;
    }

    public int outputsCount() {
        return 0;
    }

    public Output output( int index ) {
        return null;
    }

    public boolean live() {
        return true;
    }

    public void process() {
    }
}
