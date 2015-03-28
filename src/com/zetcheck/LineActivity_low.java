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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.Lock;
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
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LineActivity_low extends Activity
{
	private static final String URL_STATUS = "http://zet.icytexx.com/zetCheckServer.php?ilko=true&status=1";
	private final long REFRESH_RATE = 5000;
	private final int MAX_ATTEMPTS_NUM = 2;

	private int ID;
	private Line line;
	private URI uri;
	private Timer refreshTimer;
	private Timer bandwidthSaver;
	private Lock lock = new ReentrantLock();

	private UpdateData updateData;
	private Vehicles vehicles;
	private VehiclesListAdapter adapter;
	ListView listView;
	ProgressBar progressBar;
	TextView textView;
	boolean justStarted = true;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_line_2);

		setGlobalDisplay();
		setUpdateConfig();
	}

	private void setGlobalDisplay()
	{
		ID = getIntent().getExtras().getInt(AppData.EXTRA_MESSAGE_LINE);
		line = AppData.lines.Map.get(String.valueOf(ID));

		String[] ss = line.Route.split("\n");
		String route = ss[0];
		if (ss.length > 1)
			route += "\n" + ss[ss.length - 1];

		findViewById(R.id.line_thumbnail).setBackgroundColor(
				AppData.getStationTypeColor((ID < 100) ? true : false));
		((TextView) findViewById(R.id.line_number)).setText(line.Number);
		((TextView) findViewById(R.id.line_route)).setText(route);
		findViewById(R.id.line_decor).setBackgroundColor(
				AppData.getStationTypeColor((ID < 100) ? false : true));
		listView = (ListView) findViewById(R.id.line_vehicles);
		progressBar = (ProgressBar) findViewById(R.id.line_progress_bar);
		textView = (TextView) findViewById(R.id.line_list_empty);
	}

	private void setUpdateConfig()
	{
		updateData = new UpdateData();
		updateData.isRunning = true;
		try
		{
			uri = new URI(URL_STATUS);
		}
		catch (URISyntaxException e1)
		{
			// TODO:check ID !!!
			return;
		}
		updateData.execute((Void[]) null);
	}

	private class UpdateData extends AsyncTask<Void, Void, Void>
	{
		public boolean isRunning = true;
		public HttpUriRequest request = null;
		private DefaultHttpClient client1 = new DefaultHttpClient();
		private String resultString;
		private int attemptsNum = 0;

		@Override
		protected Void doInBackground(Void... params)
		{
			long startTime = System.currentTimeMillis();
			while (isRunning)
			{
				getData();

				if (resultString == null)
				{
					if (isRunning)
					{
						if (attemptsNum >= MAX_ATTEMPTS_NUM)
							break;
						attemptsNum++;
						continue;
					}
				}

				resultString = AppData.convertCro(resultString);

				lock.lock();
				try
				{
					vehicles = new Vehicles(resultString, line.Number);
				}
				finally
				{
					lock.unlock();
				}
				runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						Refresh();
					}
				});

				long waitTime = System.currentTimeMillis() - startTime;
				if (waitTime < REFRESH_RATE)
					try
					{
						Thread.sleep(REFRESH_RATE - waitTime);
					}
					catch (Exception e)
					{
						break;
					}
			}
			return null;
		}

		protected boolean getData()
		{
			resultString = null;
			try
			{
				request = new HttpGet(uri);

				HttpResponse response = client1.execute(request);
				HttpEntity entity = response.getEntity();

				resultString = EntityUtils.toString(entity);
				return true;
			}
			catch (Exception e)
			{
				return false;
			}
		}

		@Override
		protected void onPostExecute(Void result)
		{
			finishActivity(getResources().getString(R.string.check_connection));
		}
	}

	private void Refresh()
	{
		if (adapter == null)
		{
			adapter = new VehiclesListAdapter(this,
					R.layout.vehicles_row_layout, new ArrayList<Vehicle>(
							vehicles.list));
			listView.setAdapter(adapter);
		}
		else
		{
			adapter.refill(vehicles.list);
		}
		progressBar.setVisibility(View.GONE);
		if (vehicles.list.isEmpty())
			textView.setVisibility(View.VISIBLE);
		else
			textView.setVisibility(View.GONE);
	}

	private void finishActivity(String dialogMessage)
	{
		new AlertDialog.Builder(this).setTitle("Fatal error")
				.setMessage(dialogMessage)
				.setNeutralButton("OK", new DialogInterface.OnClickListener()
				{

					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						try
						{
							refreshTimer.cancel();
						}
						finally
						{
							finish();
						}
					}
				}).show();
	}

	//
	@Override
	public void onPause()
	{

		bandwidthSaver = new Timer();
		bandwidthSaver.schedule(new TimerTask()
		{

			@Override
			public void run()
			{
				finish();
			}
		}, 1000 * 15);

		// Log.e("onpause", "a");
		super.onPause();
		// isPaused=true;
	}

	@Override
	public void onResume()
	{
		try
		{
			bandwidthSaver.cancel();
		}
		catch (Exception e)
		{

		}
		super.onResume();
	}

	@Override
	public void onDestroy()
	{
		try
		{
			updateData.request.abort();

		}
		finally
		{}

		try
		{
			updateData.isRunning = false;
		}
		finally
		{}

		try
		{
			updateData.cancel(true);
		}
		finally
		{}
		super.onDestroy();
	}
}
