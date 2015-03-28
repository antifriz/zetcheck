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

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Departures
{
	public long creationEpoch;
	public List<List<Row>> gates;
	public boolean isSet;
	public String timeCreatedString;

	private String xMLdataString;
	private long creationDateEpoch;
	private long creationTimeEpoch;

	public Departures()
	{
		gates = new ArrayList<List<Row>>();
		gates.add(new ArrayList<Row>());
		gates.add(new ArrayList<Row>());
		gates.add(new ArrayList<Row>());
		gates.add(new ArrayList<Row>());
		gates.add(new ArrayList<Row>());
		gates.add(new ArrayList<Row>());
		isSet = false;
	}

	public synchronized void getDepartures(String XMLdata)
	{
		gates.get(0).clear();
		gates.get(1).clear();
		gates.get(2).clear();
		gates.get(3).clear();
		gates.get(4).clear();
		gates.get(5).clear();

		xMLdataString = XMLdata;
		fillDataLists();

		isSet = true;
	}

	public synchronized void refreshDepartures()
	{
		for (List<Row> list : gates)
		{
			for (Row row : list)
			{
				if (row.departureEpoch > 0)
					row.departureEpoch--;

			}
		}
	}

	private void fillDataLists()
	{
		Document doc;
		try
		{
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			doc = docBuilder.parse(new InputSource(new StringReader(
					xMLdataString)));
		}
		catch (Exception e)
		{
			// TODO: error handling

			e.printStackTrace();
			return;
		}
		try
		{
			String[] timeAndDateCreated = doc.getFirstChild().getFirstChild().getTextContent().split(" ");
			// Log.e(timeAndDateCreated[0], timeAndDateCreated[1]);
			creationDateEpoch = AppData
					.convertDateToEpoch(timeAndDateCreated[0]);
			creationTimeEpoch = AppData
					.convertTimeToEpoch(timeAndDateCreated[1]);
			creationEpoch = creationTimeEpoch + creationDateEpoch;
			timeCreatedString = timeAndDateCreated[1];
		}
		catch (Exception e)
		{
			// TODO: handle exception
			// Log.e("epocherr", "epoch");
			return;
		}

		NodeList nList = doc.getElementsByTagName("row");

		int count = nList.getLength();
		for (int i = 0; i < count; i++)
		{
			Node nNode = nList.item(i);

			Element eElement = (Element) nNode;

			int idx = AppData.getGate(eElement.getAttribute("d"));

			List<Row> rows;
			try
			{
				rows = gates.get(idx);
			}
			catch (Exception e)
			{
				rows = new ArrayList<Row>();
				gates.set(idx, rows);
			}

			Row row = new Row();

			row.lineString = eElement.getAttribute("a");
			row.tripString = eElement.getAttribute("b");
			row.destinationString = eElement.getAttribute("c");

			row.isLiveData = false;
			try
			{
				String departureString = eElement.getAttribute("f");
				if (departureString.length() < 7)
				{
					departureString = eElement.getAttribute("e");
					if (departureString.length() >= 7)
						row.departureEpoch = getRelativeTime(AppData
								.convertTimeToEpoch(departureString));
				}
				else
				{
					row.isLiveData = true;
					row.departureEpoch = getRelativeTime(AppData
							.convertTimeToEpoch(departureString));
				}
			}
			catch (Exception e2)
			{
			}

			if (row.departureEpoch==null ||row.departureEpoch < 0)
				row.departureEpoch = 0;

			rows.add(row);
			gates.set(idx, rows);
		}
	}

	private int getRelativeTime(long time)
	{
		long timeDiff = time - creationTimeEpoch;
		if (timeDiff < -AppData.DAY / 2)
			timeDiff += AppData.DAY;
		return (int) timeDiff;
	}
}
