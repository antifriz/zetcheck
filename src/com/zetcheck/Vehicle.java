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

public class Vehicle implements Comparable<Vehicle>
{
	String Line;
	String Subline;
	Integer _line;
	String ID;
	String Real;
	String TargetID;
	Station Target;
	String Soft;
	String Data;
	String Fpa;
	String PosTop;
	String PosLeft;
	String PosZ;
	String NextF;
	String RouteV;
	String Lon;
	String Lat;
	String FromID;
	Station From;
	String ToID;
	Station To;
	String Perc;
	Float _perc;

	public boolean convert()
	{
		_line = Integer.parseInt(Line);
		_perc = (float) Integer.parseInt(Perc);
		if ((From = getStationNameFromId(FromID)) == null)
			return false;
		if ((To = getStationNameFromId(ToID)) == null)
			return false;
		if ((Target = getStationNameFromId(TargetID)) == null)
			return false;
		return true;
	}

	public static Station getStationNameFromId(String ID)
	{
		int loc = AppData.stations.idToSearch.indexOf(Integer.parseInt(ID));
		if (loc < 0)
			return null;
		return AppData.stations.Map
				.get(AppData.stations.Keys.get(AppData.stations.idToSearch
						.indexOf(Integer.parseInt(ID))));
	}

	@Override
	public int compareTo(Vehicle another)
	{
		return Integer.parseInt(this.Subline) > Integer
				.parseInt(another.Subline) ? 1 : -1;
	}
}
