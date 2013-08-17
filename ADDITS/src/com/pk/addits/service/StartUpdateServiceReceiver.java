package com.pk.addits.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StartUpdateServiceReceiver extends BroadcastReceiver {

	@Override
	  public void onReceive(Context context, Intent intent) {
	    Intent service = new Intent(context, ArticleUpdateService.class);
	    context.startService(service);
	  }

}
