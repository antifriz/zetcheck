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

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.net.Uri;

public class FavoritesFragment extends Fragment
{
	public ViewPager mViewPager;
	private List<Station> favorites = new ArrayList<Station>();
	protected StationsListAdapter adapter;
	protected ListView listView;
	private TextView textView;
	private LinearLayout linearLayout;

	public FavoritesFragment()
	{
		super();
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container, Bundle savedInstanceState)
	{
		View myView = inflater.inflate(R.layout.fragment_favorites, container,
				false);

		myView.findViewById(R.id.menu_favorites_button_search)
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						MenuActivity.mViewPager.setCurrentItem(2);
					}
				});
		myView.findViewById(R.id.menu_favorites_button_ZETweb)
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Uri uriUrl = Uri.parse(getResources().getString(
								R.string.link_zet_news));
						Intent launchBrowser = new Intent(Intent.ACTION_VIEW,
								uriUrl);
						startActivity(launchBrowser);
					}
				});
		myView.findViewById(R.id.menu_favorites_app_name)
		.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				Uri uriUrl = Uri.parse(getResources().getString(
						R.string.link_fb_page));
				Intent launchBrowser = new Intent(Intent.ACTION_VIEW,
						uriUrl);
				startActivity(launchBrowser);
			}
		});
		myView.findViewById(R.id.menu_favorites_button_lines)
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						MenuActivity.mViewPager.setCurrentItem(0);
					}
				});
		myView.findViewById(R.id.menu_favorites_button_favorites)
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Intent intent = new Intent(getActivity(),
								FavoritesActivity.class);
						startActivity(intent);
						getActivity().overridePendingTransition(
								R.anim.fall_up_in, android.R.anim.fade_out);
					}
				});
		myView.findViewById(R.id.menu_favorites_button_mylocation)
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Intent intent = new Intent(getActivity(),
								LocationActivity.class);
						startActivity(intent);
						getActivity().overridePendingTransition(
								R.anim.fall_down_in, R.anim.fall_down_out);
					}
				});
		myView.findViewById(R.id.menu_favorites_button_preferences)
				.setOnClickListener(new OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
						Intent intent = new Intent(getActivity(),
								SettingsActivity.class);
						startActivity(intent);
					}
				});

		myView.findViewById(R.id.menu_favorites_button_search)
				.getBackground()
				.setColorFilter(getResources().getColor(R.color.menu_stations),
						PorterDuff.Mode.MULTIPLY);
		myView.findViewById(R.id.menu_favorites_button_preferences)
				.getBackground()
				.setColorFilter(
						getResources().getColor(R.color.menu_preferences),
						PorterDuff.Mode.MULTIPLY);
		myView.findViewById(R.id.menu_favorites_button_mylocation)
				.getBackground()
				.setColorFilter(
						getResources().getColor(R.color.menu_mylocation),
						PorterDuff.Mode.MULTIPLY);
		myView.findViewById(R.id.menu_favorites_button_favorites)
				.getBackground()
				.setColorFilter(
						getResources().getColor(R.color.menu_favorites),
						PorterDuff.Mode.MULTIPLY);
		myView.findViewById(R.id.menu_favorites_button_ZETweb)
				.getBackground()
				.setColorFilter(getResources().getColor(R.color.menu_ZETweb),
						PorterDuff.Mode.MULTIPLY);
		myView.findViewById(R.id.menu_favorites_button_lines)
				.getBackground()
				.setColorFilter(getResources().getColor(R.color.menu_lines),
						PorterDuff.Mode.MULTIPLY);

		listView = (ListView) myView.findViewById(R.id.menu_favorites_list);
		textView = (TextView) myView
				.findViewById(R.id.menu_favorites_list_empty);
		linearLayout = (LinearLayout) myView
				.findViewById(R.id.menu_favorites_frame);
//		if (AppData.setupData != null && AppData.setupData.hasFavorites)
//		{
			adapter = new StationsListAdapter(getActivity(),
					R.layout.station_row_layout, fillList());
			listView.setAdapter(adapter);
			if (favorites.isEmpty())
				textView.setVisibility(View.VISIBLE);
			else
				textView.setVisibility(View.GONE);
			linearLayout.setVisibility(View.VISIBLE);
//		}
//		else
//			linearLayout.setVisibility(View.GONE);

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

	@Override
	public void onResume()
	{
		// if (linearLayout != null)
		// {
//		if (AppData.setupData == null || !AppData.setupData.hasFavorites)
//			linearLayout.setVisibility(View.GONE);
//		else
//		{
			linearLayout.setVisibility(View.VISIBLE);
			updateList();
//		}
		// }
		super.onResume();
	}

	private List<Station> fillList()
	{
		favorites.clear();
		for (Integer ID : AppData.settingsData.favoriteIds)
		{
			favorites.add(AppData.stations.Map.get(AppData.stations.Keys
					.get(AppData.stations.idToSearch.indexOf(ID))));
		}
		return favorites;
	}

	private void updateList()
	{
		if (!isVisible())
			return;
		if (listView == null || textView == null)
			return;
		if (favorites == null || adapter == null)
		{
			favorites = new ArrayList<Station>();
			adapter = new StationsListAdapter(getActivity(),
					R.layout.station_row_layout, fillList());
			listView.setAdapter(adapter);
		}
		else
			adapter.refill(new ArrayList<Station>(fillList()));
		if (favorites.isEmpty())
			textView.setVisibility(View.VISIBLE);
		else
			textView.setVisibility(View.GONE);
	}

}
