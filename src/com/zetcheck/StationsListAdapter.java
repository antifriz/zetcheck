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

import java.util.List;

import com.zetcheck.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StationsListAdapter extends ArrayAdapter<Station>
{
	private Context context;
	private int layoutResourceId;
	private List<Station> data = null;

	public StationsListAdapter(Context context, int layoutResourceId,
			List<Station> data)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}
	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return (data!=null)?data.size():0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		StationHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new StationHolder();
			holder.thumbnail = (RelativeLayout) row
					.findViewById(R.id.search_station_thumbnail);
			holder.txtName = (TextView) row
					.findViewById(R.id.search_station_name);
			holder.txtType = (TextView) row
					.findViewById(R.id.search_station_type);
			holder.txtLines = (TextView) row
					.findViewById(R.id.vehicles_row_to);
			holder.txtDistance = (TextView) row
					.findViewById(R.id.search_station_distance);

			row.setTag(holder);
		}
		else
		{
			holder = (StationHolder) row.getTag();
		}

		Station station = data.get(position);
		row.setId(station.Id);
		holder.txtName.setText(station.Name);
		holder.txtType.setText(AppData.getStationTypeName(station.IsTram));
		holder.txtLines.setText(station.Lines);
		String temp = AppData.getDistanceTxt(station.Distance);
		if (temp != null)
		{
			holder.txtDistance.setVisibility(View.VISIBLE);
			holder.txtDistance.setText(temp);
		}
		else
		{
			holder.txtDistance.setVisibility(View.VISIBLE);
		}
		holder.thumbnail.getBackground().setColorFilter(
				AppData.getStationTypeColor(station.IsTram),
				PorterDuff.Mode.MULTIPLY);

		return row;
	}

	static class StationHolder
	{
		RelativeLayout thumbnail;
		TextView txtName;
		TextView txtType;
		TextView txtLines;
		TextView txtDistance;
	}

	public void refill(List<Station> stations)
	{
		data.clear();
		if(stations!=null)
			data.addAll(stations);
		notifyDataSetChanged();
	}
}
