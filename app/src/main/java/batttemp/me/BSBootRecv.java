package batttemp.me;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import android.util.Log;

public class BSBootRecv extends BroadcastReceiver {

	private static final String LOG_TAG="bits.batttemp.bsboot";
	private static final String PREFS_FILE="rob";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		// Check should run or ignore...
		SharedPreferences spSettings = context.getSharedPreferences(PREFS_FILE, 0);
		boolean bRun = spSettings.getBoolean("rob", false);
		
		if (bRun == true) {
			Intent iServiceStart = new Intent(context, BattTempService.class);
			PendingIntent piBOOT = PendingIntent.getService(context, 0, iServiceStart, PendingIntent.FLAG_UPDATE_CURRENT);
			
			long lRefreshInterval = Math.round(DateUtils.MINUTE_IN_MILLIS / 2);
			
			Log.i(LOG_TAG, "Bootup complete received, starting battery temperature monitor...");
			
			AlarmManager BSAlarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			BSAlarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), lRefreshInterval, piBOOT);
			BSAlarm = null;			
		} else {
			Log.i(LOG_TAG, "Bootup complete received, auto-run disabled, quitting...");
		}
		
		spSettings = null;
		
	}

}
