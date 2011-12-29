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
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class FoxRecomendationActivity extends ActivityGroup {
	
	private ArrayList<FoxPlace> recommendations;
	private PopulateRecTask rectask;
	private int current_view; 
	
	private static final int REC_VIEW = 1;
	private static final int PLACE_INFO_VIEW = 2;
	private static final int MAP_VIEW = 3;
	private static final int MORE_PLACES_VIEW = 4;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        TextView textview = new TextView(this);
//        textview.setText("Loading the Recommendations tab...");
//        setContentView(textview);
        
        
        recommendations = new ArrayList<FoxPlace>();
        current_view = 0;
        rectask = null;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		
		GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
		
		if (appState.isUserLoggedIn())
			startGetRecs();
		else	
			createRecLoginPopup();
		
		
	}
	
	@Override
	public void onBackPressed()
	{
		if(rectask.getStatus() == AsyncTask.Status.RUNNING)
		{
			rectask.cancel(true);
			super.onBackPressed();
			return;
		}
		
		switch(current_view)
		{
			case REC_VIEW:
				super.onBackPressed();
			break;
			case PLACE_INFO_VIEW:
				current_view = REC_VIEW;
				setRecView();		
			break;
			case MAP_VIEW: case MORE_PLACES_VIEW:
				current_view = REC_VIEW;
				setRecView();
			break;
			default:
				super.onBackPressed();
			break;
		}
	}
	
	private void setRecView()
	{
		setContentView(R.layout.yelplist);
		
		ListView lview = (ListView) findViewById(R.id.lview);
		lview.setAdapter(new FoxPlaceAdapter(this,
				R.layout.placerow,
				recommendations));
		
		lview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		            int position, long id) {
		    		
		    	FoxPlace place = (FoxPlace) parent.getItemAtPosition(position);

		    	setPlaceInfoView(place);

		    	
		        }
		      });
		
		TextView tview = (TextView) findViewById(R.id.yl_title);
		tview.setText("Recommendations");
		tview.setTextSize(18);
		
		Button switchbtb = (Button) findViewById(R.id.yl_switchbtn);

		switchbtb.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), FoxMapView.class);
				replaceContentView("Map Activity", intent);
				((FoxMapView)getLocalActivityManager().getCurrentActivity()).setOverlays(recommendations);
				
			}
		});	
		
		current_view = REC_VIEW;
	}
	
	public void replaceContentView(String id, Intent newIntent)
	{
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); 
		this.setContentView(view);
		current_view = MAP_VIEW;
	}
	
	private void startGetRecs()
	{
		if (rectask == null) {
			rectask = new PopulateRecTask(this);
			rectask.execute();
		}
	}
	
    private class PopulateRecTask extends AsyncTask<Void, Void, ArrayList<FoxPlace> > {


    	private FoxRecomendationActivity recactivity;
    	private ProgressDialog dialog;

    	public PopulateRecTask(FoxRecomendationActivity recactivity_)
    	{
    		recactivity = recactivity_;
    	}

    	@Override
    	protected void onPreExecute() {
            dialog = new ProgressDialog(recactivity);
            dialog.setMessage("Loading Recommendations...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

    	}

    	// automatically done on worker thread (separate from UI thread)
    	@Override
    	protected ArrayList<FoxPlace> doInBackground(final Void... unused) {
    		ArrayList<FoxPlace> recobjs = new ArrayList<FoxPlace>();
    		try
    		{
    			GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
    			recobjs = appState.fsm.getRecs();
    		}
    		catch(FoxServerException e)
    		{
    			Log.v("Recommendation Activity","Caught exception!" + e.getMsg());
    		}
    		return recobjs;
    	}

    	// can use UI thread here
    	@Override
    	protected void onPostExecute(ArrayList<FoxPlace> result) {
    		if (dialog.isShowing()) {
    			dialog.dismiss();
    		}
    		if(result.size() > 0)
    		{
    			recommendations = result; 
    			recactivity.setRecView();
    		}
    	}
    }
    
	private void setPlaceListView(final ArrayList<FoxPlace> places)
	{
		setContentView(R.layout.yelplist);
		
		ListView lview = (ListView) findViewById(R.id.lview);
		
		lview.setAdapter(new FoxPlaceAdapter(this,
				R.layout.placerow,
				places));
		lview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		            int position, long id) {
		    		
		    		setPlaceInfoView((FoxPlace)parent.getItemAtPosition(position));
		        }
		      });
		
		
		TextView tview = (TextView) findViewById(R.id.yl_title);
		tview.setText("Similar Places");
		tview.setTextSize(22);
		
		Button switchbtb = (Button) findViewById(R.id.yl_switchbtn);

		switchbtb.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), FoxMapView.class);
				replaceContentView("Map Activity", intent);
				((FoxMapView)getLocalActivityManager().getCurrentActivity()).setOverlays(places);

			}
		});
				
		current_view = MORE_PLACES_VIEW;
	}
    
    private class TaskFindMoreLike extends AsyncTask<Void, Void, ArrayList<FoxPlace> > {


    	private FoxRecomendationActivity recact;
    	private ProgressDialog dialog;
    	private String placeid;

    	public TaskFindMoreLike(FoxRecomendationActivity recact_, String placeid_)
    	{
    		recact = recact_;
    		placeid = placeid_;
    	}

    	@Override
    	protected void onPreExecute() {
            dialog = new ProgressDialog(recact);
            dialog.setMessage("Loading Similar Places...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

    	}

    	// automatically done on worker thread (separate from UI thread)
    	@Override
    	protected ArrayList<FoxPlace> doInBackground(final Void... unused) {
    		ArrayList<FoxPlace> objs = new ArrayList<FoxPlace>();
    		try
    		{
    			GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
    			objs = appState.fsm.placeMoreLike(placeid);
    		}
    		catch(FoxServerException e)
    		{
    			Log.v("RecommendationActivity","Caught exception! in placemorelike" + e.getMsg());
    		}
    		return objs;
    	}

    	// can use UI thread here
    	@Override
    	protected void onPostExecute(ArrayList<FoxPlace> result) {
    		if(result.size() > 0)
    		{
    			recact.setPlaceListView(result);
    		}
    		
    		if (dialog.isShowing()) {
    			dialog.dismiss();
    		}
    	}
    }
	
    protected void createRecLoginPopup() 
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
     				startGetRecs();
     			else
     				createRecLoginPopup();
			}
		}); 
		dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
     		public void onClick(DialogInterface dialog, int which) 
			{    			
     			dialog.dismiss();
			}
			});
		Dialog login = dialogBuilder.create();
		login.show();
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
		tview.setText("unknown");//DecimalFormat.getInstance().format(place.distance_from_request));
		
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
		
		btn = (Button) findViewById(R.id.morelikebutton);
		final String finalid = place.id;
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				TaskFindMoreLike task = new TaskFindMoreLike(FoxRecomendationActivity.this, finalid);
				task.execute();

			}
		});	


		
		current_view = PLACE_INFO_VIEW;

	}
    
}
        


