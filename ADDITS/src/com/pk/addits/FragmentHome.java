package com.pk.addits;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.pk.addits.data.Data;
import com.pk.addits.fadingactionbar.FadingActionBarHelperHome2;
import com.pk.addits.models.Article;
import com.squareup.picasso.Picasso;

public class FragmentHome extends Fragment
{
	static View view;
	static ListView list;
	static FrameLayout frame;
	static FeedAdapter adapter;
	static FadingActionBarHelperHome2 mFadingHelper;
	static Context cntxt;
	static int scrollPosition;
	static int topOffset;
	
	//static List<Article> feedList;
	//static Article[] NewsFeed;
	
	static int currentSlide;
	static Timer timer;
	Handler timeHandler;
	long startTime;
	
	static Fragment fragSlide;
	static FragmentManager fm;
	static Integer currentSlideID;
	
	Typeface fontRegular;
	Typeface fontBold;
	Typeface fontLight;
	
	public static FragmentHome newInstance(int lastScrollPosition, int lastTopOffset)
	{
		FragmentHome f = new FragmentHome();
		Bundle bdl = new Bundle();
		
		bdl.putInt("Last Scroll Position", lastScrollPosition);
		bdl.putInt("Last Top Offset", lastTopOffset);
		f.setArguments(bdl);
		
		return f;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = mFadingHelper.createView(inflater);
		
		cntxt = getActivity();
		//feedList = new ArrayList<Article>();
		list = (ListView) view.findViewById(android.R.id.list);
		
		adapter = new FeedAdapter(getActivity(), ActivityMain.articleList);
		list.setAdapter(adapter);
		currentSlide = 1;
		scrollPosition = 0;
		topOffset = 0;
		
		Bundle args = getArguments();
		if(args != null)
		{
			scrollPosition = args.getInt("Last Scroll Position", 0);
			topOffset = args.getInt("Last Top Offset", 0);
		}
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		fm = getChildFragmentManager();
		//NewsFeed = ActivityMain.getFeed();
		fontRegular = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Regular.ttf");
		fontBold = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Bold.ttf");
		fontLight = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Light.ttf");
		
		timer = new Timer();
		timeHandler = new Handler(new Callback()
		{
			@Override
			public boolean handleMessage(Message msg)
			{
				if (mFadingHelper.mLastScrollPosition < 10 && ActivityMain.articleList != null)
					populateSlide();
				
				return false;
			}
		});
		timer.schedule(new firstTask(), 5000, 7000);
		
		populateSlide();
		updateState();
		
		list.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long index)
			{
				if(position > 0)
				{
					int ID = ActivityMain.articleList.get(position - 1).getID();
					String Title = ActivityMain.articleList.get(ID).getTitle();
					String Description = ActivityMain.articleList.get(ID).getDescription();
					String Content = ActivityMain.articleList.get(ID).getContent();
					String CommentFeed = ActivityMain.articleList.get(ID).getCommentFeed();
					String Author = ActivityMain.articleList.get(ID).getAuthor();
					String Date = ActivityMain.articleList.get(ID).getDate();
					String Category = ActivityMain.articleList.get(ID).getCategory();
					String Image = ActivityMain.articleList.get(ID).getImage();
					String URL = ActivityMain.articleList.get(ID).getURL();
					boolean Favorite = ActivityMain.articleList.get(ID).isFavorite();
					boolean Read = ActivityMain.articleList.get(ID).isRead();
					
					Article Article = new Article(ID, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Favorite, Read);
					ActivityMain.callArticle(getActivity(), Article, list.getFirstVisiblePosition(), (list.getChildAt(0) == null) ? 0 : list.getChildAt(0).getTop());
				}
				else if (currentSlideID != null)
				{
					int ID = ActivityMain.articleList.get(currentSlideID).getID();
					String Title = ActivityMain.articleList.get(currentSlideID).getTitle();
					String Description = ActivityMain.articleList.get(currentSlideID).getDescription();
					String Content = ActivityMain.articleList.get(currentSlideID).getContent();
					String CommentFeed = ActivityMain.articleList.get(currentSlideID).getCommentFeed();
					String Author = ActivityMain.articleList.get(currentSlideID).getAuthor();
					String Date = ActivityMain.articleList.get(currentSlideID).getDate();
					String Category = ActivityMain.articleList.get(currentSlideID).getCategory();
					String Image = ActivityMain.articleList.get(currentSlideID).getImage();
					String URL = ActivityMain.articleList.get(currentSlideID).getURL();
					boolean Favorite = ActivityMain.articleList.get(currentSlideID).isFavorite();
					boolean Read = ActivityMain.articleList.get(currentSlideID).isRead();
					
					Article Article = new Article(ID, Title, Description, Content, CommentFeed, Author, Date, Category, Image, URL, Favorite, Read);
					ActivityMain.callArticle(getActivity(), Article, 0, 0);
				}
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
		//NewsFeed = ActivityMain.getFeed();
		
		if (ActivityMain.articleList == null)
		{
			//feedList.clear();
		}
		else
		{
			Log.v("Guess what!", "VAGINA!!!!");
			//feedList = new ArrayList<Feed>();
			//feedList.clear();
			
			//for (int x = 0; x < NewsFeed.length; x++)
			//	feedList.add(new Article(NewsFeed[x].getID(), NewsFeed[x].getTitle(), NewsFeed[x].getDescription(), NewsFeed[x].getContent(), NewsFeed[x].getCommentFeed(), NewsFeed[x].getAuthor(), NewsFeed[x].getDate(), NewsFeed[x].getCategory(), NewsFeed[x].getImage(), NewsFeed[x].getURL(), NewsFeed[x].isFavorite(), NewsFeed[x].isRead()));
			
			adapter.notifyDataSetChanged();
			list.setSelectionFromTop(scrollPosition, topOffset);
			populateSlide();
		}
	}
	
