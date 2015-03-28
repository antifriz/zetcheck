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
import java.util.concurrent.locks.ReentrantLock;

import com.zetcheck.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

public class SearchStationsFragment extends Fragment
{
	private ReentrantLock reentrantLock = new ReentrantLock();
	private EditText editText;
	private ListView listView;
	private String querry = null;

	private StationsListAdapter adapter;
	private boolean isAdapterSet = false;
	public final Handler handler = new Handler();
	private ImageView imageView;

	public SearchStationsFragment()
	{
		super();
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View myView = inflater.inflate(R.layout.fragment_search_stations, null, false);
		isAdapterSet = false;

		editText = (EditText) myView.findViewById(R.id.menu_search_input);
		listView = (ListView) myView.findViewById(R.id.menu_search_results);
		imageView = (ImageView) myView.findViewById(R.id.menu_search_station_close);

		imageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				editText.setText("");
			}
		});
		editText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{}

			@Override
			public void afterTextChanged(Editable s)
			{
				if (s.length() < 1)
				{
					try
					{
						adapter.refill(null);
					}
					catch (Exception e)
					{}
					querry = null;
					return;
				}
				querry = s.toString();
				new SearchStationsAsyncTask().execute(new String[] { querry });
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				callStation(arg1.getId());
			}
		});
		return myView;
	}

	protected void callStation(int id)
	{
		Intent intent;
		if (!AppData.isFroyo)
		{
			SlidingStationActivity.fragments = new StationLiveFragment[6];
			StationLiveFragment.data = new ArrayList<List<Row>>(6);
			for (int i = 0; i < 6; i++)
				StationLiveFragment.data.add(new ArrayList<Row>());
			intent = new Intent(getActivity(), SlidingStationActivity.class);
		}
		else
		{
			SlidingStationActivity_low.fragments = new StationLiveFragment[6];
			StationLiveFragment.data = new ArrayList<List<Row>>(6);
			for (int i = 0; i < 6; i++)
				StationLiveFragment.data.add(new ArrayList<Row>());
			intent = new Intent(getActivity(), SlidingStationActivity_low.class);
		}
		intent.putExtra(AppData.EXTRA_MESSAGE_STATION, id);

		startActivity(intent);
	}

	protected void RefreshStationNamesList(List<String> list)
	{
		List<Station> data = new ArrayList<Station>();
		Station station;
		for (String string : list)
		{
			station = AppData.stations.Map.get(string);
			if (station == null)
			{
				//Log.e("station == null", "!!!"); // TODO: error handling
			}
			data.add(station);
		}
		if (!isAdapterSet)
		{
			adapter = new StationsListAdapter(getActivity(),
					R.layout.station_row_layout, data);
			listView.setAdapter(adapter);
			isAdapterSet = true;
		}
		else
		{
			adapter.refill(data);
		}
	}
	
	
	private class SearchStationsAsyncTask extends
			AsyncTask<String, Integer, List<String>>
	{
		String lastQuerry;

		@Override
		protected List<String> doInBackground(String... params)
		{
			lastQuerry = params[0];
			return AppData.getStationNamesContaining(lastQuerry);
		}

		@Override
		protected void onPostExecute(List<String> result)
		{
			reentrantLock.lock();
			if (lastQuerry == querry)
				RefreshStationNamesList(result);
			reentrantLock.unlock();
		}
	}

}
