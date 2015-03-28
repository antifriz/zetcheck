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

import com.zetcheck.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;

public class SettingsActivity extends Activity
{
	public final Handler handler = new Handler();
	private Activity context = this;

	// private CheckBox toggle;
	// private RadioGroup groupLanguage;
	// private RadioButton groupLanguageCro;
	// private RadioButton groupLanguageEn;

	public SettingsActivity()
	{
		super();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		findViewById(R.id.menu_settings_info).setOnClickListener(
				new View.OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						new AlertDialog.Builder(context).setMessage(
								getResources().getString(R.string.info)).show();
					}
				});
		findViewById(R.id.menu_settings_help).setOnClickListener(
				new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						new AlertDialog.Builder(context).setMessage(
								getResources().getString(R.string.help)).show();
					}
				});
		findViewById(R.id.menu_settings_resetall).setOnClickListener(
				new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						new AlertDialog.Builder(context)
								.setMessage(
										getResources().getString(
												R.string.master_reset_warning))
								.setPositiveButton(
										getResources().getString(R.string.yes),
										new DialogInterface.OnClickListener()
										{
											@Override
											public void onClick(
													DialogInterface dialog,
													int which)
											{
												getApplicationContext()
														.deleteFile(
																MainActivity.SETTINGS_DATA_FILE_NAME);
												getApplicationContext()
														.deleteFile(
																MainActivity.STATIONS_DATA_FILE_NAME);
												getApplicationContext()
														.deleteFile(
																MainActivity.LINES_DATA_FILE_NAME);
												// getApplicationContext()
												// .deleteFile(
												// MainActivity.SETUP_DATA_FILE_NAME);
												MenuActivity.STAY_LIVE = false;
												finish();
											}
										})
								.setNegativeButton(
										getResources().getString(R.string.no),
										null).show();
					}
				});
		// toggle = (CheckBox) findViewById(R.id.menu_setting_favorites_on_off);
		// if (AppData.setupData == null)
		// AppData.setupData = new SetupData();
		// if (AppData.setupData.hasFavorites != null
		// && AppData.setupData.hasFavorites)
		// toggle.setChecked(true);
		// else
		// toggle.setChecked(false);
		//
		// toggle.setOnCheckedChangeListener(new OnCheckedChangeListener()
		// {
		//
		// @Override
		// public void onCheckedChanged(CompoundButton buttonView,
		// boolean isChecked)
		// {
		// if (isChecked)
		// {
		// AppData.setupData.hasFavorites = true;
		// }
		// else
		// {
		// AppData.setupData.hasFavorites = false;
		// }
		//
		// try
		// {
		// FileOutputStream fos = getApplicationContext()
		// .openFileOutput(MainActivity.SETUP_DATA_FILE_NAME,
		// Context.MODE_PRIVATE);
		//
		// ObjectOutputStream os = new ObjectOutputStream(fos);
		// os.writeObject(AppData.setupData);
		// os.close();
		// }
		// catch (Exception e)
		// {
		//
		// }
		// return;
		// }
		// });
		/*
		 * groupLanguage = (RadioGroup)
		 * findViewById(R.id.menu_setting_language); final int id2 =
		 * ((RadioButton) findViewById(R.id.menu_setting_language_en)) .getId();
		 * //
		 * ((RadioButton)findViewById(R.id.menu_setting_language_en)).setId(2);
		 * 
		 * switch (AppData.setupData.language) { case SetupData.croatian:
		 * ((RadioButton) findViewById(R.id.menu_setting_language_cro))
		 * .setChecked(true); break;
		 * 
		 * case SetupData.english: ((RadioButton)
		 * findViewById(R.id.menu_setting_language_en)) .setChecked(true);
		 * break;
		 * 
		 * default: break; } groupLanguage .setOnCheckedChangeListener(new
		 * RadioGroup.OnCheckedChangeListener() {
		 * 
		 * @Override public void onCheckedChanged(RadioGroup group, int
		 * checkedId) { Log.e("checked", "id" + checkedId); if (checkedId ==
		 * id2) { //setLocale("en_GB"); AppData.setupData.language =
		 * SetupData.english; } else { //setLocale("hr");
		 * AppData.setupData.language = SetupData.croatian; }
		 * 
		 * try { FileOutputStream fos = getApplicationContext() .openFileOutput(
		 * MainActivity.SETUP_DATA_FILE_NAME, Context.MODE_PRIVATE);
		 * 
		 * ObjectOutputStream os = new ObjectOutputStream(fos);
		 * os.writeObject(AppData.setupData); os.close(); } catch (Exception e)
		 * {
		 * 
		 * }
		 * 
		 * return; } });
		 */

	}

	@Override
	public void onBackPressed()
	{
		super.onBackPressed();
		finish();
	}
}
