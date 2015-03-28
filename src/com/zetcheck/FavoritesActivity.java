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

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FavoritesActivity extends Activity
{
//	private boolean isEditing = false;
//	private ImageView imageView;

	private List<Station> favorites = new ArrayList<Station>();
	private ListView listView;
	private TextView textView;

	public FavoritesActivity()
	{
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favorites);
//		imageView = (ImageView) findViewById(R.id.favorites_edit);
		
		listView = (ListView) findViewById(R.id.favorites_list);
		textView = (TextView) findViewById(R.id.favorites_list_empty);

		favorites = getStationsFromIds(AppData.settingsData.favoriteIds);
		
		putFavoritesNotEditing();
		
//		imageView.setOnClickListener(new OnClickListener()
//		{
//			@Override
//			public void onClick(View v)
//			{
//				if (isEditing)
//				{
//					turnOffEditing();
//				}
//				else
//				{
//					turnOnEditing();
//				}
//			}
//		});
//
//	}

//	private void turnOffEditing()
//	{
//		isEditing=false;
//		putFavoritesNotEditing();
//	}
//
//	private void turnOnEditing()
//	{
//		isEditing=true;
//		putFavoritesEditing();
	}

	private void putFavoritesNotEditing()
	{
		StationsListAdapter adapter = new StationsListAdapter(this,
				R.layout.station_row_layout, new ArrayList<Station>(favorites));
		listView.setAdapter(adapter);
		if (favorites.size() <= 0)
			textView.setVisibility(View.VISIBLE);
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				callStation(arg1.getId());
			}
		});
	}
	
//	private void putFavoritesNotEditing()
//	{
//		StationsListEditAdapter adapter = new StationsListEditAdapter(this,
//				R.layout.station_row_layout_edit, new ArrayList<Station>(favorites));
//		listView.setAdapter(adapter);
//		if (favorites.size() <= 0)
//			textView.setVisibility(View.VISIBLE);
//	}

	private List<Station> getStationsFromIds(List<Integer> Ids)
	{
		List<Station> list = new ArrayList<Station>();
		for (Integer integer : Ids)
		{
			list.add(AppData.stations.Map.get(AppData.stations.Keys
					.get(AppData.stations.idToSearch.indexOf(integer))));
		}
		return list;
	}

	@Override
	public void onBackPressed()
	{
//		if (!isEditing)
//		{
			super.onBackPressed();
			overridePendingTransition(android.R.anim.fade_in,
					R.anim.fall_down_out);

			finish();
//		}
//		else
//		{
//			// TODO: ugasi editiranje
//		}
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
			intent = new Intent(this, SlidingStationActivity.class);
		}
		else
		{
			SlidingStationActivity_low.fragments = new StationLiveFragment[6];
			StationLiveFragment.data = new ArrayList<List<Row>>(6);
			for (int i = 0; i < 6; i++)
				StationLiveFragment.data.add(new ArrayList<Row>());
			intent = new Intent(this, SlidingStationActivity_low.class);
		}
		intent.putExtra(AppData.EXTRA_MESSAGE_STATION, id);

		startActivity(intent);
	}

}
