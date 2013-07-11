package com.pk.addits;

public class Feed
{
	int ID;
	String Title;
	String Description;
	String Content;
	String CommentFeed;
	String Author;
	String Date;
	String Category;
	String Image;
	String URL;
	boolean Favorite;
	boolean Read;
	
	public Feed(int ID, String Title, String Description, String Content, String CommentFeed, String Author, String Date, String Category, String Image, String URL, boolean Favorite, boolean Read)
	{
		this.ID = ID;
		this.Title = Title;
		this.Description = Description;
		this.Content = Content;
		this.CommentFeed = CommentFeed;
		this.Author = Author;
		this.Date = Date;
		this.Category = Category;
		this.Image = Image;
		this.URL = URL;
		this.Favorite = Favorite;
		this.Read = Read;
	}
	
	public int getID()
	{
		return ID;
	}
	
	public String getTitle()
	{
		return Title;
	}
	
	public String getDescription()
	{
		return Description;
	}
	
	public String getContent()
	{
		return Content;
	}
	
	public String getCommentFeed()
	{
		return CommentFeed;
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
	
	public boolean isFavorite()
	{
		return Favorite;
	}
	
	public void setFavorite(boolean b)
	{
		Favorite = b;
	}
	
	public boolean isRead()
	{
		return Read;
	}
	
	public void setRead(boolean b)
	{
		Read = b;
	}
}