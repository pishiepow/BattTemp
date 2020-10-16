package batttemp.me;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class BattTempService extends Service {

	// Application Context
	private Context conApp = null;
	
	// Notification Icon
	private NotificationManager nMan = null;
    private Notification.Builder nBuild = null;
	private PendingIntent nPendingIntent = null;
	private static final int NOTIFICATION_ID = 1;	

	// Temperature (Raw)
	private float fTempC = 999;
    private float fLastTempC = 999;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void onCreate() {
        // Initialise Context (to this FIRST)
        conApp = this.getApplicationContext();

        // Reset Variables
        fLastTempC = -999;

        // Initialise Notification
        nMan = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nPendingIntent = PendingIntent.getActivity(conApp, 0, new Intent(conApp, MainActivity.class), 0);

        nBuild = new Notification.Builder(this);
        nBuild.setContentTitle("Battery Temperature");
        nBuild.setContentIntent(nPendingIntent);
        nBuild.setOnlyAlertOnce(true);
        nBuild.setOngoing(true);
    }

    @Override
    public void onDestroy() {
        // Kill Notification
        try
        {
            nMan.cancel(NOTIFICATION_ID);
        } finally {
            nBuild = null;
            nPendingIntent = null;
            nMan = null;
            conApp = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Get Battery Temperature
        int iIcon = GetTempUpdate();

        if (Math.abs(fLastTempC - fTempC) <= 1) {
            // Battery Temperature has not changed by more than 1 degree! Do not update.
        } else {
            String strCurTemp = "";
            String strCurStatus = BatteryTemperature.TemperatureStatus(fTempC);
            int iTempColour = BatteryTemperature.TemperatureColour(fTempC);

            if (fTempC == 999) {
                strCurTemp = "Unknown°C";
            } else {
                strCurTemp = String.valueOf(fTempC) + "°C";
            }

            // Update Last Temperature
            fLastTempC = fTempC;

            // Start Notification
            SetNotificationParams(iIcon, iTempColour, strCurTemp + " - " + strCurStatus);
            nMan.notify(NOTIFICATION_ID, nBuild.build());
        }

        // Stop service when done.
        return START_NOT_STICKY;
    }

    private void SetNotificationParams(int iIcon, int iColour, String value) {
        nBuild.setWhen(System.currentTimeMillis());
        nBuild.setSmallIcon(iIcon);
        nBuild.setContentText(value);
        nBuild.setColor(iColour);
    }

    private int GetTempUpdate() {
        fTempC = BatteryStats.getBatteryTempC(conApp);
        return BatteryTemperature.TemperatureDrawable(fTempC);
    }

}

