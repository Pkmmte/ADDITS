package com.pk.addits.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.pk.addits.R;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.adapter.FeedAdapter;
import com.pk.addits.data.Data;
import com.pk.addits.model.Article;

public class FragmentSearch extends Fragment
{
	private SharedPreferences prefs;
	private boolean adsEnabled;
	private String searchQuery;
	
	private GridView grid;
	private LinearLayout ad;
	private LinearLayout searching;
	
	public static FragmentSearch newInstance(String query)
	{
		FragmentSearch f = new FragmentSearch();
		
		Bundle bdl = new Bundle();
		bdl.putString("Search Query", query);
		f.setArguments(bdl);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		View view = inflater.inflate(R.layout.fragment_search, container, false);
		
		grid = (GridView) view.findViewById(R.id.GridView);
		ad = (LinearLayout) view.findViewById(R.id.ad);
		searching = (LinearLayout) view.findViewById(R.id.Searching);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		
		prefs = getActivity().getSharedPreferences(Data.PREFS_TAG, 0);
		adsEnabled = prefs.getBoolean(Data.PREF_TAG_ADS_ENABLED, true);
		searchQuery = getArguments().getString("Search Query", "");
		
		if (!adsEnabled)
			ad.setVisibility(View.GONE);
		
		new SearchAsyncTask().execute();
	}
	
	private class SearchAsyncTask extends AsyncTask<Void, Void, Void>
	{
		private List<Article> list;
		private FeedAdapter adapter;
		
		@Override
		protected Void doInBackground(Void... params)
		{
			list = new ArrayList<Article>();
			for (Article a : ActivityMain.articleList)
			{
				if (a.getAuthor().toLowerCase(Locale.US).contains(searchQuery) || 
					a.getTitle().toLowerCase(Locale.US).contains(searchQuery) ||
					a.getDescription().toLowerCase(Locale.US).contains(searchQuery) ||
					a.getContent().toLowerCase(Locale.US).contains(searchQuery))
					list.add(a);
			}
			
			adapter = new FeedAdapter(getActivity(), list);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void p)
		{
			searching.setVisibility(View.GONE);
			grid.setVisibility(View.VISIBLE);
			
			grid.setAdapter(adapter);
			grid.setOnItemClickListener(new OnItemClickListener()
			{
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int position, long index)
				{
					Article article = list.get(position);
					ActivityMain.callArticle(article, 0, 0);
				}
			});
		}
	}
}
