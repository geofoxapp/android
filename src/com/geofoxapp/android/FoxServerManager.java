package com.geofoxapp.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.maps.GeoPoint;

public class FoxServerManager {
	// Make XHR calls to the server
	// Contains specific callback methods
	// Throws custom Fox errors

	private static final String apiURL = "http://api.geofoxapp.com/";
	private String authkey;
	private String userEmail;
	private String userPassword;
	
	public FoxServerManager(String email, String password) {
		userEmail = email;
		userPassword = password;
	}
	
	public boolean setLoginInfo(String email, String pass) {
		
		String key = getAuthKey(email, pass);
		
		if (key != null && key != "")
		{
			userEmail = email;
			userPassword = pass;
			authkey = key;
			return true;
		}
		
		return false;
		
	}
	
	public boolean loginRefreshAuthKey()
	{
		String key = getAuthKey(userEmail, userPassword);
		
		if(key == null || key == "")
			return false;
			
		authkey = key;
		return true;		
	}

	private String getAuthKey(String email,String password)
	//Will return Null if the account is not valid
	{
		
		String url = apiURL + "?action=login&email=" + email + "&password=" + password;
		
		JSONObject json = new JSONObject();
		String key;
		
		try
		{
			json = sendServerRequest(url);
			key = json.getString("result");
		}
		catch (Throwable e)
		{
			return null;
		}
		
		return key;
	}

	private JSONObject sendServerRequest(String url) throws FoxServerException
	//Summary: connects to the url, returns the response object from the server as a JSONObject
	//Throws FoxServerException if there is a problem with the API code if an api related error or code 999 if code error
	{
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget;
		HttpEntity entity;
		HttpResponse response;
		
		try
		{
			//Prepare a request object
			httpget = new HttpGet(url);	
			//Execute the request
			response = httpclient.execute(httpget);
			
			if(response == null)
				throw new FoxServerException(999, "response was null from: " + url);

			entity = response.getEntity();

			if(entity == null)
				throw new FoxServerException(999, "response entity was null from: " + url);
			
			// A Simple JSON Response Read
			InputStream instream = entity.getContent();

			JSONObject object = new JSONObject(convertStreamToString(instream));

			Boolean err = object.getBoolean("error");
			if( err )
			{
				int errorNum = object.getInt("code");
				String msg = object.getString("message");
				throw new FoxServerException(errorNum, msg);
			}
			return object;
		}
		catch(JSONException json)
		{
			throw new FoxServerException(999, "json exception from: " + url);
		} 
		catch (ClientProtocolException e)
		{
			throw new FoxServerException(999, "HTTP exception from: " + url);
		} 
		catch (IOException e)
		{
			throw new FoxServerException(999, "IOException from: " + url);
		}
	}

	private static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the BufferedReader.readLine()
         * method. We iterate until the BufferedReader return null which means
         * there's no more data to read. Each line will appended to a StringBuilder
         * and returned as String.
         * Borrowed from web tutorial http://damonsk.com/2010/01/jsonarray-httpclient-android/
         */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

	public void createUser(String pass, String fname, String lname, String email, String phone) throws FoxServerException
	{
		//Create the user by sending form data

		String url = apiURL + "?action=user_create&password=" + pass + "&fname=" + fname + "&lname=" + lname+ "&email=" + email+ "&phone=" + phone;
		int id;
		JSONObject json = new JSONObject();
		
		try
		{
			json = sendServerRequest(url);
			//server returns: {(I)user_id|null}
			id = json.getInt("result");
			
			userEmail = email;
			userPassword = pass;
			authkey = getAuthKey(email, pass);

		}
		catch (FoxServerException e)
		{
			authkey = userEmail = userPassword = null;
			throw e;
		}
		catch (JSONException e)
		{
			authkey = userEmail = userPassword = null;
			throw new FoxServerException(999, "json exception from create user");
		}
	}

	public ArrayList<FoxCategoryPlaces> placeSearchNeighborhood(float lat, float lon) throws FoxServerException
	{
		ArrayList<FoxCategoryPlaces> hoodmap = new ArrayList<FoxCategoryPlaces>();
		
		String url = apiURL + "?action=place_search_neighborhood&auth_key=" + authkey + "&lat=" + lat + "&lon=" + lon;
		
		JSONObject json = new JSONObject();
		JSONObject jsoncats = new JSONObject();
		JSONArray jsoncatplaces = new JSONArray();
		
		try
		{
			json = sendServerRequest(url);
			jsoncats = json.getJSONObject("result");
			
			JSONArray catNames = new JSONArray();
			catNames = jsoncats.names();
			
			for(int i = 0; i < catNames.length(); i++)
			{
				String category = catNames.getString(i);
				hoodmap.add(new FoxCategoryPlaces(category, jsoncats.getJSONArray(category)));
			}
		}
		catch(JSONException e)
		{
			throw new FoxServerException(999, "json exception from place search neighborhood");
		}
		
		return hoodmap;
		
	}

