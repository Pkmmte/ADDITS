package com.pk.addits.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.pk.addits.R;
import com.pk.addits.model.ChangelogItem;

public class ChangelogAdapter extends BaseAdapter
{
	private Context context;
	private List<ChangelogItem> listItem;
	
	public ChangelogAdapter(Context context, List<ChangelogItem> listItem)
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
		ViewHolder holder;
		ChangelogItem entry = listItem.get(position);
		if (view == null)
		{
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.dialog_changelog_list_item, null);
			
			holder = new ViewHolder();
			holder.txtBuild = (TextView) view.findViewById(R.id.txtBuild);
			holder.txtDate = (TextView) view.findViewById(R.id.txtDate);
			holder.txtLog = (TextView) view.findViewById(R.id.txtLog);
			
			view.setTag(holder);
		}
		else
			holder = (ViewHolder) view.getTag();
		
		holder.txtBuild.setText(entry.getBuild());
		holder.txtDate.setText(entry.getDate());
		holder.txtLog.setText(entry.getLog());
		
		return view;
	}
	
	private class ViewHolder
	{
		public TextView txtBuild;
		public TextView txtDate;
		public TextView txtLog;
	}
}