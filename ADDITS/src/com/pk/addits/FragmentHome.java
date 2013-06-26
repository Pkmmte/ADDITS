package com.pk.addits;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koushikdutta.ion.Ion;
import com.manuelpeinado.fadingactionbar.FadingActionBarHelperHome;
import com.squareup.picasso.Picasso;

public class FragmentHome extends Fragment
{
	static View view;
	static ScrollGridView grid;
	static FrameLayout frame;
	static FadingActionBarHelperHome mFadingHelper;
	
	List<FeedItem> feedList = new ArrayList<FeedItem>();;
	FeedItem[] NewsFeed;
	
	static SlideItem[] Slides;
	static int currentSlide;
	static Timer timer;
	Handler timeHandler;
	long startTime;
	
	static Fragment fragSlide;
	static FragmentManager fm;
	static FragmentTransaction transaction;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = mFadingHelper.createView(inflater);
		
		grid = (ScrollGridView) view.findViewById(R.id.GridView);
		frame = (FrameLayout) view.findViewById(R.id.slideContent);
		frame.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, Data.getHeightByPercent(getActivity(), 0.4)));
		currentSlide = 1;
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		fm = getChildFragmentManager();
		NewsFeed = Data.generateDummyFeed();
		
		timer = new Timer();
		timeHandler = new Handler(new Callback()
		{
			@Override
			public boolean handleMessage(Message msg)
			{
				if (currentSlide == 9)
					currentSlide = 1;
				else
					currentSlide++;
				
				populateSlide();
				
				return false;
			}
		});
		
		// Dummy Data
		Slides = Data.generateDummySlides();
		populateSlide();
		
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/Android/data/com.pk.addits/slides.xml");
		startTime = System.currentTimeMillis();
		// if (dir.exists())
		timer.schedule(new firstTask(), 5000, 7000);
		
		for (int x = 0; x < NewsFeed.length; x++)
			feedList.add(new FeedItem(NewsFeed[x].getTitle(), NewsFeed[x].getDescription(), NewsFeed[x].getAuthor(), NewsFeed[x].getDate(), NewsFeed[x].getCategory(), NewsFeed[x].getImage(), NewsFeed[x].getURL()));
		
		FeedAdapter adapter = new FeedAdapter(getActivity(), feedList);
		grid.setAdapter(adapter);
		grid.setExpanded(true);
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
		
		mFadingHelper = new FadingActionBarHelperHome().actionBarBackground(R.drawable.ab_background).withContext(getActivity()).contentLayout(R.layout.fragment_home);
		mFadingHelper.initActionBar(activity);
	}
	
	@SuppressLint("Recycle")
	public static void populateSlide()
	{
		fragSlide = FragmentHomeSlider.newInstance(Slides, currentSlide);
		
		transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_out, R.anim.fade_in);
		transaction.replace(R.id.slideContent, fragSlide);
		transaction.commit();
	}
	
	public static void previousSlide()
	{
		timer.cancel();
		timer.purge();
		
		if (currentSlide == 1)
			currentSlide = 9;
		else
			currentSlide--;
		
		populateSlide();
	}
	
	public static void nextSlide()
	{
		timer.cancel();
		timer.purge();
		
		if (currentSlide == 9)
			currentSlide = 1;
		else
			currentSlide++;
		
		populateSlide();
	}
	
	// Tells handler to send a message
	class firstTask extends TimerTask
	{
		@Override
		public void run()
		{
			timeHandler.sendEmptyMessage(0);
		}
	};
	
	public static class FragmentHomeSlider extends Fragment
	{
		int Slide;
		String Text;
		String SubText;
		String URL;
		
		ImageView imgImage;
		TextView txtText;
		TextView txtSubText;
		LinearLayout Content;
		RelativeLayout btnPrevious;
		RelativeLayout btnNext;
		
		View s1;
		View s2;
		View s3;
		View s4;
		View s5;
		View s6;
		View s7;
		View s8;
		View s9;
		
		public static final FragmentHomeSlider newInstance(SlideItem[] Slides, int slide)
		{
			FragmentHomeSlider f = new FragmentHomeSlider();
			Bundle bdl = new Bundle(4);
			
			bdl.putInt("Slide", slide);
			bdl.putString("Text", Slides[slide - 1].getText());
			bdl.putString("SubText", Slides[slide - 1].getSubText());
			bdl.putString("URL", Slides[slide - 1].getURL());
			
			f.setArguments(bdl);
			return f;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View view = inflater.inflate(R.layout.fragment_home_slider, container, false);
			
			imgImage = (ImageView) view.findViewById(R.id.Image);
			txtText = (TextView) view.findViewById(R.id.txtText);
			txtSubText = (TextView) view.findViewById(R.id.txtSubText);
			Content = (LinearLayout) view.findViewById(R.id.Content);
			btnPrevious = (RelativeLayout) view.findViewById(R.id.btnPrevious);
			btnNext = (RelativeLayout) view.findViewById(R.id.btnNext);
			s1 = view.findViewById(R.id.slide1);
			s2 = view.findViewById(R.id.slide2);
			s3 = view.findViewById(R.id.slide3);
			s4 = view.findViewById(R.id.slide4);
			s5 = view.findViewById(R.id.slide5);
			s6 = view.findViewById(R.id.slide6);
			s7 = view.findViewById(R.id.slide7);
			s8 = view.findViewById(R.id.slide8);
			s9 = view.findViewById(R.id.slide9);
			
			return view;
		}
		
		@Override
		public void onStart()
		{
			super.onStart();
			
			retrieveData();
			
			txtText.setText(Text);
			txtSubText.setText(SubText);
			btnPrevious.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					FragmentHome.previousSlide();
				}
			});
			btnNext.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					FragmentHome.nextSlide();
				}
			});
			Content.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					Intent i = new Intent(Intent.ACTION_VIEW);
					i.setData(Uri.parse(URL));
					getActivity().startActivity(i);
				}
			});
			
			setIndicator();
			setImage();
		}
		
		public void retrieveData()
		{
			Bundle args = getArguments();
			Slide = args.getInt("Slide");
			Text = args.getString("Text");
			SubText = args.getString("SubText");
			URL = args.getString("URL");
		}
		
		public void setIndicator()
		{
			switch (Slide)
			{
				case 1:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					break;
				case 2:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					break;
				case 3:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					break;
				case 4:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					break;
				case 5:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					break;
				case 6:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					break;
				case 7:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					break;
				case 8:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					break;
				case 9:
					s1.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s2.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s3.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s4.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s5.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s6.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s7.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s8.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light_transparent));
					s9.setBackgroundColor(getActivity().getResources().getColor(R.color.holo_blue_light));
					break;
				default:
					break;
			}
		}
		
		public void setImage()
		{
			// TODO Add Image Later...
		}
	}
	
	public class FeedAdapter extends BaseAdapter
	{
		private Context context;
		
		private List<FeedItem> listItem;
		
		public FeedAdapter(Context context, List<FeedItem> listItem)
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
			FeedItem entry = listItem.get(position);
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

			//holder.imgPreview.setScaleType(ScaleType.FIT_XY);
			//Picasso.with(context).load(entry.getImage()).fit().into(holder.imgPreview);
			//Ion.with(context, entry.getImage()).withBitmap().intoImageView(holder.imgPreview);
			Ion.with(holder.imgPreview).load(entry.getImage());
			
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
	
	public static class SlideItem
	{
		String Text;
		String SubText;
		String ImageURL;
		String URL;
		
		public SlideItem(String Text, String SubText, String ImageURL, String URL)
		{
			this.Text = Text;
			this.SubText = SubText;
			this.ImageURL = ImageURL;
			this.URL = URL;
		}
		
		public String getText()
		{
			return Text;
		}
		
		public String getSubText()
		{
			return SubText;
		}
		
		public String getImageURL()
		{
			return ImageURL;
		}
		
		public String getURL()
		{
			return URL;
		}
	}
	
	public static class FeedItem
	{
		String Title;
		String Description;
		String Author;
		String Date;
		String Category;
		String Image;
		String URL;
		
		public FeedItem(String Title, String Description, String Author, String Date, String Category, String Image, String URL)
		{
			this.Title = Title;
			this.Description = Description;
			this.Author = Author;
			this.Date = Date;
			this.Category = Category;
			this.Image = Image;
			this.URL = URL;
		}
		
		public String getTitle()
		{
			return Title;
		}
		
		public String getDescription()
		{
			return Description;
		}
		
		public String getAuthor()
		{
			return Author;
		}
		
		public String getDate()
		{
			return Date;
		}
		
		public String getCategory()
		{
			return Category;
		}
		
		public String getImage()
		{
			return Image;
		}
		
		public String getURL()
		{
			return URL;
		}
	}
}
