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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.zetcheck.R;

public class MenuActivity_low extends FragmentActivity
{

	private static SearchLinesFragment searchLinesFragment;
	private static FavoritesFragment favoritesFragment;
	private static SearchStationsFragment searchStationsFragment;

	public static boolean STAY_LIVE = true;

	public final Handler handler = new Handler();
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	public static ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_menu_2);

		AppData.context = this;

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.menu_pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setOffscreenPageLimit(1);

		mViewPager.setCurrentItem(1);

		searchLinesFragment = new SearchLinesFragment();
		favoritesFragment = new FavoritesFragment();
		searchStationsFragment = new SearchStationsFragment();

	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter
	{

		public SectionsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			switch (position)
			{
				case 0:
					return searchLinesFragment;
				case 1:
					return favoritesFragment;
				case 2:
					return searchStationsFragment;
				default:
					return null;
			}
		}

		@Override
		public int getCount()
		{
			return 3;
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus)
		{
			if (!STAY_LIVE)
				finish();
			// if(AppData.setupData!=null&&AppData.setupData.hasFavorites)
			// favoritesFragment.handler.post(favoritesFragment.updateFavorites);
			// toggleFullscreen(false);
		}
	}

	@Override
	public void onBackPressed()
	{
		if (mViewPager.getCurrentItem() != 1)
		{
			mViewPager.setCurrentItem(1);
			return;
		}
		Intent intent1 = new Intent(Intent.ACTION_MAIN);
		intent1.addCategory(Intent.CATEGORY_HOME);
		intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent1);
	}

	@Override
	public void onResume()
	{
		super.onResume();
	}

	@Override
	public void onPause()
	{
		super.onPause();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}
}