	public ArrayList<FoxPlace> placeSearchNear(float lat, float lon) throws FoxServerException
	{
		// search for places near us
		// TODO: get the position from the main activity

		int radius;

		radius = 25;
		
		String url = apiURL + "?action=place_search_near" + "&auth_key=" + authkey + "&lat=" + lat+ "&lon=" + lon+ "&radius=" + radius;

		JSONObject json = new JSONObject();
		JSONArray jsonplaces = new JSONArray();
	
		ArrayList<FoxPlace> nearPlaces = new ArrayList<FoxPlace>();
		
		try
		{
			json = sendServerRequest(url);
			jsonplaces = json.getJSONArray("result");
			
			for(int i = 0; i < jsonplaces.length(); i++)
			{
				nearPlaces.add(new FoxPlace(jsonplaces.getJSONObject(i), true, false));
			}
		}
		catch(JSONException e)
		{
			throw new FoxServerException(999, "json exception from place search near");
		}
		
		return nearPlaces;
	}
     
	public FoxPlace placeGetDetails(String id) throws FoxServerException
	{
		// get business logic from a place id
		
		JSONObject jsonResponse = new JSONObject();
		JSONObject result = new JSONObject();
		FoxPlace place;
		
		try
		{
			String url = apiURL + "?action=place_get_details" + "&auth_key=" + authkey + "&place_id=" + id;

			jsonResponse = sendServerRequest(url);
			result = jsonResponse.getJSONObject("result");
			place = new FoxPlace(result, true, true); 
			return place;
		}	
		catch (JSONException e)
		{
			throw new FoxServerException(999, "json exception from placeGetDetails");
		}

	}
	
	public void addCheckin(String placeID, String note) throws FoxServerException
	{
		JSONObject jsonResponse = new JSONObject();
		int checkinID;
		
		try
		{
			String url = apiURL + "?action=checkin_add" + "&auth_key=" + authkey + "&place_id=" + placeID + "&note=" + note;

			jsonResponse = sendServerRequest(url);
			checkinID = jsonResponse.getInt("result");
			return;
		}	
		catch (JSONException e)
		{
			throw new FoxServerException(999, "json exception from addCheckin");
		}
	}
	
//    checkin_history                         {(S)auth_key}
//    =>{[(I)checkin_id,(S)note,(I)timestamp,(S)place_id,(S)place_name]|null}
    public ArrayList<FoxHistoryObject> getAllHistory() throws FoxServerException
    {
		String url = apiURL + "?action=checkin_history" + "&auth_key=" + authkey;

		JSONObject json = new JSONObject();
		JSONObject jsonresult = new JSONObject();
		JSONArray checkinIDarray = new JSONArray();
	
		ArrayList<FoxHistoryObject> history = new ArrayList<FoxHistoryObject>();
		
		try
		{
			json = sendServerRequest(url);
			jsonresult = json.getJSONObject("result");
			checkinIDarray = jsonresult.names();
			
			if(checkinIDarray == null)
				throw new FoxServerException(999, "no checkins available");
			
			for(int i = 0; i < checkinIDarray.length(); i++)
			{
				history.add(new FoxHistoryObject(jsonresult.getJSONObject(checkinIDarray.getString(i))));
			}
		}
		catch(JSONException e)
		{
			throw new FoxServerException(999, "json exception from place search near");
		}
		
		return history;
    }
	
