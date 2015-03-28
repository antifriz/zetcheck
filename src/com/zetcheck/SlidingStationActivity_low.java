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

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SlidingStationActivity_low extends FragmentActivity
{
	public static StationLiveFragment[] fragments;

	private static final String URL_DEPARTURES = "http://zet.icytexx.com/zetCheckServer.php?ilko=true&departures=";
	private static final long REFRESH_RATE = 15000;
	private static final int OFFSCREEN_PAGE_LIMIT = 1;
	private static final int MAX_ATTEMPTS_NUM = 2;

	private int ID;
	private Station station;
	private URI uri;
	private Departures departures;
	private UpdateData updateData;
	private int fragmentsCnt;
	private int[] fragmentGates = new int[6];
	// private boolean isPaused = false;
	private SectionsPagerAdapter mSectionsPagerAdapter;
	private ViewPager mViewPager;
	private Timer refreshTimer;
	private Timer bandwidthSaver;
	private boolean isDataReady;
	private Lock lock = new ReentrantLock();
	private boolean isCurrentItemSet = false;
	private TextView refreshTextView;
	private boolean newData;
	private ImageView favoritesStar;
	private boolean isFavorite;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// Log.e("oncreate", "a");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sliding_station);

		setGlobalDisplay();
		setViewPagerConfig(savedInstanceState);
		setUpdateConfig();

	}

	private void setGlobalDisplay()
	{
		ID = getIntent().getExtras().getInt(AppData.EXTRA_MESSAGE_STATION);
		station = AppData.stations.Map.get(AppData.stations.Keys
				.get(AppData.stations.idToSearch.indexOf(ID)));

		findViewById(R.id.station_thumbnail).setBackgroundColor(
				AppData.getStationTypeColor(station.IsTram));
		((TextView) findViewById(R.id.station_type)).setText(AppData
				.getStationTypeName(station.IsTram));
		((TextView) findViewById(R.id.station_name)).setText(station.Name);
		findViewById(R.id.station_pager_title_strip).setBackgroundColor(
				AppData.getStationTypeColor(!station.IsTram));
		refreshTextView = ((TextView) findViewById(R.id.station_refresh_info));
		favoritesStar = (ImageView) findViewById(R.id.station_star);

		if (AppData.settingsData.favoriteIds.contains(ID))
		{
			isFavorite = true;
			favoritesStar.setBackgroundResource(android.R.drawable.star_big_on);
		}
		else
		{
			isFavorite = false;
			favoritesStar
					.setBackgroundResource(android.R.drawable.star_big_off);
		}

		favoritesStar.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				try
				{
					if (isFavorite)
					{
						favoritesStar
								.setBackgroundResource(android.R.drawable.star_big_off);
						AppData.settingsData.favoriteIds.remove((Object) ID);
					}
					else
					{
						favoritesStar
								.setBackgroundResource(android.R.drawable.star_big_on);
						AppData.settingsData.favoriteIds.add(ID);
					}
					isFavorite = !isFavorite;
					FileOutputStream fos = getApplicationContext()
							.openFileOutput(
									MainActivity.SETTINGS_DATA_FILE_NAME,
									Context.MODE_PRIVATE);
					ObjectOutputStream os = new ObjectOutputStream(fos);
					os.writeObject(AppData.settingsData);
					os.close();
				}
				catch (Exception e)
				{

				}

			}
		});
	}

	private void setViewPagerConfig(Bundle savedInstanceState)
	{
		fragmentsCnt = 0;
		for (int i = station.Gates, j = 0; j < 6; i = i >> 1, j++)
		{
			if (i % 2 == 0)
				continue;
			fragmentGates[fragmentsCnt++] = j;
		}

		if (savedInstanceState == null)
		{
			for (int i = 0; i < fragmentsCnt; i++)
			{
				fragments[i] = new StationLiveFragment();
				fragments[i].ID = i;
			}
		}

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	private void setUpdateConfig()
	{
		departures = new Departures();
		updateData = new UpdateData();
		updateData.isRunning = true;
		try
		{
			uri = new URI(URL_DEPARTURES + String.valueOf(ID));
		}
		catch (URISyntaxException e1)
		{
			// TODO:check ID !!!
			return;
		}
		updateData.execute((Void[]) null);
		isDataReady = false;
		refreshTimer = new Timer();
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
			while (isRunning)
			{
				long startTime = System.currentTimeMillis();

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
					departures.getDepartures(resultString);

					for (int i = 0; i < fragmentsCnt; i++)
					{
						StationLiveFragment.data.set(i, new ArrayList<Row>(
								departures.gates.get(fragmentGates[i])));
					}
				}
				finally
				{
					lock.unlock();
				}
				newData = true;
				if (!isDataReady)
				{
					isDataReady = true;
					refreshTimer.scheduleAtFixedRate(new TimerUpdateTask(),
							1000, 1000);
				}

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

	private class SectionsPagerAdapter extends FragmentPagerAdapter
	{
		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			if (position >= 0)
			{
				return fragments[position];
			}
			return null;
		}

		@Override
		public int getCount()
		{
			return fragmentsCnt;
		}

		@Override
		public CharSequence getPageTitle(int position)
		{
			if(fragmentGates[position]==0)
				return getResources().getString(R.string.title_section1);
			if(fragmentGates[position]==1)
				return getResources().getString(R.string.title_section2);
			if(fragmentGates[position]==2)
				return getResources().getString(R.string.title_section3);
			if(fragmentGates[position]==3)
				return getResources().getString(R.string.title_section4);
			if(fragmentGates[position]==4)
				return getResources().getString(R.string.title_section5);
			if(fragmentGates[position]==5)
				return getResources().getString(R.string.title_section6);
			return "???";
		}

	}

	private class TimerUpdateTask extends TimerTask
	{
		@Override
		public void run()
		{
			lock.lock();
			try
			{
				if (isDataReady)
				{
					// Log.e("krecem", "krecem");
					for (int i = 0; i < fragmentsCnt; i++)
					{
						List<Row> list = StationLiveFragment.data.get(i);
						for (Row row : list)
						{
							if (row.departureEpoch > 0)
								row.departureEpoch--;
						}
						fragments[i].stationActivityHandler
								.post(fragments[i].reloadDisplay);
					}
					int currentItem = mViewPager.getCurrentItem();

					while (currentItem < fragmentsCnt
							&& (StationLiveFragment.data == null || StationLiveFragment.data
									.get(currentItem).isEmpty()))
						currentItem++;
					if (!isCurrentItemSet && currentItem < fragmentsCnt
							&& currentItem != mViewPager.getCurrentItem())
					{
						isCurrentItemSet = true;
						final int ci = currentItem;
						runOnUiThread(new Runnable()
						{

							@Override
							public void run()
							{
								mViewPager.setCurrentItem(ci);
							}
						});
					}
				}
				if (newData)
				{
					String temp;
					if (refreshTextView.getText() == "U�itao")
					{
						newData = false;

						temp = departures.timeCreatedString;
					}
					else
					{
						temp = "U�itao";
					}
					final String temp2 = temp;
					runOnUiThread(new Runnable()
					{

						@Override
						public void run()
						{
							refreshTextView.setText(temp2);
						}
					});
				}
			}
			finally
			{
				lock.unlock();
			}
		}
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

	//
	// @Override
	// public void onRestart()
	// {
	//
	// Log.e("onrestart", "a");
	// super.onRestart();
	// // setDefaultContext();
	// // startLoadingProcess();
	//
	// // isPaused=false;
	// // lock.lock();
	// // try
	// // {
	// // Log.e("asdf","ajede");
	// // waitToStart.signal();
	// // Log.e("asdf1","ajede");
	// // }
	// // catch (Exception e)
	// // {
	// // Log.e("zastoooobasti","");
	// // }
	// // finally
	// // {
	// // lock.unlock();
	// // }
	//
	// }
	//
	// @Override
	// public void onStart()
	// {
	// Log.e("onstart", "a");
	// super.onStart();
	// }
	//
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

	//
	// @Override
	// public void onStop()
	// {
	// Log.e("onstop", "a");
	//
	// super.onStop();
	// }
	//
	// @Override
	// public void onConfigurationChanged(Configuration newConfig)
	// {
	// // TODO Auto-generated method stub
	// Log.e("ONCONFIGCHANGED!", "sd");
	// super.onConfigurationChanged(newConfig);
	// }
	//
	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// Log.e("onsave", "a");
		super.onSaveInstanceState(outState);
		// for (int i = 0; i < fragmentsCnt; i++)
		// getSupportFragmentManager().putFragment(
		// outState,
		// StationLiveFragment.class.getName()
		// + AppData.DIRECTIONS[fragmentGates[i]],
		// mSectionsPagerAdapter.getItem(i));
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		try
		{
			//updateData.request.abort();

		}
		finally
		{}

		try
		{
			//Log.e("abort","abort1");
			//updateData.request.abort();
			//Log.e("abort","abort2");
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
			refreshTimer.cancel();
		}
		finally
		{}

		try
		{
			updateData.cancel(true);
		}
		finally
		{}

		// Log.e("ondestory", "a");
		StationLiveFragment.data.clear();
		if (isFinishing())
		{
			StationLiveFragment.data.clear();
			fragments = null;
		}
	}
}
