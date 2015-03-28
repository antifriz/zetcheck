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
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Vehicles
{
	String xMLdataString;
	List<Vehicle> list = new ArrayList<Vehicle>();
	String line;

	public Vehicles(String string, String _line)
	{
		xMLdataString = string;
		line = _line;
		fillDataLists();
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

		NodeList nList = doc.getElementsByTagName("frt");

		int count = nList.getLength();
		for (int i = 0; i < count; i++)
		{
			Node nNode = nList.item(i);

			Element eElement = (Element) nNode;

			Vehicle v = new Vehicle();
			v.Line = eElement.getAttribute("l");
			if (!v.Line.equals(line))
				continue;
			v.Subline = eElement.getAttribute("u");
			v.ID = eElement.getAttribute("f");
			//v.Real = eElement.getAttribute("r");
			v.TargetID = eElement.getAttribute("d");
			//v.Soft = eElement.getAttribute("s");
			//v.Data = eElement.getAttribute("t");
			//v.Fpa = eElement.getAttribute("a");
			//v.PosTop = eElement.getAttribute("h");
			//v.PosLeft = eElement.getAttribute("b");
			//v.PosZ = eElement.getAttribute("z");
			//v.NextF = eElement.getAttribute("nf");
			//v.RouteV = eElement.getAttribute("rv");
			//v.Lon = eElement.getAttribute("rx");
			//v.Lat = eElement.getAttribute("ry");
			v.FromID = eElement.getAttribute("v");
			v.ToID = eElement.getAttribute("n");
			v.Perc = eElement.getAttribute("p");

			v.convert();

			list.add(v);
		}
		Collections.sort(list);
	}

}
