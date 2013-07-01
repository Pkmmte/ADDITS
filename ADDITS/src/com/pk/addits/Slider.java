package com.pk.addits;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.pk.addits.FragmentHome.SlideItem;
import com.squareup.picasso.Picasso;

public class Slider extends Fragment
{
	int Slide;
	String Text;
	String SubText;
	String ImageURL;
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
	
	public static final Slider newInstance(SlideItem[] Slides, int slide)
	{
		Slider f = new Slider();
		Bundle bdl = new Bundle(5);
		
		bdl.putInt("Slide", slide);
		bdl.putString("Text", Slides[slide - 1].getText());
		bdl.putString("SubText", Slides[slide - 1].getSubText());
		bdl.putString("ImageURL", Slides[slide - 1].getImageURL());
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
		ImageURL = args.getString("ImageURL");
		URL = args.getString("URL");
	}

	@SuppressWarnings("deprecation")
	@TargetApi(16)
	public void setIndicator()
	{
		if (Build.VERSION.SDK_INT >= 16)
		{
			switch (Slide)
			{
				case 1:
					s1.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					s2.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s3.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s4.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s5.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					break;
				case 2:
					s1.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s2.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					s3.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s4.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s5.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					break;
				case 3:
					s1.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s2.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s3.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					s4.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s5.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					break;
				case 4:
					s1.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s2.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s3.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s4.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					s5.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					break;
				case 5:
					s1.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s2.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s3.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s4.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s5.setBackground(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					break;
				default:
					break;
			}
		}
		else
		{
			switch (Slide)
			{
				case 1:
					s1.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					s2.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s3.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s4.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s5.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					break;
				case 2:
					s1.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s2.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					s3.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s4.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s5.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					break;
				case 3:
					s1.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s2.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s3.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					s4.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s5.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					break;
				case 4:
					s1.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s2.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s3.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s4.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					s5.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					break;
				case 5:
					s1.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s2.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s3.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s4.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_off));
					s5.setBackgroundDrawable(getActivity().getResources().getDrawable(R.drawable.indicator_on));
					break;
				default:
					break;
			}
		}
	}
	
	public void setImage()
	{
		imgImage.setScaleType(ScaleType.FIT_XY);
		if (ImageURL.length() > 0)
			Picasso.with(getActivity()).load(ImageURL).error(R.drawable.no_image_banner).fit().into(imgImage);
		else
			Picasso.with(getActivity()).load(R.drawable.no_image_banner).fit().into(imgImage);
	}
}