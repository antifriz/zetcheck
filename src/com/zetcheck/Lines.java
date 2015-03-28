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


public class Lines implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final int TRAM = 177;
	public static final int BUS = 178;
	public HashMap<String, Line> Map = new HashMap<String, Line>();
	public List<String> Keys = new ArrayList<String>();

	public Lines(String xmlStationsData)
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

		NodeList nList = doc.getElementsByTagName("row");

		int count = nList.getLength();
		int i;
		for (i = 0; i < count; i++)
		{
			Node nNode = nList.item(i);

			Element eElement = (Element) nNode;
			String line = removeNines(eElement.getAttribute("a"));
			if (line.length() <= 0)
				continue;

			String route = eElement.getAttribute("b");

			Keys.add(line);
			Map.put(line, new Line(line,route.replace(",","\n").replace("\n ", "\n")));
			// Log.v("" + line, "" + route);
		}

		//Log.v("Ucitao linije:", String.valueOf(i) + "/" + String.valueOf(count));
	}

	public static boolean isTram(String s)
	{
		if (s.length() <= 2)
			return true;
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
		s = s.split("\\|")[0];

		if (s.startsWith("9") && s.length() > 1)
			return "";
		return s;
	}
}
