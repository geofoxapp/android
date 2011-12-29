package com.geofoxapp.android;


import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FoxHistAdapter extends ArrayAdapter<FoxHistoryObject> {

	private ArrayList<FoxHistoryObject> items;
	private Context context;
	private boolean isDateAdapter;
	
	public FoxHistAdapter(Context context_, int textViewResourceId, ArrayList<FoxHistoryObject> items_, boolean isDateAdapter_)
	{
		super(context_, textViewResourceId, items_);
		items = items_;
		context = context_;
		isDateAdapter = isDateAdapter_;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.historyrow, null);
		}
		FoxHistoryObject o = items.get(position);
		if (o != null) {
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			TextView bt = (TextView) v.findViewById(R.id.bottomtext);
			if (tt != null)
			{
				tt.setText(o.place.name);
				tt.setTypeface(null, Typeface.BOLD);
			}
			if(bt != null)
			{

				
				if (isDateAdapter)
				{
					String printdate;
					Date current = new Date();
					current.setHours(0);
					current.setMinutes(0);
					current.setSeconds(0);
					
					if (o.time.before(current))
						printdate = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(o.time);
					else
						printdate = "Today at " + DateFormat.getTimeInstance(DateFormat.SHORT).format(o.time);
					
					bt.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>Last Visit:</i>&nbsp;" + printdate));
				}
				else
				{
					bt.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<i>Number of Visits:</i>&nbsp;" + o.place.checkin_count));
				}				
			}
			ImageView iv = (ImageView) v.findViewById(R.id.smallplaceimg);
			iv.setImageBitmap(o.place.photo_small);
		}
		return v;
	}
}
