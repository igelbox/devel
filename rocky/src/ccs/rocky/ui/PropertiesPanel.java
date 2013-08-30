package ccs.rocky.ui;

import ccs.rocky.core.Node;
import ccs.rocky.ui.views.NodeView;
import ccs.util.Exceptions;
import java.awt.BorderLayout;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author igel
 */
public class PropertiesPanel extends JPanel {

    private static class Model extends AbstractTableModel {

        private static class Prop {

            private final Object obj;
            private final String name;
            private final Method get, set;

            public Prop( Object obj, Method get ) {
                this.obj = obj;
                this.name = get.getName();
                this.get = get;
                Method s;
                try {
                    s = get.getDeclaringClass().getMethod( name, this.get.getReturnType() );
                } catch ( NoSuchMethodException e ) {
                    s = null;
                }
                this.set = s;
            }
        }
        private final List<Prop> props = new ArrayList<Prop>();

        public Model( Object o ) {
            if ( o != null ) {
                Class<?> c = o.getClass();
                while ( c != Object.class ) {
                    for ( Method m : c.getDeclaredMethods() )
                        if ( m.isAnnotationPresent( Node.Param.class ) )
                            props.add( new Prop( o, m ) );
                    c = c.getSuperclass();
                }
            }
        }

        @Override
        public int getRowCount() {
            return props.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public boolean isCellEditable( int rowIndex, int columnIndex ) {
            return (columnIndex == 1) && (props.get( rowIndex ).set != null);
        }

        @Override
        public Object getValueAt( int rowIndex, int columnIndex ) {
            Prop p = props.get( rowIndex );
            switch ( columnIndex ) {
                case 0:
                    return p.name;
                case 1:
                    try {
                        Float v = (Float) p.get.invoke( p.obj );
                        if ( v == (float) Math.E )
                            return "e";
                        if ( v == (float) Math.PI )
                            return "pi";
                        return v;
                    } catch ( Throwable t ) {
                        throw Exceptions.wrap( t );
                    }
            }
            return null;
        }

        @Override
        public void setValueAt( Object aValue, int rowIndex, int columnIndex ) {
            Prop p = props.get( rowIndex );
            try {
                if ( p.get.getReturnType() == float.class ) {
                    float v;
                    if ( "e".equals( aValue ) )
                        v = (float) Math.E;
                    else if ( "pi".equals( aValue ) )
                        v = (float) Math.PI;
                    else
                        v = Float.valueOf( aValue.toString() );
                    p.set.invoke( p.obj, v );
                }
            } catch ( Throwable t ) {
                throw Exceptions.wrap( t );
            }
        }
    }
    private final JTable table = new JTable();

    public PropertiesPanel() {
        super( new BorderLayout() );
        add( table );
    }

    public void setObject( Object obj ) {
        Object s;
        if ( obj instanceof NodeView )
            s = ((NodeView) obj).node();
        else
            s = null;
        table.setModel( new Model( s ) );
    }
}
