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

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

public class Stations implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int TRAM = 177;
	public static final int BUS = 178;
	public HashMap<String, Station> Map = new HashMap<String, Station>();
	public List<String> Keys = new ArrayList<String>();
	public List<String> KeysToSearch = new ArrayList<String>();
	public List<Integer> idToSearch = new ArrayList<Integer>();
	public List<Double> lattitudeDoubles = new ArrayList<Double>();
	public List<Double> longitudeDoubles = new ArrayList<Double>();

	public Stations(String xmlStationsData)
	{
		Document doc;
		try
		{
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.parse(new InputSource(new StringReader(
					xmlStationsData)));
		}
		catch (Exception e)
		{
			return;
		}

		NodeList nList = doc.getElementsByTagName("hst");

		int count = nList.getLength();
		int i;
		for (i = 0; i < count; i++)
		{
			Node nNode = nList.item(i);

			Element eElement = (Element) nNode;

			String lines = removeNines(eElement.getAttribute("li"));

			if (lines.length() <= 0)
				continue;

			boolean isTram = isTram(lines) ? true : false;
			String name = eElement.getAttribute("na");

			int id;
			try
			{
				id = Integer.parseInt(eElement.getAttribute("no"));
			}
			catch (Exception e)
			{
				// Log.e(String.valueOf(i)+" "+"TryParse",name);
				continue;
			}

			Double latDouble;
			Double longDouble;
			try
			{
				latDouble = Double.parseDouble(eElement.getAttribute("la"));
				longDouble = Double.parseDouble(eElement.getAttribute("lo"));

			}
			catch (Exception e)
			{
				latDouble = longDouble = 0.D;
			}

			NodeList nNodeList = nNode.getChildNodes();
			Integer dIdInteger = 0;

			boolean hasLocation = (latDouble == 0 || longDouble == 0) ? false
					: true;
			int locationCnt = 0;
			if (!hasLocation)
			{
				for (int ii = 0; ii < nNodeList.getLength(); ii++)
				{
					Double latTempDouble = 0.D;
					Double longTempDouble = 0.D;
					try
					{
						latDouble = Double.parseDouble(eElement
								.getAttribute("dLa"));
						longDouble = Double.parseDouble(eElement
								.getAttribute("dLo"));
					}
					finally
					{
						if (latTempDouble == 0 || longTempDouble == 0)
							continue;
						latDouble += latTempDouble;
						longDouble += longTempDouble;
						locationCnt++;
					}
				}
				if (locationCnt > 0)
				{
					latDouble /= locationCnt;
					longDouble /= locationCnt;
				}
			}

			for (int ii = 0; ii < nNodeList.getLength(); ii++)
			{
				eElement = (Element) nNodeList.item(ii);
				String temp = eElement.getAttribute("dId");
				if (temp.contains("10"))
					continue;
				// Log.e("---", temp+":"+
				// (dIdInteger|(1<<AppData.getGate(temp))));
				dIdInteger |= 1 << AppData.getGate(temp);
			}

			Station station = new Station();
			station.Gates = dIdInteger;
			station.Id = id;
			station.IsTram = isTram;
			station.Longitude = longDouble;
			station.Latitude = latDouble;
			station.Name = AppData.convertCro(name);
			station.Lines = removeNines(lines);
//			 Log.e(station.Name, ""+station.Gates);

			name = ((char) (isTram ? TRAM : BUS)) + name;
			while (Map.get(name) != null)
			{
				name = name.concat("I");
				// Log.w("imezamjeni", name);
			}

			Keys.add(name);
			KeysToSearch.add(name.toLowerCase(Locale.ENGLISH)
					.replaceAll("�", "c").replaceAll("�", "c")
					.replaceAll("�", "z").replaceAll("�", "s")
					.replaceAll("�", "d").replaceAll("[^a-z0-9]", ""));
			idToSearch.add(id);

			lattitudeDoubles.add(latDouble);
			longitudeDoubles.add(longDouble);
//
//			Log.e("" + latDouble, "" + longDouble);
			Map.put(name, station);
		}

		 Log.v("Ucitao stanice:",String.valueOf(i) + "/" + String.valueOf(count));
	}

	private static boolean isTram(String s)
	{
		String[] strings = s.split(" ");
		for (String string : strings)
		{
			if (string.length() <= 2)
				return true;
		}
		return false;
	}

	public List<String> getStationNamesContaining(String content)
	{
		content.toLowerCase(Locale.ENGLISH).replaceAll("[^a-z0-9]", "");
		String matcher;
		List<String> result = new ArrayList<String>();
		for (String stationName : Keys)
		{
			matcher = stationName.toLowerCase(Locale.ENGLISH).replaceAll(
					"[^a-z0-9]", "");
			if (matcher.contains(content))
				result.add(stationName);
		}
		return result;
	}

	public String removeNines(String s)
	{
		String[] ss = s.split(" ");
		for (String sss : ss)
		{
			if (sss.startsWith("9") && sss.length() > 1)
				s = s.replaceFirst(sss, "");
		}
		return s.replaceAll("  ", " ");

	}

}
