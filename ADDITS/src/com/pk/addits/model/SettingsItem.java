package com.pk.addits.model;

public class SettingsItem
{
	String Name;
	String Description;
	String Type;
	String Value;
	String ValueType;
	
	public SettingsItem(String Name, String Description, String Type, String Value, String ValueType)
	{
		this.Name = Name;
		this.Description = Description;
		this.Type = Type;
		this.Value = Value;
		this.ValueType = ValueType;
	}
	
	public SettingsItem(String Type, String Value)
	{
		this.Name = "";
		this.Description = "";
		this.Type = Type;
		this.Value = Value;
		this.ValueType = "";
	}
	
	public String getName()
	{
		return Name;
	}
	
	public String getDescription()
	{
		return Description;
	}
	
	public String getType()
	{
		return Type;
	}
	
	public String getValue()
	{
		return Value;
	}
	
	public String getValueType()
	{
		return ValueType;
	}
}