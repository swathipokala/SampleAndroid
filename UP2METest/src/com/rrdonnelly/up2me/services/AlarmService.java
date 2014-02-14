package com.rrdonnelly.up2me.services;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.widget.Toast;

import com.rrdonnelly.up2me.LoginActivity;
import com.rrdonnelly.up2me.SyncProcessActivity;
import com.rrdonnelly.up2me.dao.SettingsDAO;
import com.rrdonnelly.up2me.dao.UserDAO;
import com.rrdonnelly.up2me.valueobjects.User;

public class AlarmService extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";

	SharedPreferences prefsServiceAlarm = null;
	SharedPreferences.Editor prefsServiceAlarmEditor = null;
	Context context;
	SyncProcessActivity sync;
	SettingsDAO settingsDAO;

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
		wl.acquire();
		Bundle extras = intent.getExtras();
		if (extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)) {

		} else {
			if (isAppRunning(context)) {
				ConnectivityManager cm = (ConnectivityManager) context
						.getSystemService(Activity.CONNECTIVITY_SERVICE);

				if (cm != null && cm.getActiveNetworkInfo() != null
						&& cm.getActiveNetworkInfo().isConnected()) {
					prefsServiceAlarm = context.getSharedPreferences(
							"ServiceAlarm", 0);
					boolean start = prefsServiceAlarm.getBoolean("starting",
							false);

					if (start == true) {
						prefsServiceAlarm = context.getSharedPreferences(
								"ServiceAlarm", 0);
						prefsServiceAlarmEditor = prefsServiceAlarm.edit();
						prefsServiceAlarmEditor.putBoolean("starting", false);
						prefsServiceAlarmEditor.commit();

					} else {
						sync = new SyncProcessActivity();
						settingsDAO = new SettingsDAO();
						new RunAlarmTask(context).execute("");
						
						
					}
				} else {

				}
			}
		}
		wl.release();
	}

	public void SetAlarm(Context context, String time, String duration,
			String userToken, String userName, String salt, long loginUserId) {

		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmService.class);
		intent.putExtra(ONE_TIME, Boolean.FALSE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

		SimpleDateFormat sdfdate = new SimpleDateFormat("hh:mm a");
		Date selectedDate;
		try {
			selectedDate = sdfdate.parse(time);
			long selected = selectedDate.getTime();

			Calendar currentDate = Calendar.getInstance();
			String strCurrentDate = sdfdate.format(currentDate.getTime());

			// if(currentDateMilli == selected){
			am.setRepeating(AlarmManager.RTC_WAKEUP, selected,
					1000 * 60 * Integer.parseInt(duration), pi);
			// }

		} catch (ParseException e) {
			e.printStackTrace();
		}

	}

	protected boolean isAppRunning(Context context) {
		String activity = LoginActivity.class.getName();
		ActivityManager activityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);

		List<RunningTaskInfo> tasks = activityManager
				.getRunningTasks(Integer.MAX_VALUE);

		for (RunningTaskInfo task : tasks) {
			if (activity.equals(task.baseActivity.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmService.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public void setOnetimeTimer(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmService.class);
		intent.putExtra(ONE_TIME, Boolean.TRUE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
	}
	
	private class RunAlarmTask extends AsyncTask<String, Integer, String> {

		private Context context;

		public RunAlarmTask(Context context) {
			this.context = context;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected String doInBackground(String... sUrl) {
			try {
				long lastSyncDateTime = 0l;
				
				lastSyncDateTime = settingsDAO.getLastSyncDateTime(
						context,
						prefsServiceAlarm.getLong("loginUserId", 1));
				
//				Toast.makeText(context, "Running", Toast.LENGTH_LONG)
//						.show();
				sync.syncStatementsProcess(context,
						prefsServiceAlarm.getString("userToken", null),
						prefsServiceAlarm.getString("userName", null),
						prefsServiceAlarm.getString("salt", null),
						prefsServiceAlarm.getLong("loginUserId", 1),
						lastSyncDateTime);
				sync.syncOffersProcess(context,
						prefsServiceAlarm.getString("userToken", null),
						prefsServiceAlarm.getString("userName", null),
						prefsServiceAlarm.getString("salt", null),
						prefsServiceAlarm.getLong("loginUserId", 1),
						lastSyncDateTime);
				sync.syncOfferProvidersProcess(context,
						prefsServiceAlarm.getString("userToken", null),
						prefsServiceAlarm.getString("userName", null),
						prefsServiceAlarm.getString("salt", null),
						prefsServiceAlarm.getLong("loginUserId", 1),
						lastSyncDateTime);
				sync.syncTagsProcess(context,
						prefsServiceAlarm.getString("userToken", null),
						prefsServiceAlarm.getString("userName", null),
						prefsServiceAlarm.getString("salt", null),
						prefsServiceAlarm.getLong("loginUserId", 1),
						lastSyncDateTime);
				sync.syncDocumentProvidersProcess(context,
						prefsServiceAlarm.getString("userToken", null),
						prefsServiceAlarm.getString("userName", null),
						prefsServiceAlarm.getString("salt", null),
						prefsServiceAlarm.getLong("loginUserId", 1),
						lastSyncDateTime);
				sync.syncCloudProvidersProcess(context, 
						prefsServiceAlarm.getString("userToken", null),
						prefsServiceAlarm.getString("userName", null),
						prefsServiceAlarm.getString("salt", null),
						prefsServiceAlarm.getLong("loginUserId", 1),
						lastSyncDateTime);
				sync.syncUserFavorite(context, 
						prefsServiceAlarm.getLong("loginUserId", 1),
						prefsServiceAlarm.getString("userToken", null),
						prefsServiceAlarm.getString("userName", null),
						prefsServiceAlarm.getString("salt", null),
						lastSyncDateTime);
				
				sync.synUserLibrariesProcess(context, prefsServiceAlarm.getString("userToken", null),
						prefsServiceAlarm.getString("userName", null),
						prefsServiceAlarm.getString("salt", null),
						prefsServiceAlarm.getLong("loginUserId", 1), lastSyncDateTime);
				
				User user = UserDAO.getUserByUserName(prefsServiceAlarm.getString("userName", null), context);
				UserService.getUserInformation(context, prefsServiceAlarm.getLong("loginUserId", 1),  user.getPassword());
				} catch (Exception e) {
					return e.toString();
				} 
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
		}
	}

}