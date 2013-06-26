package com.pk.addits;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.manuelpeinado.fadingactionbar.FadingActionBarHelperHome;

public class FragmentHome extends Fragment
{
	static View view;
	static FadingActionBarHelperHome mFadingHelper;
	
	static SlideItem[] Slides;
	static int currentSlide;
	static Timer timer;
	Handler timeHandler;
	long startTime;
	
	static Fragment fragSlide;
	static FragmentManager fm;
	static FragmentTransaction transaction;
	
	// DEBUG
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		view = mFadingHelper.createView(inflater);
		
		// if (mArguments != null)
		// {
		// ImageView img = (ImageView) view.findViewById(R.id.image_header);
		// img.setImageResource(mArguments.getInt(ARG_IMAGE_RES));
		// }
		
		
		
		return view;
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		Toast.makeText(getActivity(), "TADAH!!", Toast.LENGTH_SHORT).show();
		fm = getChildFragmentManager();
		
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
		
		File sdCard = Environment.getExternalStorageDirectory();
		File dir = new File(sdCard.getAbsolutePath() + "/Android/data/com.pk.addits/slides.xml");
		startTime = System.currentTimeMillis();
		if (dir.exists())
			timer.schedule(new firstTask(), 5000, 7000);
		
		//Dummy Data
		Slides = new SlideItem[9];
		Slides[0] = new SlideItem("Dummy Title", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[1] = new SlideItem("Dummy Title 2 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[2] = new SlideItem("Dummy Title 3 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[3] = new SlideItem("Dummy Title4 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[4] = new SlideItem("Dummy Title5 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[5] = new SlideItem("Dummy Title 6", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[6] = new SlideItem("Dummy Title 7", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[7] = new SlideItem("Dummy Title 8", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[8] = new SlideItem("Dummy Title 9", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		currentSlide = 1;
		
		populateSlide();
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
		
		mFadingHelper = new FadingActionBarHelperHome()
		.actionBarBackground(R.drawable.ab_background)
		.withContext(getActivity())
		.contentLayout(R.layout.fragment_home);
		mFadingHelper.initActionBar(activity);
	}
	
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
}
