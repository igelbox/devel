package ccs.swappy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author igel
 */
public class MountSwap extends BroadcastReceiver {

    @Override
    public void onReceive( Context context, Intent intent ) {
        if ( Intent.ACTION_BOOT_COMPLETED.equals( intent.getAction() ) )
            sudo( "swapon /dev/block/mmcblk1p2" );
    }

    static int sudo( String cmd ) {
        try {
            return Runtime.getRuntime().exec( "su -c " + cmd ).waitFor();
        } catch ( IOException e ) {
            Logger.getLogger( MountSwap.class.getName() ).log( Level.SEVERE, null, e );
            return -1;
        } catch ( InterruptedException e ) {
            Logger.getLogger( MountSwap.class.getName() ).log( Level.SEVERE, null, e );
            return -1;
        }
    }
}
