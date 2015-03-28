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

public class LinesListAdapter extends ArrayAdapter<Line>
{
	private Context context;
	private int layoutResourceId;
	private List<Line> data = null;

	public LinesListAdapter(Context context, int layoutResourceId,
			List<Line> data)
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
		LineHolder holder = null;

		if (row == null)
		{
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new LineHolder();
			holder.thumbnail = (RelativeLayout) row
					.findViewById(R.id.search_lines_thumbnail);
			holder.txtType = (TextView) row
					.findViewById(R.id.search_lines_type);
			holder.txtRoute = (TextView) row
					.findViewById(R.id.search_lines_route);
			holder.txtNumber = (TextView) row
					.findViewById(R.id.search_lines_number);

			row.setTag(holder);
		}
		else
		{
			holder = (LineHolder) row.getTag();
		}

		Line line = data.get(position);
		row.setId(line._number);
		holder.txtNumber.setText(line.Number);
		holder.txtType.setText(AppData.getStationTypeName((line._number<100)?true:false));
		holder.txtRoute.setText(line.Route);
		holder.thumbnail.getBackground().setColorFilter(
				AppData.getStationTypeColor((line._number<100)?true:false),
				PorterDuff.Mode.MULTIPLY);

		return row;
	}

	static class LineHolder
	{
		RelativeLayout thumbnail;
		TextView txtType;
		TextView txtNumber;
		TextView txtRoute;
	}

	public void refill(List<Line> lines)
	{
		data.clear();
		if(lines!=null)
			data.addAll(lines);
		notifyDataSetChanged();
	}
}
