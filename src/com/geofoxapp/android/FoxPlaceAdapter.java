package com.geofoxapp.android;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class FoxPlaceAdapter extends ArrayAdapter<FoxPlace> {
	
	private ArrayList<FoxPlace> items;
	private Context context;
	
	public FoxPlaceAdapter(Context context_, int textViewResourceId, ArrayList<FoxPlace> items_)
	{
		super(context_, textViewResourceId, items_);
		items = items_;
		context = context_;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{

		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.placerow, null);
		}
		FoxPlace o = items.get(position);
		if (o != null) {
			TextView nt = (TextView) v.findViewById(R.id.pr_nametext);
			TextView dt = (TextView) v.findViewById(R.id.pr_numratings);
			if (nt != null)
			{
				nt.setText(o.name);
				nt.setTypeface(null, Typeface.BOLD);
			}
			if(dt != null)
			{

				dt.setText(Html.fromHtml("&nbsp;&nbsp;&nbsp;&nbsp;<i>Reviews:</i>&nbsp;" + o.review_count));
			}
			ImageView iv = (ImageView) v.findViewById(R.id.pr_smallplaceimg);
			iv.setImageBitmap(o.photo_small);
			iv = (ImageView) v.findViewById(R.id.pr_reviewimg);
			iv.setImageBitmap(o.rating_img);
			
		}
		return v;
	}
}
