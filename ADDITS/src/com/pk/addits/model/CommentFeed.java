package com.pk.addits.model;

public class CommentFeed
{
	String Creator;
	String Content;
	String Date;
	
	public CommentFeed(String Creator, String Content, String Date)
	{
		this.Creator = Creator;
		this.Content = Content;
		this.Date = Date;
	}
	
	public String getCreator()
	{
		return Creator;
	}
	
	public String getContent()
	{
		return Content;
	}
	
	public String getDate()
	{
		return Date;
	}
}