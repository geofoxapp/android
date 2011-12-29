package com.geofoxapp.android;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.android.maps.MapView;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.LauncherActivity.ListItem;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class FoxFindActivity extends ActivityGroup {
    
	
	private static final int CATEGORY_VIEW = 1;
	private static final int PLACES_VIEW = 2;
	private static final int PLACE_INFO_VIEW = 3;
	private static final int MAP_VIEW = 4;
	
	private int current_view; 
	
	
	private FoxCategoryPlaces selected_category;
	private ArrayList<FoxCategoryPlaces> all_categories;
	private Location lastLocation;

	//public ArrayList<FoxPlace> nearbyPlaces;
	private PopulateNeighborhoodTask  hoodTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
	      super.onCreate(savedInstanceState);

//	      TextView textview = new TextView(this);
//	      textview.setText("Loading...");
//	      setContentView(textview);

	      lastLocation = ((FoxMainActivity)getParent()).getCurrentLocation();

	      current_view = 0;
	      selected_category = null;
	      all_categories = new ArrayList<FoxCategoryPlaces>();
	      
	      GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
	      
	      if (appState.isUserLoggedIn())
	    	  startFindNeighborhood();
	      else	
	    	  createFindLoginPopup();

	}
	
	public void startFindNeighborhood()
	{
		hoodTask = new PopulateNeighborhoodTask((float)lastLocation.getLatitude(), (float)lastLocation.getLongitude(), this);
		hoodTask.execute();
	}
	
	public void setPlaceInfoView(FoxPlace place)
	{
		place.loadBigImageBitmaps();
		
		setContentView(R.layout.placeview);

		TextView tview = (TextView) findViewById(R.id.pv_name);
		tview.setText(place.name);
		tview = (TextView) findViewById(R.id.pv_address);
		tview.setText(place.address + "\n" + place.city + ", " + place.state + ' ' + place.zip_code);
		tview = (TextView) findViewById(R.id.pv_phone);
		tview.setText(PhoneNumberUtils.formatNumber(place.phoneNum.trim()));
		
		tview = (TextView) findViewById(R.id.pv_numvisits);
		tview.setText(""+place.checkin_count);
		
		tview = (TextView) findViewById(R.id.pv_rating_count);
		tview.setText("(" + place.review_count + ")");
		
		tview = (TextView) findViewById(R.id.pv_distanceAway);
		tview.setText(DecimalFormat.getInstance().format(place.distance_from_request));
		
		Button btn = (Button) findViewById(R.id.pv_yelpreviewlink);
		final String finalurl = place.mobile_url;
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(finalurl));
				startActivity(i);

			}
		});	
		ImageButton imgbtn = (ImageButton) findViewById(R.id.clicktocallbtn);
		final String finalnum = place.phoneNum;
		imgbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent i = new Intent(Intent.ACTION_CALL);
				i.setData(Uri.parse("tel:+" + finalnum));
				startActivity(i);

			}
		});
		
		
		ImageView pic = (ImageView) findViewById(R.id.pv_stars);
		pic.setImageBitmap(place.rating_img);

		pic = (ImageView) findViewById(R.id.pv_picture);
		pic.setImageBitmap(place.photo);

	}
	
	public void setPlacesView(FoxCategoryPlaces cat)
	{

		
		ProgressDialog dialog = ProgressDialog.show(FoxFindActivity.this, "", 
                "Loading. Please wait...", true);
		
		cat.loadSmallImages();
		
		setContentView(R.layout.yelplist);
		
		final ArrayList<FoxPlace> places = cat.getPlaces();
		
		
		
		ListView lview = (ListView) findViewById(R.id.lview);
		lview.setAdapter(new FoxPlaceAdapter(this,
				R.layout.placerow,
				places));
		lview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		            int position, long id) {
		    		
		    	FoxPlace place = (FoxPlace) parent.getItemAtPosition(position);

		    	setPlaceInfoView(place);
		    	current_view = PLACE_INFO_VIEW;
		    	
		        }
		      });
		
		TextView tview = (TextView) findViewById(R.id.yl_title);
		tview.setText(cat.toString());
		tview.setTextSize(16);
		
		Button switchbtb = (Button) findViewById(R.id.yl_switchbtn);

		switchbtb.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), FoxMapView.class);
				replaceContentView("Map Activity", intent);
				((FoxMapView)getLocalActivityManager().getCurrentActivity()).setOverlays(places);
				
			}
		});	
		
		dialog.dismiss();
	}
	
	public void replaceContentView(String id, Intent newIntent)
	{
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); 
		this.setContentView(view);
		current_view = MAP_VIEW;
	}
	
	public void setCatView(ArrayList<FoxCategoryPlaces> hoodmap)
	{
		
		ListView lview = new ListView(this);

		lview.setAdapter(new ArrayAdapter<FoxCategoryPlaces>(this,
				android.R.layout.simple_list_item_1,
				hoodmap));
		
		lview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		            int position, long id) {
		    		
		    	FoxCategoryPlaces category = (FoxCategoryPlaces) parent.getItemAtPosition(position);
		    	
		    	setPlacesView(category);
		    	
		    	current_view = PLACES_VIEW;
		    	selected_category = category;
		    	
		        }
		      });
		
        setContentView(lview);
        
        current_view = CATEGORY_VIEW;
        all_categories = hoodmap;
        
		
	}

	@Override
	public void onBackPressed()
	{
		if(hoodTask.getStatus() == AsyncTask.Status.RUNNING)
		{
			hoodTask.cancel(true);
			super.onBackPressed();
			return;
		}
		
		switch(current_view)
		{
			case CATEGORY_VIEW:
				super.onBackPressed();
			break;
			case PLACES_VIEW:
				setCatView(all_categories);
			break;
			case PLACE_INFO_VIEW:
				current_view = PLACES_VIEW;
				setPlacesView(selected_category);				
			break;
			case MAP_VIEW:
				current_view = PLACES_VIEW;
				setPlacesView(selected_category);
			break;
			default:
				super.onBackPressed();
			break;
		}
	}
	
    private class PopulateNeighborhoodTask extends AsyncTask<Void, Void, ArrayList<FoxCategoryPlaces> > 
    {

    	private float lat;
    	private float lon;
    	private FoxFindActivity findactivity;
    	private ProgressDialog dialog;

    	public PopulateNeighborhoodTask(float lat_, float lon_, FoxFindActivity findactivity_)
    	{
    		lat = lat_;
    		lon = lon_;
    		findactivity = findactivity_;
    	}

    	@Override
    	protected void onPreExecute() {
            dialog = new ProgressDialog(findactivity);
            dialog.setMessage("Loading Neighboorhood...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

    	}

    	// automatically done on worker thread (separate from UI thread)
    	@Override
    	protected ArrayList<FoxCategoryPlaces> doInBackground(final Void... unused) {
    		ArrayList<FoxCategoryPlaces> nearbyCategories = new ArrayList<FoxCategoryPlaces>();
    		try
    		{
    			GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
    			nearbyCategories = appState.fsm.placeSearchNeighborhood(lat, lon);
    		}
    		catch(FoxServerException e)
    		{
    			Log.v("FindActivity","Caught exception!");
    		}
    		return nearbyCategories;
    	}

    	// can use UI thread here
    	@Override
    	protected void onPostExecute(ArrayList<FoxCategoryPlaces> result) {
    		if (dialog.isShowing()) {
    			dialog.dismiss();
    		}
    		findactivity.setCatView(result);
    	}
    }
	
    
    protected void createFindLoginPopup() 
    {
		LayoutInflater infalter = LayoutInflater.from(this);
	    final View textEntryView = infalter.inflate(R.layout.logindialog, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Please log in...");
    	dialogBuilder.setView(textEntryView);
     	dialogBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener(){
     		public void onClick(DialogInterface dialog, int which) 
			{    			
     			String email = ((EditText) textEntryView.findViewById(R.id.ALERT_DIALOG_EMAIL_TEXT)).getText().toString();
     			String password = ((EditText) textEntryView.findViewById(R.id.ALERT_DIALOG_PASS_TEXT)).getText().toString();
     			GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());

     			if (appState.setLoginInfo(email, password))
     				startFindNeighborhood();
     			else
     				createFindLoginPopup();
			}
		}); 
		dialogBuilder.setNegativeButton("Cancel", null);
		Dialog login = dialogBuilder.create();
		login.show();
	}
    
	public void onResume()
	{
		super.onResume();		
	}
	
}
