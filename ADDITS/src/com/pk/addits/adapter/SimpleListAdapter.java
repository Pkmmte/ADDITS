package com.pk.addits.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pk.addits.R;

public class SimpleListAdapter extends BaseAdapter
{
	private Context context;

	private List<String> listItem;

	public SimpleListAdapter(Context context, List<String> listItem)
	{
		this.context = context;
		this.listItem = listItem;
	}

	public int getCount()
	{
		return listItem.size();
	}

	public Object getItem(int position)
	{
		return listItem.get(position);
	}

	public long getItemId(int position)
	{
		return position;
	}

	public View getView(int position, View view, ViewGroup viewGroup)
	{
		String entry = listItem.get(position);
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.dialog_list_item, null);
		}

		TextView textValue = (TextView) view.findViewById(R.id.txtValue);
		textValue.setText(entry);
		return view;
	}
}