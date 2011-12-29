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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class FoxHistoryActivity extends ActivityGroup {
    
	private ArrayList<FoxHistoryObject> history;
	private ArrayList<FoxHistoryObject> uniquehistory;
	
	private PopulateHistoryTask allHistoryTask;
	
	private static final int HISTORY_VIEW = 1;
	private static final int PLACE_INFO_VIEW = 2;
	private static final int MAP_VIEW = 3;
	private static final int MORE_PLACES_VIEW = 4;
	
	private int current_view; 
	private boolean sort_by_date;
	
	private FoxMainActivity main;
	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        TextView textview = new TextView(this);
//        textview.setText("Loading the History tab...");// \n \n Here we will display the locations where a user has previously checked in. ");
//        setContentView(textview);
        
        history = new ArrayList<FoxHistoryObject>();
        uniquehistory = new ArrayList<FoxHistoryObject>();
        
        current_view = 0;
        
        main = (FoxMainActivity)getParent();
        
    }
	
	public List<FoxPlace> getHistoryPlaces()
	{
		ArrayList<FoxPlace> histPlaces =  new ArrayList<FoxPlace>();
		
		for(int i = 0; i < history.size(); i++)
		{
			histPlaces.add(history.get(i).place);
		}
		
		return histPlaces;
	}
	
	
	@Override
	public void onBackPressed()
	{
		if(allHistoryTask.getStatus() == AsyncTask.Status.RUNNING)
		{
			allHistoryTask.cancel(true);
			super.onBackPressed();
			return;
		}
		
		switch(current_view)
		{
			case HISTORY_VIEW:
				super.onBackPressed();
			break;
			case PLACE_INFO_VIEW:
				current_view = HISTORY_VIEW;
				if(this.sort_by_date)
					setHistoryDateView();
				else
					setHistoryCountView();
			break;
			case MAP_VIEW:
				current_view = HISTORY_VIEW;
				if(this.sort_by_date)
					setHistoryDateView();
				else
					setHistoryCountView();
			break;
			case MORE_PLACES_VIEW:
				current_view = HISTORY_VIEW;
				if(this.sort_by_date)
					setHistoryDateView();
				else
					setHistoryCountView();
			break;
			default:
				super.onBackPressed();
			break;
		}
	}
	
	
	@Override
	public void onResume()
	{
		super.onResume();
		
		
		GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
		
		if (appState.isUserLoggedIn())
			startGetAllHistory();
		else	
			createHistoryLoginPopup();
		
		
	}
	
	private void startGetAllHistory()
	{
		allHistoryTask = new PopulateHistoryTask(this);
		allHistoryTask.execute();
	}
	
	private void setHistoryCountView()
	{
		setContentView(R.layout.yelphistorylist);
		
		Set<FoxHistoryObject> objset = new HashSet<FoxHistoryObject>(history);
		uniquehistory.clear();
		uniquehistory.addAll(objset);
		
		Comparator<FoxHistoryObject> comperator = new Comparator<FoxHistoryObject>() {

			public int compare(FoxHistoryObject object1,
					FoxHistoryObject object2) {
				return object2.place.checkin_count - object1.place.checkin_count;
			}
		};

		Collections.sort(uniquehistory, comperator);
		
		ListView lview = (ListView) findViewById(R.id.lview);

		lview.setAdapter(new FoxHistAdapter(this,
				R.layout.historyrow,
				uniquehistory, false));
		lview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		            int position, long id) {
		    		
		    		setPlaceInfoView(((FoxHistoryObject)parent.getItemAtPosition(position)).place);
		        }
		      });
		
		TextView tview = (TextView) findViewById(R.id.yl_title);
		tview.setText("History");
		tview.setTextSize(32);
		
		Button mapswitchbtn = (Button) findViewById(R.id.yl_switchbtn);

		mapswitchbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), FoxMapView.class);
				replaceContentView("Map Activity", intent);
				((FoxMapView)getLocalActivityManager().getCurrentActivity()).setOverlays(getHistoryPlaces());

			}
		});	
		
		
		Button sortswitchbtn = (Button) findViewById(R.id.yl_sortbtn);
		sortswitchbtn.setText("Sort By Visited Date");
		
		sortswitchbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				setHistoryDateView();

			}
		});
		
		current_view = HISTORY_VIEW;
		this.sort_by_date = false;
	}

	private void setHistoryDateView()
	{
		setContentView(R.layout.yelphistorylist);
		
		ListView lview = (ListView) findViewById(R.id.lview);
		
		lview.setAdapter(new FoxHistAdapter(this,
				R.layout.historyrow,
				history, true));
		lview.setOnItemClickListener(new OnItemClickListener() {
		    public void onItemClick(AdapterView<?> parent, View view,
		            int position, long id) {
		    		
		    		setPlaceInfoView(((FoxHistoryObject)parent.getItemAtPosition(position)).place);
		        }
		      });
		
		lview.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int pos, long id) {
				
				showDiag(pos);
				
				return true;
			}
			
		});

		
		TextView tview = (TextView) findViewById(R.id.yl_title);
		tview.setText("History");
		tview.setTextSize(32);
		
		Button switchbtb = (Button) findViewById(R.id.yl_switchbtn);

		switchbtb.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent intent = new Intent(v.getContext(), FoxMapView.class);
				replaceContentView("Map Activity", intent);
				((FoxMapView)getLocalActivityManager().getCurrentActivity()).setOverlays(getHistoryPlaces());

			}
		});
		
		Button sortswitchbtn = (Button) findViewById(R.id.yl_sortbtn);
		
		sortswitchbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				
				setHistoryCountView();

			}
		});
		
		current_view = HISTORY_VIEW;
		this.sort_by_date = true;
		
	}
	
	public void removeCheckinAndReset(int position)
	{
		FoxHistoryObject removeMe = history.get(position);
		GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
		boolean removed;
		try
		{
			removed = appState.fsm.removeCheckin(removeMe.checkin_id);
		}
		catch(FoxServerException err)
		{
			return;
		}
		
		if(removed)
		{
			history.remove(position);
			setHistoryDateView();
		}
		else
			return;
	}
	
	
    public void showDiag(final int position)
    {
		AlertDialog.Builder dialog = new AlertDialog.Builder(FoxHistoryActivity.this);
		dialog.setTitle(history.get(position).place_name);
		
		dialog.setMessage("Remove CheckIn?");
		

		dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener(){
     		public void onClick(DialogInterface dialog, int which) 
			{    			
     			removeCheckinAndReset(position);
     			dialog.dismiss();
			}
		}); 
		dialog.setNegativeButton("Cancel", null);
		dialog.show();	
    }
	
	public void replaceContentView(String id, Intent newIntent)
	{
		View view = getLocalActivityManager().startActivity(id,newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)) .getDecorView(); 
		this.setContentView(view);
		current_view = MAP_VIEW;
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
				TaskFindMoreLike task = new TaskFindMoreLike(FoxHistoryActivity.this, finalid);
				task.execute();

			}
		});	

		
		current_view = PLACE_INFO_VIEW;

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
	
	
    private class PopulateHistoryTask extends AsyncTask<Void, Void, ArrayList<FoxHistoryObject> > {


    	private FoxHistoryActivity historyactivity;
    	private ProgressDialog dialog;

    	public PopulateHistoryTask(FoxHistoryActivity historyact_)
    	{
    		historyactivity = historyact_;
    	}

    	@Override
    	protected void onPreExecute() {
            dialog = new ProgressDialog(historyactivity);
            dialog.setMessage("Loading History...");
            dialog.setIndeterminate(true);
            dialog.setCancelable(false);
            dialog.show();

    	}

    	// automatically done on worker thread (separate from UI thread)
    	@Override
    	protected ArrayList<FoxHistoryObject> doInBackground(final Void... unused) {
    		ArrayList<FoxHistoryObject> historyobjs = new ArrayList<FoxHistoryObject>();
    		try
    		{
    			GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
    			historyobjs = appState.fsm.getAllHistory();
    		}
    		catch(FoxServerException e)
    		{
    			Log.v("HistoryActivity","Caught exception!" + e.getMsg());
    		}
    		return historyobjs;
    	}

    	// can use UI thread here
    	@Override
    	protected void onPostExecute(ArrayList<FoxHistoryObject> result) {
    		if(result.size() > 0)
    		{
    			history = result;
    			
    			Comparator<FoxHistoryObject> comperator = new Comparator<FoxHistoryObject>() {

    				public int compare(FoxHistoryObject object1,
    						FoxHistoryObject object2) {
    					return object2.time.compareTo(object1.time);
    				}
    			};

    			Collections.sort(history, comperator);
			
    			historyactivity.setHistoryDateView();
    		}
    		
    		if (dialog.isShowing()) {
    			dialog.dismiss();
    		}
    	}
    }
    
    private class TaskFindMoreLike extends AsyncTask<Void, Void, ArrayList<FoxPlace> > {


    	private FoxHistoryActivity historyactivity;
    	private ProgressDialog dialog;
    	private String placeid;

    	public TaskFindMoreLike(FoxHistoryActivity historyact_, String placeid_)
    	{
    		historyactivity = historyact_;
    		placeid = placeid_;
    	}

    	@Override
    	protected void onPreExecute() {
            dialog = new ProgressDialog(historyactivity);
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
    			Log.v("HistoryActivity","Caught exception! in placemorelike" + e.getMsg());
    		}
    		return objs;
    	}

    	// can use UI thread here
    	@Override
    	protected void onPostExecute(ArrayList<FoxPlace> result) {
    		if(result.size() > 0)
    		{
    			historyactivity.setPlaceListView(result);
    		}
    		
    		if (dialog.isShowing()) {
    			dialog.dismiss();
    		}
    	}
    }

    
    protected void createHistoryLoginPopup() 
    {
		LayoutInflater infalter = this.getLayoutInflater();// LayoutInflater.from(this);
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
     				startGetAllHistory();
     			else
     				createHistoryLoginPopup();
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
}
