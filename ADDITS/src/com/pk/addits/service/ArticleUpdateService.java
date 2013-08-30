package com.pk.addits.service;

import java.util.List;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.util.XmlDom;
import com.pk.addits.data.Data;
import com.pk.addits.data.DatabaseHelper;
import com.pk.addits.model.Article;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.Html;
import android.util.Log;

/*
 * This service runs in its own thread process
 * and grabs the feed asynchronously. The service
 * starts when the application starts and can be
 * set to start at device boot. Being that the 
 * service starts at device boot and application start.
 * There is no need to have all the other update functionality.
 * All article updating for the app should be based on this service
 * moving forward in development.
*/
public class ArticleUpdateService extends Service {
	
	private AQuery aq;

	public ArticleUpdateService() {
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.v("Update Service.", "Service is fully opperational.");
		aq = new AQuery(getApplicationContext());
		aq.ajax(Data.FEED_URL, XmlDom.class, this, "saveFeed");
	  return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings({ "null", "unused" })
	private void saveFeed(String url, XmlDom xml, AjaxStatus status) {
		DatabaseHelper db = DatabaseHelper.getInstance(this);
		int precount = db.getArticleCount();
		int postCount;
		List<XmlDom> entries = xml.tags("item");
		for (XmlDom item : entries) { 
			db.addArticle(new Article((Integer) null, item.text("title"), 
					Html.fromHtml(item.text("description")).toString(),
					item.text("content:encoded"), item.text("wfw:commentRss"),
					item.text("dc:creator"), item.text("pubDate"),
					item.text("category"), Data.pullLinks(item.text("description")),
					item.text("link"), false, false));
		}
		postCount = db.getArticleCount();
		if (postCount > precount) {
			// TODO: Fire off notification here!
			
			// Im still working out the notification functionality.
		}
	}

}
