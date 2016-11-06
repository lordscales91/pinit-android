package io.github.lordscales91.pinit.utils;

import android.app.Activity;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

public class PlayServicesUtils {
	/**
	 * Utility method to update Android's security provider.
	 * See: http://stackoverflow.com/a/36892715/3107765
	 */
	public static void updateProvider(Activity callingAct) {
		 try {
	            ProviderInstaller.installIfNeeded(callingAct);
	        } catch (GooglePlayServicesRepairableException e) {
	            // Thrown when Google Play Services is not installed, up-to-date, or enabled
	            // Show dialog to allow users to install, update, or otherwise enable Google Play services.
	            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingAct, 0);
	        } catch (GooglePlayServicesNotAvailableException e) {
	            Log.e("SecurityException", "Google Play Services not available.");
	        }
	}

}
