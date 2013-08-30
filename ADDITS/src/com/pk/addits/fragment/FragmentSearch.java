package com.pk.addits.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import com.pk.addits.R;
import com.pk.addits.activity.ActivityMain;
import com.pk.addits.adapter.FeedAdapter;
import com.pk.addits.data.Data;
import com.pk.addits.model.Article;
import com.pk.addits.model.SerializableArticleList;

public class FragmentSearch extends Fragment
{
	private SharedPreferences prefs;
	private boolean adsEnabled;
	private String searchQuery;
	
	private GridView grid;
	private LinearLayout ad;
	private LinearLayout searching;
	private TextView noResults;

	private InputMethodManager imm;
	private OnQueryTextListener queryListener;
	private SearchView searchView;
	
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
		setHasOptionsMenu(true);
		
		grid = (GridView) view.findViewById(R.id.GridView);
		ad = (LinearLayout) view.findViewById(R.id.ad);
		searching = (LinearLayout) view.findViewById(R.id.Searching);
		noResults = (TextView) view.findViewById(R.id.NoResults);
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();

		imm = (InputMethodManager) getActivity().getSystemService(FragmentActivity.INPUT_METHOD_SERVICE);
		prefs = getActivity().getSharedPreferences(Data.PREFS_TAG, 0);
		adsEnabled = prefs.getBoolean(Data.PREF_TAG_ADS_ENABLED, true);
		searchQuery = getArguments().getString("Search Query", "");
		
		if (!adsEnabled)
			ad.setVisibility(View.GONE);
		
		if (!getArguments().getBoolean("Search Complete", false))
			new SearchAsyncTask().execute();
		else
		{
			searching.setVisibility(View.GONE);
			
			SerializableArticleList sal = (SerializableArticleList) getArguments().getSerializable("Search Results");
			final List<Article> list = sal.getList();
			
			if (list.size() > 0)
			{
				grid.setVisibility(View.VISIBLE);
				
				FeedAdapter adapter = new FeedAdapter(getActivity(), list);
				
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
			else
				noResults.setVisibility(View.VISIBLE);
		}
		
		queryListener = new OnQueryTextListener()
		{
			@Override
			public boolean onQueryTextChange(String newText)
			{
				return false;
			}
			
			@Override
			public boolean onQueryTextSubmit(String query)
			{
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				ActivityMain.callSearch(query.toLowerCase(Locale.US));
				
				return false;
			}
		};
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
	{
		menu.clear();
		inflater.inflate(R.menu.home, menu);
		
		searchView = (SearchView) menu.findItem(R.id.Search_Label).getActionView();
		searchView.setOnQueryTextListener(queryListener);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.Website_Label:
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(Data.MAIN_URL));
				startActivity(i);
				return true;
			case R.id.Settings_Label:
				ActivityMain.callSettings();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	private class SearchAsyncTask extends AsyncTask<Void, Void, Void>
	{
		private List<Article> list;
		private FeedAdapter adapter;
		private SerializableArticleList sal;
		
		@Override
		protected Void doInBackground(Void... params)
		{
			list = new ArrayList<Article>();
			for (Article a : ActivityMain.articleList)
			{
				if (a.getAuthor().toLowerCase(Locale.US).contains(searchQuery) || a.getTitle().toLowerCase(Locale.US).contains(searchQuery) || a.getDescription().toLowerCase(Locale.US).contains(searchQuery) || a.getContent().toLowerCase(Locale.US).contains(searchQuery))
					list.add(a);
			}
			
			adapter = new FeedAdapter(getActivity(), list);
			sal = new SerializableArticleList(list);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void p)
		{
			searching.setVisibility(View.GONE);
			
			if (list.size() > 0)
			{
				
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
			else
				noResults.setVisibility(View.VISIBLE);
			
			getArguments().putBoolean("Search Complete", true);
			getArguments().putSerializable("Search Results", sal);
		}
	}
}
