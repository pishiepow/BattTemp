package batttemp.me;

/**
 * Class: BatteryStats
 * Gets the temperature of the devices battery.
 *
 * Created by rich on 01/01/16.
 */

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.text.format.DateUtils;
import android.util.Log;

public class BatteryStats {
	private static BatteryStats _instance;
	private static final String LOG_TAG="bits.batttemp.battstats";
	private static final float BATTERY_TEMP_DIVISOR=10;
	private static long lLastUpdate=0;
	private static float fLastTemp=999;


	/** Constructor
	 *
	 * Blank constructor. This class is a singleton.
	 *
	 */
	private BatteryStats()
	{
		
	}


	/** Singleton Constructor
	 *
	 * Fetches or creates the singleton.
	 *
	 * @return - the instance of itself if it exists, or a new instance if not.
	 */
	public static BatteryStats getInstance()
	{
		if (_instance == null)
		{
			_instance = new BatteryStats();
		}
		return _instance;
	}


	/** getBatteryTempC
	 *
	 * Returns the battery temperature in degrees C.
	 *
	 * @return - the battery temperature in degrees C or 999 for error.
	 */
	public static float getBatteryTempC(Context conapp)
	{
		float fResult = 999;
		
		if ((lLastUpdate + (DateUtils.SECOND_IN_MILLIS * 9.5)) >= System.currentTimeMillis()) {
			// If the last update was within 9.5 seconds, use the last value to save battery life.
			fResult = fLastTemp;
			Log.i(LOG_TAG, "Battery Temperature Refreshed: " + String.valueOf(fResult) + "C [Cached]");
		} else {
			// Otherwise, get the actual battery temperature from SYSFS.
			fResult = getBatteryTempRaw(conapp);

			if (fResult == 9999) {
				// 9999 is the error code for getBatteryTempRaw and 999 is the error code
				// for this function.
				fResult = 999;
			} else {
				// Divide the SYSFS value by 10 to get the temperature in degrees C.
				fResult = fResult / BATTERY_TEMP_DIVISOR;
			}
				
			fLastTemp = fResult;
			lLastUpdate = System.currentTimeMillis();
			Log.i(LOG_TAG, "Battery Temperature Refreshed: " + String.valueOf(fResult) + "C");
		}
		
		return fResult;
	}


	/** getBatteryTempRaw
	 *
	 * Gets the raw value for the battery temperature from SYSFS.
	 *
	 * @return - the raw value for the battery temperature from SYSFS or 9999 for error.
	 */
    private static float getBatteryTempRaw(Context conapp)
    {
    	// 9999 means Error
    	float fResult = 9999;

    	try
    	{
			// Try reading from BatteryManager
			Intent iBatt = conapp.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
			fResult      = ((float) iBatt.getIntExtra(BatteryManager.EXTRA_TEMPERATURE,0));
			iBatt        = null;
    	}
    	catch (Exception ex)
    	{
			// Set fResult to 9999 (error) if it fails
			fResult = 9999;
    	}

   		return fResult;
    }
}


