package com.geofoxapp.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import com.google.android.maps.GeoPoint;

public class FoxPlace {

	public boolean bigImagesLoaded;
	public boolean smallImagesLoaded;
	
	public String address;
	public double avg_rating;
	public String city;
	public double distance_from_request;
	public String id;
	public boolean is_closed;
	public double lat;
	public double lon;
	public String name;
	
	public String nearby_url;
	public String phoneNum;
	
	public String photo_small_url;
	public String photo_url;
	public String rating_img_url;
	
	public Bitmap photo_small;
	public Bitmap photo;
	public Bitmap rating_img;
	
	
	public int review_count;
	public int checkin_count;
	public String state;
	public String place_url;
	public int zip_code;
	public String mobile_url;
	
	public GeoPoint point;
	
	public FoxPlace()
	{
		
	}
	
	@Override
	public String toString()
	{
		return name;
	}
	
	public FoxPlace(JSONObject obj, boolean loadSmallImages, boolean loadBigImages) throws FoxServerException
	//Structured as below
	{
		bigImagesLoaded = loadBigImages;
		smallImagesLoaded = loadSmallImages;
		
		try
		{
			
			String bigaddress = obj.getString("address1");
			
			String adr2 = obj.getString("address2").trim();
			String adr3 = obj.getString("address3").trim();
			
			if(adr2.length() > 0)
				bigaddress += '\n' + adr2 ;
			
			
			if(adr3.length() > 0)
				bigaddress += '\n' + adr3;
			
			
			address = bigaddress;
			
			avg_rating = obj.getDouble("avg_rating");
			city = obj.getString("city");
			distance_from_request = obj.getDouble("distance");
			id = obj.getString("id");
			lat = obj.getDouble("latitude");
			lon = obj.getDouble("longitude");
			point = new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6));
			name = obj.getString("name");
			
			nearby_url = obj.getString("nearby_url");
			phoneNum = obj.getString("phone");
			
			photo_small_url = obj.getString("photo_url_small");
			photo_url = obj.getString("photo_url");
			rating_img_url = obj.getString("rating_img_url");
			
			if(loadSmallImages)
			{	
				photo_small = getBitmapFromURL(photo_small_url);
				rating_img = getBitmapFromURL(rating_img_url);
			}
			if (loadBigImages)
			{
				photo = getBitmapFromURL(photo_url);
			}
			
			review_count = obj.getInt("review_count");
			checkin_count = obj.getInt("checkin_count");
			state = obj.getString("state");
			place_url = obj.getString("url");
			zip_code = obj.getInt("zip");
			mobile_url = obj.getString("mobile_url");
		}
		catch (JSONException e)
		{
			throw new FoxServerException(999, "JSON exception parsing FoxPlace");
		}
	}
	
	public void loadSmallImageBitmaps()
	{
		if(smallImagesLoaded)
			return;
		

		photo_small = getBitmapFromURL(photo_small_url);
		rating_img = getBitmapFromURL(rating_img_url);
		smallImagesLoaded = true;
		
	}
	
	public void loadBigImageBitmaps()
	{
		if(bigImagesLoaded)
			return;
		

		photo = getBitmapFromURL(photo_url);
		bigImagesLoaded = true;
		
	}
	
	
	
	
	private Bitmap getBitmapFromURL(String url)
	{
		InputStream is;
		URL picURL;
		Bitmap imageBitmap = null;
		try {
			//Try this just to warm up the dns so we get no UnknownHostException
		    try {
		        InetAddress i = InetAddress.getByName(url);
		      } catch (UnknownHostException e1) {
		        
		      }

		      
        	//Form the URL from a string
			picURL = new URL(url);    		
			//Use HttpUrlConnection to download the image data 
			HttpURLConnection con = (HttpURLConnection)picURL.openConnection();
			con.setDoInput(true);	//Yes, we want to send input on the connection
			con.connect();
            is = con.getInputStream();	//read the image data
            imageBitmap = BitmapFactory.decodeStream(is); //decode it to a bitmap
            is.close();
            is = null;
		} catch (MalformedURLException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
        return imageBitmap;

	}
}