	public static void populateSlide()
	{
		String sTitle = "";
		String sAuthor = "";
		String sDate = "";
		String sImage = "";
		String sCategory = "";
		
		if (ActivityMain.articleList != null)
		{
			Random generator = new Random();
			int r = generator.nextInt(ActivityMain.articleList.size());
			sTitle = ActivityMain.articleList.get(r).getTitle();
			sAuthor = ActivityMain.articleList.get(r).getAuthor();
			sDate = Data.parseDate(cntxt, ActivityMain.articleList.get(r).getDate());
			sImage = ActivityMain.articleList.get(r).getImage();
			sCategory = ActivityMain.articleList.get(r).getCategory();
			currentSlideID = ActivityMain.articleList.get(r).getID();
			
			if(sImage.length() < 1)
			{
				populateSlide();
				return;
			}
		}
		
		if (!sCategory.equalsIgnoreCase("DAILY SAVER"))
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
		
		private List<Article> listItem;
		
		public FeedAdapter(Context context, List<Article> listItem)
		{
			this.context = context;
			this.listItem = listItem;
		}
		
		public int getCount()
		{
			return listItem.size();
		}
		
		public Article getItem(int position)
		{
			return listItem.get(position);
		}
		
		public long getItemId(int position)
		{
			return position;
		}
		
		public View getView(int position, View view, ViewGroup viewGroup)
		{
			final ViewHolder holder;
			Article entry = listItem.get(position);
			if (view == null)
			{
				LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inflater.inflate(R.layout.feed_item, null);
				
				holder = new ViewHolder();
				holder.Card = (LinearLayout) view.findViewById(R.id.Card);
				holder.lblUnread = view.findViewById(R.id.lblUnread);
				holder.drkRead = (FrameLayout) view.findViewById(R.id.drkRead);
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
			holder.txtDate.setText(Data.parseDate(context, entry.getDate()));
			holder.txtCategory.setText(entry.getCategory());
			
			if (entry.getImage().length() > 0)
				Picasso.with(context).load(entry.getImage()).error(R.drawable.loading_image_error).fit().skipCache().into(holder.imgPreview);
			else
				holder.imgPreview.setVisibility(View.GONE);
			
			holder.imgPreview.setAdjustViewBounds(false);
			
			if (entry.isRead())
			{
				holder.lblUnread.setVisibility(View.INVISIBLE);
				if (entry.getImage().length() > 0)
					holder.drkRead.setForeground(new ColorDrawable(context.getResources().getColor(R.color.black_trans)));
			}
			else
			{
				holder.lblUnread.setVisibility(View.VISIBLE);
				holder.drkRead.setForeground(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
			}
			
			Animation cardAnimation = AnimationUtils.loadAnimation(context, R.anim.card_anim_list);
			holder.Card.startAnimation(cardAnimation);
			
			return view;
		}
	}
	
	private static class ViewHolder
	{
		public LinearLayout Card;
		public View lblUnread;
		public FrameLayout drkRead;
		public TextView txtTitle;
		public TextView txtDescription;
		public TextView txtAuthor;
		public TextView txtDate;
		public TextView txtCategory;
		public ImageView imgPreview;
		
	}
}
