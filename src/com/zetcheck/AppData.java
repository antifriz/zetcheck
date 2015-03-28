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

 */
package com.zetcheck;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class AppData
{
	public static boolean isFroyo;;
	
	public static Context context;
	public static Stations stations;
	public static SettingsData settingsData;
//	public static SetupData setupData;
	public static Lines lines;
	public static Integer language;

	public static final String EXTRA_MESSAGE_STATION = "station";
	public static final String EXTRA_MESSAGE_LINE = "line";
	
	public static int COLOR_TRAM = Color.rgb(0x00, 0x99, 0xCC);
	public static int COLOR_BUS = Color.rgb(0xFF, 0x88, 0x00);

	// ZET set
//	private static final int DIRECTION_EAST_INT = 1;
//	private static final int DIRECTION_WEST_INT = 2;
//	private static final int DIRECTION_NORTH_INT = 3;
//	private static final int DIRECTION_SOUTH_INT = 4;

	private static final int DIRECTION_OTHER_INT = 5;
	public static final int DIRECTION_TURNSTILE_INT = 0;

//	private static final String DIRECTION_NORTH_STRING = "SJEVER";
//	private static final String DIRECTION_SOUTH_STRING = "JUG";
//	private static final String DIRECTION_EAST_STRING = "ISTOK";
//	private static final String DIRECTION_WEST_STRING = "ZAPAD";
//	private static final String DIRECTION_OTHER_STRING = "NESVRSTANO";
//	private static final String DIRECTION_TURNSTILE_STRING = "OKRETI�TE";

//	public static final String[] DIRECTIONS = new String[6];
//	static
//	{
//		DIRECTIONS[DIRECTION_EAST_INT] = DIRECTION_EAST_STRING;
//		DIRECTIONS[DIRECTION_NORTH_INT] = DIRECTION_NORTH_STRING;
//		DIRECTIONS[DIRECTION_OTHER_INT] = DIRECTION_OTHER_STRING;
//		DIRECTIONS[DIRECTION_SOUTH_INT] = DIRECTION_SOUTH_STRING;
//		DIRECTIONS[DIRECTION_TURNSTILE_INT] = DIRECTION_TURNSTILE_STRING;
//		DIRECTIONS[DIRECTION_WEST_INT] = DIRECTION_WEST_STRING;
//	}

	private static final int TEXT_SIZE_SEC_STRING = 15;
	private static final int TEXT_SIZE_SEC_NUM = 20;

	public static final int DAY = 86400;
	public static final int HOUR = 3600;
	public static final long MINUTE = 60;
	public static final long MAX_TIME_FOR_SEC = 5 * MINUTE;

	private static final int[] COLOR_INTEGERS = { Color.rgb(0, 95, 229),
			Color.rgb(55, 126, 184), Color.rgb(20, 160, 211),
			Color.rgb(21, 127, 147), Color.rgb(9, 112, 84),
			Color.rgb(102, 120, 1), Color.rgb(71, 168, 69),
			Color.rgb(10, 180, 180), Color.rgb(130, 190, 0),
			Color.rgb(96, 36, 85), Color.rgb(142, 72, 149),
			Color.rgb(130, 0, 168), Color.rgb(179, 22, 105),
			Color.rgb(221, 8, 114), Color.rgb(205, 92, 92),
			Color.rgb(228, 26, 28), Color.rgb(255, 127, 0),
			Color.rgb(223, 180, 98), Color.rgb(235, 207, 0) };
	private static final int COLOR_COUNT = COLOR_INTEGERS.length;
	private static final int[] COLOR_COMBINATION = { 13, 11 };

	public static List<String> getStationNamesContaining(String content)
	{
		content = content.toLowerCase(Locale.ENGLISH).replaceAll("�", "c")
				.replaceAll("�", "c").replaceAll("�", "z").replaceAll("�", "s")
				.replaceAll("�", "d").replaceAll("[^a-z0-9]", "");
		List<String> result = new ArrayList<String>();

		// int n=15;
		int i = 0;
		for (String s : stations.KeysToSearch)
		{
			if (s.contains(content))
			{
				// result.add(s);
				result.add(stations.Keys.get(i));
				// if(--n<0)
				// {
				// result.add("...");
				// return result;
				// }
			}
			i++;
		}
		return result;
	}
	
	public static List<String> getLineNamesContaining(String content)
	{
		List<String> result = new ArrayList<String>();

		for (String s : lines.Keys)
			if (s.contains(content))
				result.add(s);
		return result;
	}

	public static int getGate(String gate)
	{
		int temp;
		try
		{
			temp = Integer.parseInt(gate);
		}
		catch (Exception e)
		{
			return DIRECTION_OTHER_INT;
		}
		if ((temp >= 1 && temp <= 4) || (temp >= 21 && temp <= 24))
			return temp % 10;
		if (temp < 10 || (temp >= 20 && temp < 30))
			return DIRECTION_OTHER_INT;
		return DIRECTION_TURNSTILE_INT;
	}

	public static long convertTimeToEpoch(String timeString)
	{
		SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
		Date date;
		try
		{
			date = df.parse(timeString);
		}
		catch (ParseException e)
		{
			return -1;
		}
		return date.getTime() / 1000;
	}

	public static long convertDateToEpoch(String timeString)
	{
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try
		{
			date = df.parse(timeString);
		}
		catch (ParseException e)
		{
			return -1;
		}
		return date.getTime() / 1000;
	}

	public static String convertCro(String s)
	{
		if (s == null)
			return null;
		return s.replace("\u00C8", "\u010C")// �
				.replace("\u00E8", "\u010D")// �
				.replace("\u00F0", "\u0111") // �
				.replace("\u00D0", "\u0110") // �
				.replace("\u008E", "\u017D") // �
				.replace("\u009E", "\u017E") // �
				.replace("\u009A", "\u0161") // �
				.replace("\u008A", "\u0160") // �
				.replace("\u00E6", "\u0107") // �
				.replace("\u00C6", "\u0106"); // �
	}

	public static int getLineColor(String line)
	{
		int colorInteger;
		try
		{
			colorInteger = Integer.parseInt(line);
		}
		catch (Exception e)
		{
			return COLOR_INTEGERS[new Random().nextInt(17) % COLOR_COUNT];
		}
		colorInteger = (COLOR_COMBINATION[0] * colorInteger + COLOR_COMBINATION[1]);
		colorInteger %= COLOR_COUNT;
		return COLOR_INTEGERS[colorInteger];
	}

	public static int getStationTypeColor(boolean isTram)
	{
		return isTram ? COLOR_TRAM : COLOR_BUS;
	}

	public static String getStationTypeName(boolean isTram)
	{
		return isTram ? "TRAM" : "BUS";
	}

	public static String getMinString(int time)
	{
		if (time < 3600)
			return String.valueOf(time / 60);
		return String.valueOf(String.format("%d:%02d", time / 3600,
				(time / 60) % 60));
	}

	public static String getSecString(int time)
	{
		if (time < MAX_TIME_FOR_SEC)
			return String.format("%02d", time % 60);
		return "min";
	}

	public static int getSecSize(int time)
	{
		if (time < MAX_TIME_FOR_SEC)
			return TEXT_SIZE_SEC_NUM;
		return TEXT_SIZE_SEC_STRING;
	}

	public static String getDistanceTxt(int distance)
	{
		if (distance < 0)
			return null;
		if (distance < 500)
			return distance + "\nm";
		if (distance < 5000)
			return ((float) distance) / 1000 + "\nkm";
		return distance / 1000 + "\nkm";
	}

	public static boolean isNetworkReady(Context ctx)
	{
		ConnectivityManager conMgr = (ConnectivityManager) ctx
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo i = conMgr.getActiveNetworkInfo();
		if (i == null)
			return false;
		if (!i.isConnected())
			return false;
		if (!i.isAvailable())
			return false;
		return true;
	}
	


}
