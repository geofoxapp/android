package com.geofoxapp.android;

import java.util.List;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.ViewFlipper;


public class FoxMainActivity extends TabActivity {

	private LocationManager locManager;
	private Location currentLocation;
	
	
	private LocationListener gpsListener;
	private LocationListener networkListener;
	
	
	//To be given when no other GeoPoint has been initialized
	private static final GeoPoint lbmeGeoPoint = new GeoPoint(42288880, -83713481);
    private static final int TWO_MINUTES = 1000 * 60 * 2;
	
    private boolean map_shown;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //SET UP THE TABS
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FoxCheckinActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("checkin").setIndicator("",
                          res.getDrawable(R.drawable.checkin_icon))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FoxFindActivity.class);
        spec = tabHost.newTabSpec("find").setIndicator("",
                          res.getDrawable(R.drawable.find_icon))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FoxRecomendationActivity.class);
        spec = tabHost.newTabSpec("recomendation").setIndicator("",
                          res.getDrawable(R.drawable.rec_icon))
                      .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, FoxHistoryActivity.class);
        spec = tabHost.newTabSpec("history").setIndicator("",
        				  res.getDrawable(R.drawable.history_icon))
        			  .setContent(intent);
        tabHost.addTab(spec);
        
        tabHost.setCurrentTab(0);

        
        //END OF THE TABS
        
        currentLocation = null;
        map_shown = false;
//		GeofoxApplication appState = ((GeofoxApplication)getApplicationContext());
//		
//		if (!appState.fsm.loginRefreshAuthKey())
//			createLoginPopup();
    }
    
    

    
    
    @Override
	public void onStart()
    {
    	super.onStart();
        startLocationFinding();
    	return;
    }
    
    @Override
	public void onStop()
    {
    	super.onStop();
    	locManager.removeUpdates(networkListener);
    	locManager.removeUpdates(gpsListener);
    	return;
    }
    
    public GeoPoint getCurrentGeoPoint()
    {
    	if (currentLocation == null)
    		return lbmeGeoPoint;

    	return new GeoPoint((int)(currentLocation.getLatitude() * 1E6), (int)(currentLocation.getLongitude() * 1E6));
    }
    
    public Location getCurrentLocation()
    {
    	if (currentLocation == null)
    	{
    		Location dummyLMBEloc = new Location("MockProvider");
    		dummyLMBEloc.setLatitude(42.288880);
    		dummyLMBEloc.setLongitude(-83.713481);
    		return dummyLMBEloc;
    	}
    	
    	return currentLocation;
    }
    
    private void startLocationFinding()
    {
    	locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

    	gpsListener = new LocationListener() {
    		public void onLocationChanged(Location location) {
    			// Called when a new location is found by the gps location provider.
    			if(isBetterLocation(location, currentLocation))
    				currentLocation = location;
    				
    		}
    		public void onStatusChanged(String provider, int status, Bundle extras) {}
    		public void onProviderEnabled(String provider) {}
    		public void onProviderDisabled(String provider) {}
    	};

    	networkListener = new LocationListener() {
    		public void onLocationChanged(Location location) {
    			// Called when a new location is found by the network location provider.
    			if(isBetterLocation(location, currentLocation))
    				currentLocation = location;
    		}
    		public void onStatusChanged(String provider, int status, Bundle extras) {}
    		public void onProviderEnabled(String provider) {}
    		public void onProviderDisabled(String provider) {}
    	};
        
    	//Request updates to be sent to another function for comparing to current
    	//Updated every minute for network and every minute and a half for GPS
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 90000, 15, gpsListener);
    	locManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 60000, 15, networkListener);
    	
    	
    	//Set the current best location
    	//If no location is stored in either provider set the location to null - if a GeoPoint is requested LBME is returned
    	Location oldgpsloc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	Location oldnetworkloc = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    	
    	if (oldnetworkloc != null && oldgpsloc != null)
    	{
    		if (isBetterLocation(oldnetworkloc, oldgpsloc))
    		{
    			currentLocation = new Location(oldnetworkloc);
    		}
    		else
    		{
    			currentLocation = new Location(oldgpsloc);
    		}
    	}
    	else if (  oldnetworkloc == null && oldgpsloc != null )
    	{
    		currentLocation = new Location(oldgpsloc);
    	}
    	else if (oldgpsloc == null && oldnetworkloc != null)
    	{
    		currentLocation = new Location(oldnetworkloc);
    	}
    	else
    	{
    		currentLocation = null;    	   		
    	}    	
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation)
    {
        /** Determines whether one Location reading is better than the current Location fix
         * @param location  The new Location that you want to evaluate
         * @param currentBestLocation  The current Location fix, to which you want to compare the new one
         */
       // Borrowed from Google -- http://developer.android.com/guide/topics/location/obtaining-user-location.html
    	
    	if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
        // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2)
    {     /** Checks whether two providers are the same */
        if (provider1 == null) {
          return provider2 == null;
        }
        return provider1.equals(provider2);
    }
     
    
}