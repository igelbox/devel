package ccs.flashlight;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends Activity {

    Camera cam;
    String fmbk;

    @Override
    public void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.main );
        final ToggleButton toggler = (ToggleButton) findViewById( R.id.toggler );
        toggler.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {

            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                flashlight( isChecked );
            }
        } );
    }

    void flashlight( boolean on ) {
        if ( on ) {
            cam = Camera.open();
            Camera.Parameters p = cam.getParameters();
            fmbk = p.getFlashMode();
            p.setFlashMode( Camera.Parameters.FLASH_MODE_TORCH );
            cam.setParameters( p );
            cam.startPreview();
        } else {
            Camera.Parameters p = cam.getParameters();
            p.setFlashMode( fmbk );
            cam.setParameters( p );
            cam.stopPreview();
            cam.release();
        }
    }
}
