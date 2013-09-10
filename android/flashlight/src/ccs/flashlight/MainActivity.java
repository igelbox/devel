package ccs.flashlight;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    Camera cam;
    String fmbk;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        LinearLayout contentView = new LinearLayout( this );
        contentView.setGravity( Gravity.CENTER );
        contentView.setBackgroundColor( Color.WHITE );
        setContentView( contentView );
        final ToggleButton toggler = new ToggleButton( this );
        contentView.addView( toggler );
        toggler.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                flashlight( isChecked );
            }
        } );
    }

    void flashlight( boolean on ) {
        if ( on ) {
            if ( cam != null )
                return;
            cam = Camera.open();
            Camera.Parameters p = cam.getParameters();
            fmbk = p.getFlashMode();
            p.setFlashMode( Camera.Parameters.FLASH_MODE_TORCH );
            cam.setParameters( p );
            cam.startPreview();
        } else {
            if ( cam == null )
                return;
            Camera.Parameters p = cam.getParameters();
            p.setFlashMode( fmbk );
            cam.setParameters( p );
            cam.stopPreview();
            cam.release();
            cam = null;
        }
    }
}
