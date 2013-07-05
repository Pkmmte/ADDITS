package com.pk.addits;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pk.addits.fadingactionbar.FadingActionBarHelperHome2;
import com.squareup.picasso.Picasso;

public class FragmentHome extends Fragment
{
	static View view;
	static ScrollGridView grid;
	static FrameLayout frame;
	static Button moar;
	static FeedAdapter adapter;
	static FadingActionBarHelperHome2 mFadingHelper;
	
	static List<Feed> feedList;
	static Feed[] NewsFeed;
	
	static int currentSlide;
	static Timer timer;
	Handler timeHandler;
	long startTime;
	
	static Fragment fragSlide;
	static FragmentManager fm;
	static int numLoaded;
	static Integer currentSlideID;
	
	Typeface fontRegular;
	Typeface fontBold;
	Typeface fontLight;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = mFadingHelper.createView(inflater);
		
		feedList = new ArrayList<Feed>();
		grid = (ScrollGridView) view.findViewById(R.id.GridView);
		moar = (Button) view.findViewById(R.id.MoarArticles);
		
		adapter = new FeedAdapter(getActivity(), feedList);
		grid.setAdapter(adapter);
		grid.setExpanded(true);
		currentSlide = 1;
		numLoaded = 0;
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		fm = getChildFragmentManager();
		NewsFeed = ActivityMain.getFeed();
		fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Regular.ttf");
		fontBold = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Bold.ttf");
		fontLight = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Light.ttf");
		
		timer = new Timer();
		timeHandler = new Handler(new Callback()
		{
			@Override
			public boolean handleMessage(Message msg)
			{
				if (mFadingHelper.mLastScrollPosition < 10 && NewsFeed != null)
					populateSlide();
				
				return false;
			}
		});
		timer.schedule(new firstTask(), 5000, 7000);
		
		populateSlide();
		
		updateState();
		
