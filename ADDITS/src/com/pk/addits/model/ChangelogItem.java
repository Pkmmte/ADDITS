package com.pk.addits.model;

public class ChangelogItem
{
	String Build;
	String Date;
	String Log;
	
	public ChangelogItem(String Build, String Date, String Log)
	{
		this.Build = Build;
		this.Date = Date;
		this.Log = Log;
	}
	
	public String getBuild()
	{
		return Build;
	}
	
	public String getDate()
	{
		return Date;
	}
	
	public String getLog()
	{
		return Log;
	}
}