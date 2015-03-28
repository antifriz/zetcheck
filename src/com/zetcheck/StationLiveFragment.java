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
import java.util.List;

import com.zetcheck.R;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class StationLiveFragment extends Fragment
{
	public Integer ID;
	public boolean isDataSet = false;
	public static List<List<Row>> data;

	private ProgressBar progressBar;
	private ListView directionDataListView;
	private TextView isEmptyteTextView;
	public StationLiveAdapter stationLiveAdapter;

	public StationLiveFragment()
	{
		super();
		setRetainInstance(true);
	}

	final Runnable reloadDisplay = new Runnable()
	{
		@Override
		public void run()
		{
			try
			{
				if (stationLiveAdapter == null)
				{
					isDataSet = true;
					stationLiveAdapter = new StationLiveAdapter(getActivity(),
							R.layout.live_station_list_row, new ArrayList<Row>(
									data.get(ID)));
					directionDataListView.setAdapter(stationLiveAdapter);
				}
				else
				{
					stationLiveAdapter.refill(data.get(ID));
				}
				progressBar.setVisibility(View.GONE);
				if (data.get(ID).isEmpty())
					isEmptyteTextView.setVisibility(View.VISIBLE);
				else
					isEmptyteTextView.setVisibility(View.GONE);
			}
			catch (Exception e)
			{}
		}
	};

	public final Handler stationActivityHandler = new Handler();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View myView = inflater.inflate(R.layout.fragment_sliding_station,
				container, false);

		progressBar = (ProgressBar) myView
				.findViewById(R.id.station_progress_bar);
		directionDataListView = (ListView) myView
				.findViewById(R.id.station_list_view);
		isEmptyteTextView = (TextView) myView
				.findViewById(R.id.station_list_empty);

		if (isDataSet)
		{
			if (data.get(ID).isEmpty())
				isEmptyteTextView.setVisibility(View.VISIBLE);
			else
				isEmptyteTextView.setVisibility(View.GONE);
			stationLiveAdapter = new StationLiveAdapter(getActivity(),
					R.layout.live_station_list_row, new ArrayList<Row>(
							data.get(ID)));
			directionDataListView.setAdapter(stationLiveAdapter);
			progressBar.setVisibility(View.GONE);
		}

		// directionDataListView.setOnItemClickListener(new
		// OnItemClickListener()
		// {
		// @Override
		// public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
		// long arg3)
		// {
		// //Log.e("sa","s"+arg1.getId());
		// callLine(arg1.getId());
		// }
		// });
		return myView;
	}

	@Override
	public void onDestroyView()
	{
		if (stationLiveAdapter != null)
			isDataSet = true;
		super.onDestroyView();
	}
	//
	// @Override
	// public void onDetach()
	// {
	// Log.e("ondetach", name);
	// super.onDetach();
	// }
	//
	// @Override
	// public void onPause()
	// {
	// Log.e("onpause", name);
	// super.onPause();
	// }
	//
	// @Override
	// public void onSaveInstanceState(Bundle outState)
	// {
	// Log.e("onsave", name);
	// super.onSaveInstanceState(outState);
	// }
	//
	// @Override
	// public void onStart()
	// {
	// Log.e("onstart", name);
	// super.onStart();
	// }
	//
	// @Override
	// public void onResume()
	// {
	// Log.e("onresume", name);
	// super.onResume();
	// }
	//
	// @Override
	// public void onStop()
	// {
	// Log.e("onstop", name);
	// super.onStop();
	// }
	//
	// @Override
	// public void onDestroy()
	// {
	// Log.e("ondestory", name);
	// super.onDestroy();
	// }

}
