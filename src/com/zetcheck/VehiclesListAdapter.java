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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VehiclesListAdapter extends ArrayAdapter<Vehicle>
{
	private Context context;
	private int layoutResourceId;
	private List<Vehicle> data = null;

	public VehiclesListAdapter(Context context, int layoutResourceId,
			List<Vehicle> data)
	{
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		//Log.e("getView", "" + position + "/" + data.size());
		View row = convertView;
		VehicleHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			// Log.e("getView2",""+position +"/"+data.size());
			row = inflater.inflate(layoutResourceId, parent, false);
			// Log.e("getView3",""+position +"/"+data.size());
			holder = new VehicleHolder();
			holder.viewBar = (View) row.findViewById(R.id.vehicles_row_bar);
			holder.txtTo = (TextView) row.findViewById(R.id.vehicles_row_to);
			holder.txtFrom = (TextView) row
					.findViewById(R.id.vehicles_row_from);
			holder.txtDestination = (TextView) row
					.findViewById(R.id.vehicles_row_destination);
			holder.txtSubline = (TextView) row
					.findViewById(R.id.vehicles_row_subline);

			row.setTag(holder);
		}
		else
		{
			holder = (VehicleHolder) row.getTag();
		}
		Vehicle vehicleData = data.get(position);

		holder.txtDestination.setText(vehicleData.Target.Name);
		holder.txtFrom.setText(vehicleData.From.Name);
		holder.txtTo.setText(vehicleData.To.Name);
		holder.txtSubline.setText(vehicleData.Subline.startsWith("-")?"ERR":vehicleData.Subline.substring(0,
				vehicleData.Line.length())
				+ " "
				+ vehicleData.Subline.substring(vehicleData.Line.length()));
		holder.viewBar.setLayoutParams(new LinearLayout.LayoutParams(
				0, 10,
				vehicleData._perc));
		return row;
	}

	@Override
	public int getCount()
	{
		return (data != null) ? data.size() : 0;
	}

	public void refill(List<Vehicle> rows)
	{
		data.clear();
		if(rows!=null)
			data.addAll(rows);
		notifyDataSetChanged();
	}

	static class VehicleHolder
	{
		View viewBar;
		TextView txtFrom;
		TextView txtTo;
		TextView txtDestination;
		TextView txtSubline;
	}
}
