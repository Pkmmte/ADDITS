package com.pk.addits;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

import com.pk.addits.FragmentHome.FeedItem;
import com.pk.addits.FragmentHome.SlideItem;

public class Data
{
	public static int getHeightByPercent(Context context, double percent)
	{
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = (int) (size.y * percent);
		return height;
	}
	
	public static SlideItem[] generateDummySlides()
	{
		SlideItem[] Slides = new SlideItem[9];
		Slides[0] = new SlideItem("Dummy Title", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[1] = new SlideItem("Dummy Title 2 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[2] = new SlideItem("Dummy Title 3 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[3] = new SlideItem("Dummy Title4 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[4] = new SlideItem("Dummy Title5 ", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[5] = new SlideItem("Dummy Title 6", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[6] = new SlideItem("Dummy Title 7", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[7] = new SlideItem("Dummy Title 8", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		Slides[8] = new SlideItem("Dummy Title 9", "Dummy Sub", "Meh", "http://addits.androiddissected.com/");
		
		return Slides;
	}
	
	public static FeedItem[] generateDummyFeed()
	{
		FeedItem[] Feeeeedz = new FeedItem[7];
		Feeeeedz[0] = new FeedItem("Dumb Title", "Blah blah blah blah blah blajsaasdsdasdasdas", "Cliff Wade", "June 22", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/");
		Feeeeedz[1] = new FeedItem("Dumber Title", "Lorem ipsum stuff", "Cliff Wade", "June 22", "GAME REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/ManOfSteelHeader.jpg", "http://addits.androiddissected.com/2013/06/22/man-of-steel-i-loved-the-movie-can-the-android-game-match-it/");
		Feeeeedz[2] = new FeedItem("Retard Title", "sdfsdfiounwsdei3wne iwnr f dsfdsdasdasdas", "Cliff Wade", "June 22", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/");
		Feeeeedz[3] = new FeedItem("Dummy Title", "Blah blah blah blah blah", "Cliff Wade", "June 22", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/");
		Feeeeedz[4] = new FeedItem("Smart Title", "Insert something smart here", "Roberto Mezquia Jr", "June 22", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/");
		Feeeeedz[5] = new FeedItem("titllle", "A preview of your article will appear here", "Cliff Wade", "June 22", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/");
		Feeeeedz[6] = new FeedItem("Title", "Description", "Cliff Wade", "June 22", "APP REVIEWS", "http://addits.androiddissected.com/wp-content/uploads/2013/06/Filmgrain.png", "http://addits.androiddissected.com/2013/06/22/meet-my-new-movie-companion-filmgrain/");
		
		return Feeeeedz;
	}
}