		grid.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long index)
			{
				int ID = feedList.get(position).getID();
				String Title = feedList.get(position).getTitle();
				String Description = feedList.get(position).getDescription();
				String Content = feedList.get(position).getContent();
				String CommentFeed = feedList.get(position).getCommentFeed();
				String Author = feedList.get(position).getAuthor();
				String Date = feedList.get(position).getDate();
				String Category = feedList.get(position).getCategory();
				String Image = feedList.get(position).getImage();
				String URL = feedList.get(position).getURL();
				int Comments = feedList.get(position).getComments();
				boolean Favorite = feedList.get(position).isFavorite();
				boolean Read = feedList.get(position).isRead();
				
				Feed Article = new Feed(ID, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Comments, Favorite, Read);
				ActivityMain.callArticle(getActivity(), Article);
			}
		});
		moar.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				moar.setText("Loading Articles...");
				
				if (numLoaded + 10 < NewsFeed.length)
					addArticles(10);
				else
					addArticles(NewsFeed.length - numLoaded);
			}
		});
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		
		timer.cancel();
		timer.purge();
	}
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		
		frame = new FrameLayout(activity);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Data.getHeightByPercent(activity, 0.35));
		frame.setLayoutParams(layoutParams);
		frame.setId(R.id.slider_content);
		frame.setClickable(true);
		mFadingHelper = new FadingActionBarHelperHome2().actionBarBackground(R.drawable.ab_background).headerView(frame).contentLayout(R.layout.fragment_home);
		mFadingHelper.initActionBar(activity);
	}
	
	public static void updateState()
	{
		NewsFeed = ActivityMain.getFeed();
		
		if (NewsFeed == null)
		{
			moar.setVisibility(View.GONE);
			feedList.clear();
		}
		else
		{
			grid.setVisibility(View.VISIBLE);
			
			if (numLoaded + 10 < NewsFeed.length)
				addArticles(10);
			else
				addArticles(NewsFeed.length - numLoaded);
			
			populateSlide();
		}
	}
	
	public static void addArticles(int number)
	{
		for (int x = 0; x < number; x++)
		{
			feedList.add(new Feed(NewsFeed[numLoaded].getID(), NewsFeed[numLoaded].getTitle(), NewsFeed[numLoaded].getDescription(), NewsFeed[numLoaded].getContent(), NewsFeed[numLoaded].getCommentFeed(), NewsFeed[numLoaded].getAuthor(), NewsFeed[numLoaded].getDate(), NewsFeed[numLoaded].getCategory(), NewsFeed[numLoaded].getImage(), NewsFeed[numLoaded].getURL(), NewsFeed[numLoaded].getComments(), NewsFeed[numLoaded].isFavorite(), NewsFeed[numLoaded].isRead()));
			numLoaded++;
		}
		
		if (numLoaded < NewsFeed.length)
		{
			moar.setText("Load More Articles");
			moar.setVisibility(View.VISIBLE);
		}
		else
			moar.setVisibility(View.GONE);
		adapter.notifyDataSetChanged();
	}
	
	public static void populateSlide()
	{
		String sTitle = "";
		String sAuthor = "";
		String sDate = "";
		String sImage = "";
		String sCategory = "";
		
		if (NewsFeed != null)
		{
			Random generator = new Random();
			int r = generator.nextInt(NewsFeed.length);
			sTitle = NewsFeed[r].getTitle();
			sAuthor = NewsFeed[r].getAuthor();
			sDate = NewsFeed[r].getDate();
			sImage = NewsFeed[r].getImage();
			sCategory = NewsFeed[r].getCategory();
			currentSlideID = NewsFeed[r].getID();
		}
		if(!sCategory.equalsIgnoreCase("DAILY SAVER"))
			fragSlide = Slider.newInstance(sTitle, sAuthor, sDate, sImage);
		else
		{
			populateSlide();
			return;
		}
		
		FragmentTransaction trans = fm.beginTransaction();
		trans.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in);
		trans.replace(R.id.slider_content, fragSlide);
		trans.commit();
	}
	
	public static void onHeaderClickListener(View v)
	{
		if(currentSlideID != null)
		{
			int ID = NewsFeed[currentSlideID].getID();
			String Title = NewsFeed[currentSlideID].getTitle();
			String Description = NewsFeed[currentSlideID].getDescription();
			String Content = NewsFeed[currentSlideID].getContent();
			String CommentFeed = NewsFeed[currentSlideID].getCommentFeed();
			String Author = NewsFeed[currentSlideID].getAuthor();
			String Date = NewsFeed[currentSlideID].getDate();
			String Category = NewsFeed[currentSlideID].getCategory();
			String Image = NewsFeed[currentSlideID].getImage();
			String URL = NewsFeed[currentSlideID].getURL();
			int Comments = NewsFeed[currentSlideID].getComments();
			boolean Favorite = NewsFeed[currentSlideID].isFavorite();
			boolean Read = NewsFeed[currentSlideID].isRead();
			
			Feed Article = new Feed(ID, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Comments, Favorite, Read);
			ActivityMain.callArticle(v.getContext(), Article);
		}
	}
	
	class firstTask extends TimerTask
	{
		@Override
		public void run()
		{
			timeHandler.sendEmptyMessage(0);
		}
	};
	
	public class FeedAdapter extends BaseAdapter
	{
		private Context context;
		
		private List<Feed> listItem;
		
		public FeedAdapter(Context context, List<Feed> listItem)
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
			Feed entry = listItem.get(position);
			if (view == null)
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.feed_item, null);
				
				holder = new ViewHolder();
				holder.txtTitle = (TextView) view.findViewById(R.id.txtTitle);
				holder.txtDescription = (TextView) view.findViewById(R.id.txtDescription);
				holder.txtAuthor = (TextView) view.findViewById(R.id.txtAuthor);
				holder.txtDate = (TextView) view.findViewById(R.id.txtDate);
				holder.txtCategory = (TextView) view.findViewById(R.id.txtCategory);
				holder.imgPreview = (ImageView) view.findViewById(R.id.imgPreview);
				
				holder.txtTitle.setTypeface(fontBold);
				holder.txtDescription.setTypeface(fontRegular);
				holder.txtAuthor.setTypeface(fontLight);
				holder.txtDate.setTypeface(fontLight);
				holder.txtCategory.setTypeface(fontRegular);
				
				view.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) view.getTag();
			}
			
			holder.txtTitle.setText(entry.getTitle());
			holder.txtDescription.setText(entry.getDescription());
			holder.txtAuthor.setText("Posted by " + entry.getAuthor());
			holder.txtDate.setText(entry.getDate());
			holder.txtCategory.setText(entry.getCategory());
			
			if (entry.getImage().length() > 0)
				Picasso.with(context).load(entry.getImage()).error(R.drawable.no_image_banner).fit().skipCache().into(holder.imgPreview);
			else
				Picasso.with(context).load(R.drawable.no_image_banner).fit().into(holder.imgPreview);
			
			return view;
		}
	}
	
	private static class ViewHolder
	{
		public TextView txtTitle;
		public TextView txtDescription;
		public TextView txtAuthor;
		public TextView txtDate;
		public TextView txtCategory;
		public ImageView imgPreview;
		
	}
}
