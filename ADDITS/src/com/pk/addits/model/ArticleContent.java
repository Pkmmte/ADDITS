package com.pk.addits.model;

public class ArticleContent
{
	int Type;
	String Content;
	
	public ArticleContent()
	{
		this.Type = 0;
		this.Content = "";
	}
	
	public ArticleContent(int Type, String Content)
	{
		this.Type = Type;
		this.Content = Content;
	}
	
	public int getType()
	{
		return Type;
	}
	
	public String getContent()
	{
		return Content;
	}
}