	public ArrayList<FoxPlace> getRecs() throws FoxServerException
	{
		String url = apiURL + "?action=rec_get_all" + "&auth_key=" + authkey;
		
		JSONObject json = new JSONObject();
		JSONArray jsonplaces = new JSONArray();
	
		ArrayList<FoxPlace> recPlaces = new ArrayList<FoxPlace>();
		
		try
		{
			json = sendServerRequest(url);
			jsonplaces = json.getJSONArray("result");
			
			for(int i = 0; i < jsonplaces.length(); i++)
			{
				recPlaces.add(new FoxPlace(jsonplaces.getJSONObject(i), true, false));
			}
		}
		catch(JSONException e)
		{
			throw new FoxServerException(999, "json exception from get recomendations");
		}
		
		return recPlaces;
	}
	
	
//    place_more_like                         {(S)auth_key,(S)place_id}
//    =>{[][---business information---]|null}
	public ArrayList<FoxPlace> placeMoreLike(String placeid) throws FoxServerException
	{		
		String url = apiURL + "?action=place_more_like" + "&auth_key=" + authkey + "&place_id=" + placeid;

		JSONObject json = new JSONObject();
		JSONArray jsonplaces = new JSONArray();
	
		ArrayList<FoxPlace> likePlaces = new ArrayList<FoxPlace>();
		
		try
		{
			json = sendServerRequest(url);
			jsonplaces = json.getJSONArray("result");
			
			for(int i = 0; i < jsonplaces.length(); i++)
			{
				likePlaces.add(new FoxPlace(jsonplaces.getJSONObject(i), true, false));
			}
		}
		catch(JSONException e)
		{
			throw new FoxServerException(999, "json exception from place more like");
		}
		
		return likePlaces;
	}
	
	
//
//    place_more_near                         {(S)auth_key,(S)place_id}
//    =>{[][---business information---]|null}
	public ArrayList<FoxPlace> placeMoreNear(String placeid) throws FoxServerException
	{		
		String url = apiURL + "?action=place_search_near" + "&auth_key=" + authkey + "&place_id=" + placeid;

		JSONObject json = new JSONObject();
		JSONArray jsonplaces = new JSONArray();
	
		ArrayList<FoxPlace> nearPlaces = new ArrayList<FoxPlace>();
		
		try
		{
			json = sendServerRequest(url);
			jsonplaces = json.getJSONArray("result");
			
			for(int i = 0; i < jsonplaces.length(); i++)
			{
				nearPlaces.add(new FoxPlace(jsonplaces.getJSONObject(i), true, false));
			}
		}
		catch(JSONException e)
		{
			throw new FoxServerException(999, "json exception from place more near");
		}
		
		return nearPlaces;
	}  
	
//    checkin_remove                          {(S)auth_key,(I)checkin_id}
//    =>{(B)true|null}
	public boolean removeCheckin(int checkin_id) throws FoxServerException
	{
		String url = apiURL + "?action=checkin_remove" + "&auth_key=" + authkey + "&checkin_id=" + checkin_id;

		JSONObject json = new JSONObject();
		boolean removed = false;
		
		try
		{
			json = sendServerRequest(url);
			removed = json.getBoolean("result");
		}
		catch(JSONException e)
		{
			throw new FoxServerException(999, "json exception from checkin remove");
		}
		
		return removed;
	}
	
	
}
    
	/* API FUNCTIONS
	
	PUBLIC
        login                                           {(S)email,(S)password}
                                                                =>{(S)auth_key|(B)false}
                                                                
        user_create                                     {(S)password,(S)fname,(S)lname,(S)email,(S)phone}
                                                                =>{(I)user_id|null}
                                                                
        password_reset                          {(S)email}
                                                                =>{(B)true|null}

	SECURE
        user_details                            {(S)auth_key}
                                                                =>{(I)user_id,(S)fname,(S)lname,(S)email,(S)phone,(B)email_valid|null}
                                                                
        user_update                                     {(S)auth_key,(S)password,(S)fname,(S)lname,(S)email,(S)phone}
                                                                =>{(B)true|null}
                                                                
        email_verify                            {(S)auth_key}
                                                                =>{(B)true|null}
                                                                
        email_confirm                           {(S)auth_key,(S)code}
                                                                =>{(B)true|null}
        
        place_search_near                       {(S)auth_key,(F)lat,(F)lon}
                                                                =>{[][---business information---]|null}
        
        place_search_category           {(S)auth_key,(F)lat,(F)lon,(S)category}
                                                                =>{[][---business information---]|null}
        
        place_search_neighborhood       {(S)auth_key,(F)lat,(F)lon}
                                                                =>{[(S)category][---business information---]|null}
        
        place_get_details                       {(S)auth_key,(S)place_id}
                                                                =>{[---business information---]|null}
                                                                
        place_get_details_live          {(S)auth_key,(S)place_id}
                                                                =>{[---business information---]|null}
        
        place_more_like                         {(S)auth_key,(S)place_id}
                                                                =>{[][---business information---]|null}
        
        place_more_near                         {(S)auth_key,(S)place_id}
                                                                =>{[][---business information---]|null}
        
        place_categories_get_all        {(S)auth_key}
                                                                =>{[[(S)short][(S)long]]|null}
        
        place_categories_get_short      {(S)auth_key,(S)long}
                                                                =>{(S)short|null}
        
        place_categories_get_long       {(S)auth_key,(S)short}
                                                                =>{(S)long|null}
        
        checkin_add                                     {(S)auth_key,(S)place_id,(S)note}
                                                                =>{(I)checkin_id|null}
        
        checkin_remove                          {(S)auth_key,(I)checkin_id}
                                                                =>{(B)true|null}
        
        checkin_history                         {(S)auth_key}
                                                                =>{[(I)checkin_id,(S)note,(I)timestamp,(S)place_id,(S)place_name]|null}
        
        checkin_history_date            {(S)auth_key,(I)date_start,(I)date_end}
                                                                =>{[(I)checkin_id,(S)note,(I)timestamp,(S)place_id,(S)place_name]|null}
                                                                
        rec_get_all                                     {(S)auth_key}
                                                                =>{[][---business information---]|null}

 */
	
