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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StationLiveAdapter extends ArrayAdapter<Row>
{
	private Context context;
	private int layoutResourceId;
	private List<Row> data = null;

	public StationLiveAdapter(Context context, int layoutResourceId,
			List<Row> data)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// Log.e("getView",""+position +"/"+data.size());
		View row = convertView;
		RowHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new RowHolder();
			holder.thumbnail = (RelativeLayout) row
					.findViewById(R.id.live_station_thumbnail);
			holder.txtDestination = (TextView) row
					.findViewById(R.id.live_station_destination);
			holder.txtLine = (TextView) row
					.findViewById(R.id.live_station_line_number);
			holder.txtTimeMin = (TextView) row
					.findViewById(R.id.live_station_time_min);
			holder.txtTimeSec = (TextView) row
					.findViewById(R.id.live_station_time_sec);

			row.setTag(holder);
		}
		else
		{
			holder = (RowHolder) row.getTag();
		}
		Row rowData = data.get(position);

		 try
		 {
		 row.setId(Integer.valueOf(rowData.lineString));//trip
		 }
		 catch (Exception e)
		 {
		 row.setId(0);// TODO:look at and think 'bout it
		 }
		holder.txtDestination.setText(rowData.destinationString);
		holder.txtLine.setText(rowData.lineString);

		int time = rowData.departureEpoch;

		holder.txtTimeMin.setText(AppData.getMinString(time));
		holder.txtTimeSec.setText(AppData.getSecString(time));
		holder.txtTimeSec.setTextSize(AppData.getSecSize(time));
		
		if (rowData.isLiveData==null||!rowData.isLiveData)
		{
			holder.txtTimeMin.setTextColor(Color.rgb(150, 0, 0));
			holder.txtTimeSec.setTextColor(Color.rgb(150, 0, 0));
		}

		holder.thumbnail.getBackground().setColorFilter(
				AppData.getLineColor(rowData.lineString),
				PorterDuff.Mode.MULTIPLY);
		rowData.departureSecTextView = holder.txtTimeSec;
		rowData.departureMinTextView = holder.txtTimeMin;
		return row;
	}

	@Override
	public int getCount()
	{
		// TODO Auto-generated method stub
		return (data != null) ? data.size() : 0;
	}

	public void refill(List<Row> rows)
	{
		data.clear();
		data.addAll(rows);
		notifyDataSetChanged();
	}

	static class RowHolder
	{
		RelativeLayout thumbnail;
		TextView txtDestination;
		TextView txtLine;
		TextView txtTimeMin;
		TextView txtTimeSec;
	}
}
