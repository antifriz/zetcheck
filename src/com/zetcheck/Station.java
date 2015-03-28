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

public class Station implements Serializable,Comparable<Station>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String Name;
	public String Lines;
	public int Id;
	public double Longitude;
	public double Latitude;
	public boolean IsTram;
	public int Gates;
	public int Distance=-1;
	
	public Station() 
	{
	}
	public Station(Station station)
	{
		Id = station.Id;
		Longitude = station.Longitude;
		Latitude = station.Latitude;
		IsTram = station.IsTram;
		Gates = station.Gates;
		Distance = station.Distance;
		
		Name = new String(station.Name);
		Lines = new String(station.Lines);
	}
	@Override
	public int compareTo(Station another)
	{
		return this.Distance>another.Distance ? 1 : -1;
	}	
}
