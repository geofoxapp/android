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


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FoxCheckinActivity extends Activity {
    
	private PopulateNearbyTask  nearbyTask;
	
	private ArrayList<FoxPlace> places;
	private FoxPlace CheckinPlace;
	private Location lastLocation;
	private boolean isCheckedIn;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

//		TextView textview = new TextView(this);
//		textview.setText("Loading...");
//		setContentView(textview);

		places = new ArrayList<FoxPlace>();
		CheckinPlace = null;
		lastLocation = ((FoxMainActivity)getParent()).getCurrentLocation();
		isCheckedIn = false;
    }
	
	
    
    @Override
	public void onResume()
    {
    	super.onResume();
    	
    	
		GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
		
		if (appState.isUserLoggedIn())
			startFindingNearby();
		else	
			createCheckinLoginPopup();
    }
	
	@Override
	public void onBackPressed()
	{
		if(isCheckedIn)
		{
			isCheckedIn = false;
			setCheckInView(0, false);
			return;
		}
		super.onBackPressed();
	}
	
	private void startFindingNearby()
	{
		nearbyTask = new PopulateNearbyTask((float)lastLocation.getLatitude(), (float)lastLocation.getLongitude(), this);
		nearbyTask.execute();
	}
	
	public void setCheckInView(int pos, boolean changeTopPlace)
	{
		if(changeTopPlace)
		{
			FoxPlace p = places.remove(pos);
			
			if (CheckinPlace != null)
				places.add(CheckinPlace);
			
			CheckinPlace = p;
		}
		
		CheckinPlace.loadBigImageBitmaps();
		
		setContentView(R.layout.checkinview);
		
		
		TextView tview = (TextView) findViewById(R.id.cv_name);
		tview.setText(CheckinPlace.name);
		tview = (TextView) findViewById(R.id.cv_address);
		tview.setText(CheckinPlace.address);
		tview = (TextView) findViewById(R.id.cv_phone);
		tview.setText(CheckinPlace.phoneNum);

		ImageView pic = (ImageView) findViewById(R.id.cv_stars);
		pic.setImageBitmap(CheckinPlace.rating_img);

		pic = (ImageView) findViewById(R.id.cv_picture);
		pic.setImageBitmap(CheckinPlace.photo);
		
		Button checkin = (Button) findViewById(R.id.cv_checkinbtn);
		checkin.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				checkinToSelected();			
			}
		});

		
		
		ListView lv = (ListView) findViewById(R.id.cv_nearbylist);
		lv.setAdapter(new ArrayAdapter<FoxPlace>(this,
		android.R.layout.simple_list_item_1,
		places));
		
		
		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
            int position, long id) {
    		
				setCheckInView(position, true);
			}
		});
		
	}
	
	public void checkinToSelected()
	{
		try
		{
			GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
			appState.fsm.addCheckin(CheckinPlace.id, appState.getUserEmail());
		}
		catch(FoxServerException e)
		{
			Toast.makeText(this, "Could not check into place - " + e.getMsg(), Toast.LENGTH_SHORT);
			return;
		}
		
		setContentView(R.layout.placeview);
		
		
		TextView tview = (TextView) findViewById(R.id.pv_name);
		tview.setText(CheckinPlace.name);
		tview = (TextView) findViewById(R.id.pv_address);
		tview.setText(CheckinPlace.address + "\n" + CheckinPlace.city + ", " + CheckinPlace.state + ' ' + CheckinPlace.zip_code);
		tview = (TextView) findViewById(R.id.pv_phone);
		tview.setText(PhoneNumberUtils.formatNumber(CheckinPlace.phoneNum.trim()));
		
		tview = (TextView) findViewById(R.id.pv_numvisits);
		tview.setText(""+CheckinPlace.checkin_count);
		
		tview = (TextView) findViewById(R.id.pv_rating_count);
		tview.setText("(" + CheckinPlace.review_count + ")");
		
		tview = (TextView) findViewById(R.id.pv_distanceAway);
		tview.setText(DecimalFormat.getInstance().format(CheckinPlace.distance_from_request));
		
		Button btn = (Button) findViewById(R.id.pv_yelpreviewlink);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(CheckinPlace.mobile_url));
				startActivity(i);

			}
		});	
		ImageButton imgbtn = (ImageButton) findViewById(R.id.clicktocallbtn);
		imgbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent i = new Intent(Intent.ACTION_CALL);
				i.setData(Uri.parse("tel:+" + CheckinPlace.phoneNum));
				startActivity(i);

			}
		});
		
		
		ImageView pic = (ImageView) findViewById(R.id.pv_stars);
		pic.setImageBitmap(CheckinPlace.rating_img);

		pic = (ImageView) findViewById(R.id.pv_picture);
		pic.setImageBitmap(CheckinPlace.photo);
		
		isCheckedIn = true;
		
		Toast.makeText(this.getParent(), "Checked into " + CheckinPlace.name, Toast.LENGTH_SHORT);

	}
		
    private class PopulateNearbyTask extends AsyncTask<Void, Void, ArrayList<FoxPlace> > {

    	private float lat;
    	private float lon;
    	private FoxCheckinActivity checkinactivity;
    	private ProgressDialog dialog;

    	public PopulateNearbyTask(float lat_, float lon_, FoxCheckinActivity checkinactivity_)
    	{
    		lat = lat_;
    		lon = lon_;
    		checkinactivity = checkinactivity_;
    	}

    	@Override
    	protected void onPreExecute() {
            dialog = new ProgressDialog(checkinactivity);
            dialog.setMessage("Loading Nearby Places...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

    	}

    	// automatically done on worker thread (separate from UI thread)
    	@Override
    	protected ArrayList<FoxPlace> doInBackground(final Void... unused) {
    		ArrayList<FoxPlace> nearbyPlaces = new ArrayList<FoxPlace>();
    		try
    		{
    			GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
    			nearbyPlaces = appState.fsm.placeSearchNear(lat, lon);
    		}
    		catch(FoxServerException e)
    		{
    			Log.v("FindActivity","Caught exception!" + e.getMsg());
    		}
    		return nearbyPlaces;
    	}

    	// can use UI thread here
    	@Override
    	protected void onPostExecute(ArrayList<FoxPlace> result) {
    		if (dialog.isShowing()) {
    			dialog.dismiss();
    		}
    		if(result.size() > 0)
    		{
    			
    			places = result;
    			checkinactivity.setCheckInView(0, true);
    			
    		}
    	}
    }
    
    protected void createCheckinNewUserPopup()
    {
		LayoutInflater infalter = LayoutInflater.from(this);
	    final View textEntryView = infalter.inflate(R.layout.usercreateview, null);
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("Create a new user...");
    	dialogBuilder.setView(textEntryView);
     	dialogBuilder.setPositiveButton("Create", new DialogInterface.OnClickListener(){
     		public void onClick(DialogInterface dialog, int which) 
			{    			
     			String email = ((EditText) textEntryView.findViewById(R.id.uc_email)).getText().toString();
     			String password = ((EditText) textEntryView.findViewById(R.id.uc_password)).getText().toString();
     			String fname = ((EditText) textEntryView.findViewById(R.id.uc_fname)).getText().toString();
     			String lname = ((EditText) textEntryView.findViewById(R.id.uc_lname)).getText().toString();
     			String phonenum = ((EditText) textEntryView.findViewById(R.id.uc_phone)).getText().toString(); 
     			
     			//String pass, String fname, String lname, String email, String phone
     			
     			GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
     			try
     			{
     				appState.fsm.createUser(password, fname, lname, email, phonenum);
     			}
     			catch(FoxServerException e)
     			{
     				createCheckinNewUserPopup();
     				return;
     			}
     			
     			if (appState.setLoginInfo(email, password))
     				startFindingNearby();
     			else
     				createCheckinLoginPopup();
			}
		}); 
		dialogBuilder.setNegativeButton("Cancel", null);
		Dialog create = dialogBuilder.create();
		create.show();
    }
    
    protected void createCheckinLoginPopup() 
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
     				startFindingNearby();
     			else
     				createCheckinLoginPopup();
			}
		}); 
		dialogBuilder.setNegativeButton("Cancel", null);
		dialogBuilder.setNeutralButton("Create User", new DialogInterface.OnClickListener(){
     		public void onClick(DialogInterface dialog, int which) 
			{    			
     			dialog.dismiss();
     			createCheckinNewUserPopup();
			}
			});
		Dialog login = dialogBuilder.create();
		login.show();
	}
}
