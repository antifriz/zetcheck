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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.zetcheck.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class SearchLinesFragment extends Fragment
{
	private ReentrantLock reentrantLock = new ReentrantLock();
	private EditText editText;
	private ListView listView;
	private String querry = null;

	private LinesListAdapter adapter;
	private boolean isAdapterSet = false;
	public final Handler handler = new Handler();
	private ImageView imageView;

	public SearchLinesFragment()
	{
		super();
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(android.view.LayoutInflater inflater,
			android.view.ViewGroup container, Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View myView = inflater.inflate(R.layout.fragment_search_lines, null,
				false);

		isAdapterSet = false;

		editText = (EditText) myView.findViewById(R.id.lines_input);
		listView = (ListView) myView.findViewById(R.id.lines_results);
		imageView = (ImageView) myView.findViewById(R.id.lines_close);

		imageView.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				editText.setText("");
			}
		});
		editText.addTextChangedListener(new TextWatcher()
		{
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count)
			{}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{}

			@Override
			public void afterTextChanged(Editable s)
			{
				querry = s.toString();
				new SearchLinesAsyncTask().execute(new String[] { querry });
			}
		});
		listView.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3)
			{
				callLine(arg1.getId());
			}
		});

		querry = "";
		new SearchLinesAsyncTask().execute(new String[] { querry });
		return myView;
	}

	protected void callLine(int id)
	{
		Intent intent;
		if (!AppData.isFroyo)
			intent = new Intent(getActivity(), LineActivity.class);
		else
			intent = new Intent(getActivity(), LineActivity_low.class);
		intent.putExtra(AppData.EXTRA_MESSAGE_LINE, id);
		startActivity(intent);
	}

	protected void RefreshLineNamesList(List<String> list)
	{
		List<Line> data = new ArrayList<Line>();
		Line line;
		for (String string : list)
		{
			line = AppData.lines.Map.get(string);
			if (line == null)
			{
				// Log.e("line == null", "!!!"); // TODO: error handling
			}
			data.add(line);
		}
		if (!isAdapterSet)
		{
			adapter = new LinesListAdapter(getActivity(),
					R.layout.line_row_layout, data);
			listView.setAdapter(adapter);
			isAdapterSet = true;
		}
		else
		{
			adapter.refill(data);
		}
	}

	private class SearchLinesAsyncTask extends
			AsyncTask<String, Integer, List<String>>
	{
		String lastQuerry;

		@Override
		protected List<String> doInBackground(String... params)
		{
			lastQuerry = params[0];
			return AppData.getLineNamesContaining(lastQuerry);
		}

		@Override
		protected void onPostExecute(List<String> result)
		{
			reentrantLock.lock();
			if (lastQuerry == querry)
				RefreshLineNamesList(result);
			reentrantLock.unlock();
		}
	}
}
