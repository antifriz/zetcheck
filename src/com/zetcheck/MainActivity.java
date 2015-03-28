/*
Copyright 2015 Ivan Jurin

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */package com.zetcheck;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.util.Locale;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.zetcheck.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity
{
	private static final String URL_STATIONS = "http://zet.icytexx.com/zetCheckServer.php?ilko=true&stations=1";
	private static final String URL_LINES = "http://zet.icytexx.com/zetCheckServer.php?ilko=true&lines=1";
	public static final String STATIONS_DATA_FILE_NAME = "stations.ser";
	public static final String SETTINGS_DATA_FILE_NAME = "settings.ser";
//	public static final String SETUP_DATA_FILE_NAME = "setup.ser";
	public static final String LINES_DATA_FILE_NAME = "lines.ser";

	private static TextView loadingMessage;
	private static ProgressBar progressBar;
	public static Stations stations = null;
	public static SettingsData settingsData = null;
	public SetupData setupData = null;
	public static Lines lines = null;
	private ReentrantLock lock = new ReentrantLock();
	private Context context = this;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (Build.VERSION.SDK_INT == 8)
			AppData.isFroyo = true;
		else
			AppData.isFroyo = false;

		loadingMessage = (TextView) findViewById(R.id.loading_main_textView);
		progressBar = (ProgressBar) findViewById(R.id.loading_main_progressBar);

		startLoadingStationsData();
		
	}
	
	public void startLoadingStationsData()
	{
		loadingMessage.setText("");
		new StationsLoaderAsyncTask().execute();
	}

	private void finishActivity()
	{
		loadingMessage.setText("");
		progressBar.setVisibility(View.GONE);
		new AlertDialog.Builder(this)
				.setTitle("Fatal error")
				.setMessage(
						getResources().getString(R.string.check_connection)
								+ "\n")
				.setNeutralButton("OK", new OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						finish();
					}
				}).show();
	}

	public void setLocale(String language)
	{
		Locale locale = new Locale(language);
		Locale.setDefault(locale);
		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());
	}
	
	private void callNextActivity()
	{
		AppData.stations = stations;
//		AppData.setupData = setupData;
		AppData.settingsData = settingsData;
		AppData.lines = lines;

		
		Intent intent;
		if (!AppData.isFroyo)
		{
			MenuActivity.STAY_LIVE = true;
			intent = new Intent(this, MenuActivity.class);
		}
		else
		{
			MenuActivity_low.STAY_LIVE = true;
			intent = new Intent(this, MenuActivity_low.class);
		}
		startActivity(intent);
		finish();
	}

	private class StationsLoaderAsyncTask extends
			AsyncTask<Void, Void, Boolean>
	{
		private void setLoadingInfo(String message)
		{
			lock.lock();
			Message msg = new Message();
			msg.obj = message;
			mainHandler.sendMessage(msg);
			lock.unlock();
		}

		@Override
		protected Boolean doInBackground(Void... params)
		{
//			loadSetup();
//			if(AppData.setupData==null)
//				runOnUiThread(new Runnable()
//				{
//					public void run()
//					{
//						Toast.makeText(getApplicationContext(), "Favorite mo�ete uklju�iti u postavkama",Toast.LENGTH_LONG).show();
//					}
//				});
			/*String l=null;
			if (AppData.setupData != null
					&& AppData.setupData.language == SetupData.english)
				l="en_GB";
			else if (AppData.setupData != null
					&& AppData.setupData.language == SetupData.croatian)
				l="hr";
			final String ll = l;
			if(ll!=null)
				runOnUiThread(new Runnable()
				{
					public void run()
					{

						setLocale(ll);
					}
				});
			if (AppData.setupData != null)
				Log.e("lang", "::" + AppData.setupData.language);
			else
				Log.e("sad", "noo");*/
			
			if (!AppData.isNetworkReady(context))
			{
				setLoadingInfo(getResources().getString(
						R.string.checking_network_status));
				return false;
			}

			for (int i = 0; i < 2; i++)
				if (getStationData())
					break;

			return true;
		}

		protected boolean getStationData()
		{
			
			// if(setupData==null)
			// {
			// runOnUiThread(new Runnable()
			// {
			// public void run()
			// {
			// // AlertDialog.Builder alertDialogBuilder = new
			// AlertDialog.Builder(context);
			// //
			// alertDialogBuilder.setMessage(getResources().getString(R.string.info)).setNeutralButton("OK",
			// new OnClickListener()
			// // {
			// // @Override
			// // public void onClick(DialogInterface arg0, int arg1)
			// // {
			// // alertDialog2=new
			// AlertDialog.Builder(context).setMessage(getResources().getString(R.string.info)).setNeutralButton("OK",
			// null).show();
			// // }
			// // });
			// // alertDialog1 = alertDialogBuilder.show();
			// }
			// });

			// }
			setLoadingInfo(getResources().getString(
					R.string.searching_stations_data));
			try
			{
				FileInputStream fis = getApplicationContext().openFileInput(
						STATIONS_DATA_FILE_NAME);
				ObjectInputStream os = new ObjectInputStream(fis);

				setLoadingInfo(getResources().getString(R.string.loading_data));
				stations = (Stations) os.readObject();
				setLoadingInfo(getResources().getString(
						R.string.data_loaded_stations));
				os.close();

				return true;
			}
			catch (Exception e)
			{
				setLoadingInfo(getResources().getString(R.string.connecting));
			}
			String result = "";
			HttpResponse response = null;
			try
			{
				URI uri = new URI(URL_STATIONS);

				DefaultHttpClient client = new DefaultHttpClient();

				setLoadingInfo(getResources().getString(
						R.string.getting_stations_data));

				HttpUriRequest request = new HttpGet(uri);

				response = client.execute(request);
				HttpEntity entity = response.getEntity();

				setLoadingInfo(getResources().getString(
						R.string.getting_successful));
				result = EntityUtils.toString(entity);
			}
			catch (Exception e)
			{
				return false;
			}
			setLoadingInfo(getResources().getString(
					R.string.parsing_stations_data));
			result = AppData.convertCro(result);

			stations = new Stations(result);
			try
			{
				setLoadingInfo(getResources().getString(
						R.string.saving_stations_data));
				FileOutputStream fos = getApplicationContext().openFileOutput(
						STATIONS_DATA_FILE_NAME, Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);

				os.writeObject(stations);
				setLoadingInfo(getResources().getString(R.string.data_saved));
				os.close();
			}
			catch (Exception e)
			{
				Toast.makeText(
						getParent(),
						getResources()
								.getString(R.string.saving_stations_error),
						Toast.LENGTH_LONG).show();
			}
			return true;
		}

//		private void loadSetup()
//		{
//			try
//			{
//				FileInputStream fis = getApplicationContext().openFileInput(
//						SETUP_DATA_FILE_NAME);
//				ObjectInputStream os = new ObjectInputStream(fis);
//				setupData = (SetupData) os.readObject();
//				os.close();
//			}
//			catch (Exception e)
//			{
//				setupData = new SetupData();
//			}
//		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result)
			{
				new LinesLoaderAsyncTask().execute();
			}
			else
			{
				finishActivity();
			}
		}
	}

	private class LinesLoaderAsyncTask extends AsyncTask<Void, Void, Boolean>
	{
		private void setLoadingInfo(String message)
		{
			lock.lock();
			Message msg = new Message();
			msg.obj = message;
			mainHandler.sendMessage(msg);
			lock.unlock();
		}

		@Override
		protected Boolean doInBackground(Void... params)
		{
			if (!AppData.isNetworkReady(context))
			{
				setLoadingInfo(getResources().getString(
						R.string.checking_network_status));
				return false;
			}

			for (int i = 0; i < 2; i++)
				if (getLineData())
					break;

			loadSettings();

			return true;
		}

		protected boolean getLineData()
		{
			setLoadingInfo(getResources().getString(
					R.string.searching_lines_data));

			try
			{
				FileInputStream fis = getApplicationContext().openFileInput(
						LINES_DATA_FILE_NAME);
				ObjectInputStream os = new ObjectInputStream(fis);

				setLoadingInfo(getResources().getString(R.string.loading_data));
				lines = (Lines) os.readObject();
				setLoadingInfo(getResources().getString(
						R.string.data_loaded_lines));
				os.close();
				return true;
			}
			catch (Exception e)
			{
				setLoadingInfo(getResources().getString(R.string.connecting));
			}
			String result = "";
			HttpResponse response = null;
			try
			{
				URI uri = new URI(URL_LINES);

				DefaultHttpClient client = new DefaultHttpClient();

				setLoadingInfo(getResources().getString(
						R.string.getting_lines_data));

				HttpUriRequest request = new HttpGet(uri);

				response = client.execute(request);
				HttpEntity entity = response.getEntity();

				setLoadingInfo(getResources().getString(
						R.string.getting_successful));
				result = EntityUtils.toString(entity);
			}
			catch (Exception e)
			{
				return false;
			}
			setLoadingInfo(getResources()
					.getString(R.string.parsing_lines_data));
			// Log.e("a", result.substring(213, 217)+result.substring(213,
			// 217).length());
			result = AppData.convertCro(result);
			// Log.e("a", result.substring(213, 217)+result.substring(213,
			// 217).length());
			lines = new Lines(result);
			try
			{
				setLoadingInfo(getResources().getString(
						R.string.saving_lines_data));
				FileOutputStream fos = getApplicationContext().openFileOutput(
						LINES_DATA_FILE_NAME, Context.MODE_PRIVATE);
				ObjectOutputStream os = new ObjectOutputStream(fos);

				os.writeObject(lines);
				setLoadingInfo(getResources().getString(R.string.data_saved));
				os.close();
			}
			catch (Exception e)
			{
				setLoadingInfo(getResources().getString(
						R.string.lines_saving_error));
			}
			return true;
		}

		private void loadSettings()
		{
			setLoadingInfo(getResources().getString(R.string.loading_settings));
			try
			{
				FileInputStream fis = getApplicationContext().openFileInput(
						SETTINGS_DATA_FILE_NAME);
				ObjectInputStream os = new ObjectInputStream(fis);
				settingsData = (SettingsData) os.readObject();
				os.close();
			}
			catch (Exception e)
			{
				settingsData = new SettingsData();
			}
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result)
			{
				callNextActivity();
			}
			else
			{
				finishActivity();
			}
		}
	}

	private static Handler mainHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			loadingMessage.setText((String) msg.obj);
		}
	};

}
