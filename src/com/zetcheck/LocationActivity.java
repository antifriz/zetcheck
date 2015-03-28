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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.zetcheck.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class LocationActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener
{

	private static final float MEANING_OF_NEAR = 750;

	private boolean locationProcessed = true;

	private ProgressBar progressBar;
	private ListView listView;
	private TextView isEmptyteTextView;
	public StationsListAdapter adapter;

	public static List<Station> data;

	private LocationManager lm;
	private Activity context = this;

	private myLocationListener ll;

	private AlertDialog alert;

	private LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_location);

		listView = (ListView) findViewById(R.id.location_list_view);
		isEmptyteTextView = (TextView) findViewById(R.id.location_list_empty);
		progressBar = (ProgressBar) findViewById(R.id.location_progress_bar);

		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				callStation(arg1.getId());
			}
		});

		findViewById(R.id.menu_location_services).setOnClickListener(
				new View.OnClickListener()
				{

					@Override
					public void onClick(View arg0)
					{
						Intent callGPSSettingIntent = new Intent(
								android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(callGPSSettingIntent);
					}
				});
		mLocationClient = new LocationClient(this, this, this);
	}

	@Override
	protected void onResume()
	{
		lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		ll = new myLocationListener();
		lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, ll);
		lm.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, ll);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, ll);
		if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
				&& !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
			alert = showGPSDisabledAlertToUser(alert);

		super.onResume();
	}

	private AlertDialog showGPSDisabledAlertToUser(AlertDialog alert)
	{
		if (alert != null && alert.isShowing())
			return alert;
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder
				.setMessage(
						getResources().getString(
								R.string.locations_disabled_info))
				.setCancelable(false)
				.setPositiveButton(
						getResources().getString(R.string.locations_enable),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								Intent callGPSSettingIntent = new Intent(
										android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
								startActivity(callGPSSettingIntent);
							}
						})
				.setNegativeButton(getResources().getString(R.string.cancel),
						new DialogInterface.OnClickListener()
						{
							public void onClick(DialogInterface dialog, int id)
							{
								onBackPressed();
							}
						});
		alert = alertDialogBuilder.create();
		alert.show();
		return alert;
	}

	private class myLocationListener implements LocationListener
	{
		@Override
		public void onLocationChanged(Location location)
		{
			if (location != null && locationProcessed)
			{
				locationProcessed = false;
				new NearestStations().execute(location);
			}
		}

		@Override
		public void onProviderDisabled(String provider)
		{
			try
			{
				if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
						&& !lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
					alert = showGPSDisabledAlertToUser(alert);
				else if (alert != null || alert.isShowing())
					alert.dismiss();
			}
			catch (Exception e)
			{

			}
		}

		@Override
		public void onProviderEnabled(String provider)
		{
			if (alert != null || alert.isShowing())
				alert.dismiss();
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras)
		{}

	}

	@Override
	protected void onStart()
	{
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}

	/*
	 * Called when the Activity is no longer visible.
	 */
	@Override
	protected void onStop()
	{
		// Disconnecting the client invalidates it.
		mLocationClient.disconnect();
		super.onStop();
	}

	private class NearestStations extends
			AsyncTask<Location, Void, List<Station>>
	{
		private Location location;

		@Override
		protected List<Station> doInBackground(Location... params)
		{

			Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
			// Log.e("poceo", "1");
			this.location = new Location(params[0]);
			List<Station> result = new ArrayList<Station>();
			HashMap<String, Station> map = AppData.stations.Map;
			List<String> keys = AppData.stations.Keys;
			List<Double> latList = AppData.stations.lattitudeDoubles;
			List<Double> longList = AppData.stations.longitudeDoubles;

			for (int i = 0; i < longList.size(); i++)
			{
				double latDouble = latList.get(i);
				double longDouble = longList.get(i);
				if (latDouble == 0 || longDouble == 0)
					continue;

				Location stationLocation = new Location("Station");

				stationLocation.setLatitude(latDouble);

				stationLocation.setLongitude(longDouble);

				float distance = location.distanceTo(stationLocation);

				if (distance > MEANING_OF_NEAR)
					continue;

				// Log.e(""+keys.get(i), ""+distance);
				Station station = new Station(map.get(keys.get(i)));

				if (distance < 500)
					station.Distance = ((int) distance) / 10 * 10;
				else if (distance < 5000)
					station.Distance = ((int) distance) / 100 * 100;
				else
					station.Distance = ((int) distance) / 1000 * 1000;

				if (distance < MEANING_OF_NEAR)
					result.add(station);
			}

			Collections.sort(result);

			// Log.e("zavrsio", "1");

			// if(intent!=null&&isSearchActivityCalled)
			// intent = new Intent(context, StationSelectorActivity.class);

			data = new ArrayList<Station>(result);

			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					if (adapter == null)
					{
						adapter = new StationsListAdapter(context,
								R.layout.station_row_layout,
								new ArrayList<Station>(data));
						listView.setAdapter(adapter);
						progressBar.setVisibility(View.GONE);
					}
					else
						adapter.refill(new ArrayList<Station>(data));
					if (data.size() <= 0)
						isEmptyteTextView.setVisibility(View.VISIBLE);
					else
						isEmptyteTextView.setVisibility(View.GONE);
				}
			});
			try
			{
				Thread.sleep(5000);
			}
			catch (Exception e)
			{

			}
			locationProcessed = true;
			return result;
		}

		@Override
		protected void onPostExecute(List<Station> result)
		{

		}

	}

	protected void callStation(int id)
	{
		// Log.e(""+id,""+id);
		SlidingStationActivity.fragments = new StationLiveFragment[6];
		StationLiveFragment.data = new ArrayList<List<Row>>(6);
		for (int i = 0; i < 6; i++)
			StationLiveFragment.data.add(new ArrayList<Row>());
		Intent intent = new Intent(this, SlidingStationActivity.class);
		intent.putExtra(AppData.EXTRA_MESSAGE_STATION, id);

		startActivity(intent);
	}

	public static String locationStringFromLocation(final Location location)
	{
		return Location
				.convert(location.getLatitude(), Location.FORMAT_DEGREES)
				+ " "
				+ Location.convert(location.getLongitude(),
						Location.FORMAT_DEGREES);
	}

	@Override
	protected void onPause()
	{
		try
		{
			lm.removeUpdates(ll);
			lm = null;
		}
		catch (Exception e)
		{}
		super.onPause();
	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		overridePendingTransition(R.anim.fall_up_in, R.anim.fall_up_out);
		finish();
	}

	@Override
	public void onConnectionFailed(ConnectionResult result)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onConnected(Bundle connectionHint)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void onDisconnected()
	{
		// TODO Auto-generated method stub

	}

}
