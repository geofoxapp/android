package com.geofoxapp.android;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;



public class FoxHistoryObject {
	
//    checkin_history                         {(S)auth_key}
//    =>{[(I)checkin_id,(S)note,(I)timestamp,(S)place_id,(S)place_name]|null}

	public int checkin_id;
	public String note;
	public Date time;
	public String place_id;
	public String place_name;
	public FoxPlace place;
	
	public FoxHistoryObject(int checkin_id_, String note_, int timestamp_, String place_id_, String place_name_)
	{
		checkin_id = checkin_id_;
		note = note_;
		long timestamp = (long)timestamp_;
		time = new Date(timestamp * 1000);
		place_id = place_id_;
		place_name = place_name_;
	}
	
	
	public FoxHistoryObject(JSONObject obj) throws FoxServerException
	{
		try
		{
			checkin_id = obj.getInt("checkin_id");
			note = obj.getString("note");
			long timestamp = (long)obj.getInt("timestamp");
			time = new Date(timestamp * 1000);
			place_id = obj.getString("place_id");
			place_name = obj.getString("place_name");
			place = new FoxPlace(obj.getJSONObject("business"), true, false);
		}
		catch (JSONException e)
		{
			throw new FoxServerException(999, "JSON exception parsing FoxHistoryObject");
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		FoxHistoryObject otherhistobj = (FoxHistoryObject) other;
		
		if(otherhistobj.place_id.contentEquals(this.place_id))
			return true;
		else	
			return false;
		
	}
	
	@Override
	public int hashCode()
	{
		return place_id.hashCode();
	}
	
	@Override
	public String toString()
	{
		return place_name; 
	}
	
}
