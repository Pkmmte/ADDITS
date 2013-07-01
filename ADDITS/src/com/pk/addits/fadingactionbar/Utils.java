package com.pk.addits.fadingactionbar;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;

public class Utils
{
	public static int getDisplayHeight(Context context)
	{
		Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int displayHeight = size.y;
		return displayHeight;
	}
}
