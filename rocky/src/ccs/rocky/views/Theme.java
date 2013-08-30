package ccs.rocky.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;

/**
 *
 * @author igel
 */
public interface Theme {

    Color FILL_DEFAULT = Color.LIGHT_GRAY;
    Color BRDR_DEFAULT = Color.DARK_GRAY;
    Color FILL_SELECTED = Color.getHSBColor( 0.55f, 0.33f, 1.00f );
    Color BRDR_SELECTED = Color.getHSBColor( 0.55f, 1.00f, 1.00f );
    Stroke STROKE1 = new BasicStroke( 1 );
    Stroke STROKE3 = new BasicStroke( 3 );
    Font FONT_TITLE = Font.decode( Font.MONOSPACED + "-BOLD" );
    Font FONT_TITLE_P = Font.decode( Font.MONOSPACED + "-ITALIC-10" );
    FontRenderContext FRC = new FontRenderContext( null, true, true );
}
