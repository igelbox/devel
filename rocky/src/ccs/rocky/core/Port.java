package ccs.rocky.core;

import ccs.rocky.views.PortView;

/**
 *
 * @author igel
 */
public abstract class Port {

    public static enum State {

        CONST, VAR, SIGNAL
    }

    public static class Output extends Port {

        public static class FixedState extends Output {

            private final State state;

            public FixedState( String id, Node node, String caption, State state ) {
                super( id, node, caption );
                this.state = state;
            }

            @Override
            public State state() {
                return state;
            }
        }

        public Output( String id, Node node, String caption ) {
            super( id, node, caption );
        }

        @Override
        public State state() {
            State s = State.CONST;
            for ( Port.Input i : node.inputs() ) {
                Port.Output o = i.connected();
                if ( o == null )
                    continue;
                State os = o.state();
                switch ( os ) {
                    case VAR:
                        if ( s == State.CONST )
                            s = os;
                        break;
                    case SIGNAL:
                        s = os;
                        break;
                }
            }
            return s;
        }

        @Override
        protected View createView() {
            return new PortView.Output( this );
        }
    }

    public static class Input extends Port {

        protected Output connected;

        public Input( String id, Node node, String caption ) {
            super( id, node, caption );
        }

        public Output connected() {
            return connected;
        }

        public void connect( Output to ) {
            connected = to;
        }

        @Override
        public State state() {
            Output p = connected;
            return p == null ? State.CONST : p.state();
        }

        @Override
        protected View createView() {
            return new PortView.Input( this );
        }
    }
    public final String id, caption;
    public final Node node;
    private View _view;

    public Port( String id, Node node, String caption ) {
        this.id = id;
        this.node = node;
        this.caption = caption;
    }

    protected abstract View createView();

    public final View view() {
        View r = _view;
        if ( r == null )
            _view = r = createView();
        return r;
    }

    public abstract State state();
}
