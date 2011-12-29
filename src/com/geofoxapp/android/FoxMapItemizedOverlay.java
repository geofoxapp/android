package com.geofoxapp.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.EditText;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class FoxMapItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	
	private FoxMapView mapView;
	
	
	public FoxMapItemizedOverlay(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}
	
	public FoxMapItemizedOverlay(Drawable defaultMarker, FoxMapView mapView_) {
		  super(boundCenterBottom(defaultMarker));
		  mapView = mapView_;
	}

	public void addOverlay(OverlayItem overlay) {
		mOverlays.add(overlay);
		populate();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		return mapView.showDialog(item);
	}

	@Override
	protected OverlayItem createItem(int i) {
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		return mOverlays.size();
	}

}
