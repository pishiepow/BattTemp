package batttemp.me;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {


    private static final String PREFS_FILE="rob";

    // Application Context
    Context conApp = null;

    // Service Intent
    Intent iBTS = null;

    // Alarm Manager Stuff
    Intent iServiceStart = null;
    PendingIntent piBOOT = null;

    // Real Time Timer
    TimerTask ttTask = null;
    Timer tTimer = null;

    // Text View
    TextView tvS = null;
    TextView tvVer = null;

    // Gauge
   // TempGauge tTemp;

    // Temperature
    float fTempC = 999;

    // Thread Handler
    final Handler hHan = new Handler();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_temp);


        conApp = this.getApplicationContext();

        tvS = (TextView) findViewById(R.id.tvStatus);
        tvVer = (TextView) findViewById(R.id.tvVersion);

        // Initialise Gauges ...
      //  tTemp = (TempGauge) findViewById(R.id.tgTemp);
      //  tTemp.setTitle("°C");
      //  tTemp.ReInit(90, 20, -10, 50, 10, 5);

        // Set Version
        PackageInfo piPI = null;

        try {
            piPI = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVer.setText("Version: " + piPI.versionName);
        } catch (Exception ex) {
            tvVer.setText("");
        } finally {
            piPI = null;
        }

        iBTS = new Intent(conApp, BattTempService.class);

        // Interface Alarm Manager
        iServiceStart = new Intent(conApp, BattTempService.class);
        piBOOT = PendingIntent.getService(conApp, 0, iServiceStart, PendingIntent.FLAG_UPDATE_CURRENT);

        // Local Timer
        // StartTimer(); -- onResume is automatically called now.
    }




    private void RefreshTemp() {
        hHan.post(rUI);
    }

    final Runnable rUI = new Runnable() {
        public void run() {
            try {


                fTempC = BatteryStats.getBatteryTempC(conApp);

                String strStatus = BatteryTemperature.TemperatureStatus(fTempC) + " - " + BatteryTemperature.TemperatureChargeStatus(fTempC);
                int newColor = BatteryTemperature.TemperatureColour(fTempC);

                tvS.setText(strStatus);
               // tTemp.setValue(fTempC, newColor);

            } catch (Exception ex) {
                // Ignore.
            }
        }
    };

    private void StartServices() {
        long lRefreshInterval = Math.round(DateUtils.MINUTE_IN_MILLIS / 2);

        // Not Required?
        //Time tNUT = new Time();
        //tNUT.set(lRefreshInterval);

        AlarmManager BSAlarm = (AlarmManager) conApp.getSystemService(Context.ALARM_SERVICE);
        BSAlarm.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), lRefreshInterval, piBOOT);
        BSAlarm = null;

        // Start Service
        // startService(iBTS); Alarm Manager should handle this.
    }

    private void StopServices () {
        AlarmManager BSAlarm = (AlarmManager) conApp.getSystemService(Context.ALARM_SERVICE);
        BSAlarm.cancel(piBOOT);
        BSAlarm = null;

        // Stop Service
        stopService(iBTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_temp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        SharedPreferences spSettings = conApp.getSharedPreferences(PREFS_FILE, 0);
        SharedPreferences.Editor eSettings = spSettings.edit();

        switch (item.getItemId()) {
            case R.id.mnuStart: 	eSettings.putBoolean("rob", true);
                StartServices();
                break;
            case R.id.mnuCloseAll:  eSettings.putBoolean("rob", false);
                StopServices();
                break;
        }

        // Commit the edits!
        eSettings.commit();
        eSettings = null;
        spSettings = null;

        return true;
    }

    @Override
    public void onPause() {
        super.onPause();

        StopTimer();

    }

    @Override
    public void onStop() {
        super.onStop();

        StopTimer();

    }

    @Override
    public void onResume() {
        super.onResume();

        StartTimer();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        StopTimer();

        iBTS = null;
        iServiceStart = null;
        piBOOT = null;

        try {
            //tTemp.destroyDrawingCache();
        } catch (Exception ex) {
            // Do Nothing
        } finally {
            //tTemp = null;
        }

        tvVer = null;
        tvS = null;

        conApp = null;
    }

    public void StartTimer () {
        // Release Everything first...
        StopTimer();

        ttTask = new TimerTask(){
            @Override
            public void run() {
                RefreshTemp();
            }
        };
        tTimer = new Timer();
        tTimer.schedule(ttTask, 0, 10000);
    }

    public void StopTimer () {
        try {
            tTimer.cancel();
        } catch (Exception ex) {
            // Do Nothing
        }

        tTimer = null;
        ttTask = null;
    }







}