/*
{'businesses': [{'address1': '466 Haight St', 
	'address2': '', 
	'address3': '', 
	'avg_rating': 4.0, 
	'categories': 
		[{'category_filter': 'danceclubs', 
		'name': 'Dance Clubs', 
		'search_url': 'http://yelp.com/search?find_loc=466+Haight+St%2C+San+Francisco%2C+CA&cflt=danceclubs'}, 
		{'category_filter': 'lounges', 
		'name': 'Lounges', 
		'search_url': 'http://yelp.com/search?find_loc=466+Haight+St%2C+San+Francisco%2C+CA&cflt=lounges'}, 
		{'category_filter': 'tradamerican', 
		'name': 'American (Traditional)', 
		'search_url': 'http://yelp.com/search?find_loc=466+Haight+St%2C+San+Francisco%2C+CA&cflt=tradamerican'}], 
	'city': 'San Francisco', 
	'distance': 1.8780401945114136, 
	'id': 'yyqwqfgn1ZmbQYNbl7s5sQ', 
	'is_closed': False, 
	'latitude': 37.772201000000003, 
	'longitude': -122.42992599999999, 
	'mobile_url': 'http://mobile.yelp.com/biz/yyqwqfgn1ZmbQYNbl7s5sQ', 
	'name': 'Nickies', 
	'nearby_url': 'http://yelp.com/search?find_loc=466+Haight+St%2C+San+Francisco%2C+CA', 
	'neighborhoods': [{'name': 'Hayes Valley', 
						'url': 'http://yelp.com/search?find_loc=Hayes+Valley%2C+San+Francisco%2C+CA'}], 
	'phone': '4152550300', 
	'photo_url': 'http://static.px.yelp.com/bpthumb/mPNTiQm5HVqLLcUi8XrDiA/ms', 
	'photo_url_small': 'http://static.px.yelp.com/bpthumb/mPNTiQm5HVqLLcUi8XrDiA/ss', 
	'rating_img_url': 'http://static.px.yelp.com/static/20070816/i/ico/stars/stars_4.png', 
	'rating_img_url_small': 'http://static.px.yelp.com/static/20070816/i/ico/stars/stars_small_4.png', 
	'review_count': 32, 
	'reviews': [{'id': 't-sisM24K9GvvYhr-9w1EQ', 
		'rating': 3, 
		'rating_img_url': 'http://static.px.yelp.com/static/20070816/i/ico/stars/stars_3.png',
		'rating_img_url_small': 'http://static.px.yelp.com/static/20070816/i/ico/stars/stars_small_3.png', 
		'text_excerpt': 'So I know gentrification is supposed to be a bad word and all (especially here in SF), but the Lower Haight 
			might benefit a bit from it. At least, I like...', 
		'url': 'http://yelp.com/biz/yyqwqfgn1ZmbQYNbl7s5sQ#hrid:t-sisM24K9GvvYhr-9w1EQ', 
		'user_name': 'Trey F.', 
		'user_photo_url': 'http://static.px.yelp.com/upthumb/ZQDXkIwQmgfAcazw8OgK2g/ms', 
		'user_photo_url_small': 'http://static.px.yelp.com/upthumb/ZQDXkIwQmgfAcazw8OgK2g/ss', 
		'mobile_uri': 'http://mobile.yelp.com/biz/yyqwqfgn1ZmbQYNbl7s5sQ?srid=t-sisM24K9GvvYhr-9w1EQ', 
		'user_url': 'http://yelp.com/user_details?userid=XMeRHjiLhA9cv3BsSOazCA'}, 
		{'id': '8xTNOC9L5ZXwGCMNYY-pdQ', 'rating': 4, 'rating_img_url': 'http://static.px.yelp.com/static/20070816/i/ico/stars/stars_4.png', 'rating_img_url_small': 'http://static.px.yelp.com/static/20070816/i/ico/stars/stars_small_4.png', 'text_excerpt': 'This place was definitely a great place to chill. The atmosphere is very non-threatening and very neighborly. I thought it was cool that they had a girl dj...', 'url': 'http://yelp.com/biz/yyqwqfgn1ZmbQYNbl7s5sQ#hrid:8xTNOC9L5ZXwGCMNYY-pdQ', 'user_name': 'Jessy M.', 'user_photo_url': 'http://static.px.yelp.com/upthumb/Ghwoq23_alkaXawgqj7dBA/ms', 'user_photo_url_small': 'http://static.px.yelp.com/upthumb/Ghwoq23_alkaXawgqj7dBA/ss', 'mobile_uri': 'http://mobile.yelp.com/biz/yyqwqfgn1ZmbQYNbl7s5sQ?srid=8xTNOC9L5ZXwGCMNYY-pdQ', 'user_url': 'http://yelp.com/user_details?userid=4F2QG3adYIUNXplqqp9ylA', 
		{'id': 'pp33WfN_FoKlQKJ-38j_Ag', 'rating': 5, 'rating_img_url': 'http://static.px.yelp.com/static/20070816/i/ico/stars/stars_5.png', 'rating_img_url_small': 'http://static.px.yelp.com/static/20070816/i/ico/stars/stars_small_5.png', 'text_excerpt': "Love this place! I've been here twice now and each time has been a great experience. The bartender is so nice. When we had questions about the drinks he...", 'url': 'http://yelp.com/biz/yyqwqfgn1ZmbQYNbl7s5sQ#hrid:pp33WfN_FoKlQKJ-38j_Ag', 'user_name': 'Scott M.', 'user_photo_url': 'http://static.px.yelp.com/upthumb/q0POOE3vv2LzNg1qN8MMyw/ms', 'user_photo_url_small': 'http://static.px.yelp.com/upthumb/q0POOE3vv2LzNg1qN8MMyw/ss', 'mobile_uri': 'http://mobile.yelp.com/biz/yyqwqfgn1ZmbQYNbl7s5sQ?srid=pp33WfN_FoKlQKJ-38j_Ag', 'user_url': 'http://yelp.com/user_details?userid=FmcKafW272uSWXbUF2rslA'}], 
		
	'state': 'CA', 
	'state_code': 'CA', 
	'country': 'USA', 
	'country_code': 'US', 
	'url': 'http://yelp.com/biz/nickies-san-francisco', 
	'zip': '94117'}
	],
	'message': {'code': 0, 'text': 'OK', 'version': '1.1.0'}} 
*/