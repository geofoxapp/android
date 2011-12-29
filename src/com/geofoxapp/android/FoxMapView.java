package com.geofoxapp.android;



import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.MapController;
import com.google.android.maps.MapActivity;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class FoxMapView extends MapActivity {
    //testing SVN
	private MapView mapView;
	private MapController mc;
	
	private GeoPoint curPoint;
	
	private Activity parent;
	
	private List<FoxPlace> places;
	
	public boolean placeInfoShowing;
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mapview);
        
        mapView = (MapView) findViewById(R.id.mapview);
        mc = mapView.getController();
        mapView.setBuiltInZoomControls(true);
        
        parent = getParent();
        
        curPoint = ((FoxMainActivity)parent.getParent()).getCurrentGeoPoint();
        
    }
    
    
    public void setOverlays(List<FoxPlace> placesToMap)
    {
    	places = placesToMap;
    	
        List<Overlay> mapOverlays = mapView.getOverlays();
        OverlayItem overlayitem = new OverlayItem(curPoint, "Current Location", "You're Here!");
        
        Drawable drawable = this.getResources().getDrawable(R.drawable.marker);
    	FoxMapItemizedOverlay itemsoverlay = new FoxMapItemizedOverlay(drawable, this);
        
        itemsoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemsoverlay);
        
        drawable = this.getResources().getDrawable(R.drawable.pin);
        itemsoverlay = new FoxMapItemizedOverlay(drawable, this);
        
        for(int i = 0; i < places.size(); i++)
        {
        	overlayitem = new OverlayItem(places.get(i).point, places.get(i).name, ""+i);
        	itemsoverlay.addOverlay(overlayitem);
        }
        
        mapOverlays.add(itemsoverlay);

        mc.animateTo(curPoint);
        mc.setZoom(13); 
        
        placeInfoShowing = false;
    }
    
    
    
    public boolean showDialog(final OverlayItem item)
    {
		AlertDialog.Builder dialog = new AlertDialog.Builder(parent);
		dialog.setTitle(item.getTitle());
		
		if(item.getTitle() == "Current Location")
		{
			dialog.setNegativeButton("Close", null);
			dialog.show();
			return true;

		}

		dialog.setPositiveButton("More Info", new DialogInterface.OnClickListener(){
     		public void onClick(DialogInterface dialog, int which) 
			{    			
     			setPlaceInfoView(places.get(Integer.valueOf(item.getSnippet())));
     			dialog.dismiss();
			}
		}); 
		dialog.setNegativeButton("Close", null);
		dialog.show();
		return true;
    	
    }
    

    
    public void setPlaceInfoView(final FoxPlace place)
    {
    	Window window = this.getWindow();
    	
		setContentView(R.layout.placeview);
		place.loadBigImageBitmaps();

		TextView tview = (TextView) findViewById(R.id.pv_name);
		tview.setText(place.name);
		tview = (TextView) findViewById(R.id.pv_address);
		tview.setText(place.address + "\nAnn Arbor, MI 48104");
		tview = (TextView) findViewById(R.id.pv_phone);
		tview.setText(PhoneNumberUtils.formatNumber(place.phoneNum.trim()));
		
		tview = (TextView) findViewById(R.id.pv_numvisits);
		tview.setText(""+place.checkin_count);
		
		tview = (TextView) findViewById(R.id.pv_rating_count);
		tview.setText("(" + place.review_count + ")");
		
		Button btn = (Button) findViewById(R.id.pv_yelpreviewlink);
		btn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent i = new Intent(Intent.ACTION_VIEW);
				i.setData(Uri.parse(place.mobile_url));
				startActivity(i);

			}
		});	
		ImageButton imgbtn = (ImageButton) findViewById(R.id.clicktocallbtn);
		imgbtn.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				
				Intent i = new Intent(Intent.ACTION_CALL);
				i.setData(Uri.parse("tel:+" + place.phoneNum));
				startActivity(i);

			}
		});
		
		
		ImageView pic = (ImageView) findViewById(R.id.pv_stars);
		pic.setImageBitmap(place.rating_img);

		pic = (ImageView) findViewById(R.id.pv_picture);
		pic.setImageBitmap(place.photo);
		
		placeInfoShowing = true;
		

    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}