package com.bidachon.velib;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;


public class StationAdapter extends ArrayAdapter<Station> {


        private Activity activity;
        private android.widget.Filter filter;
        private ArrayList<Station> filteredStations;
        private ArrayList<Station> allStations;

        public StationAdapter(Context context, int textViewResourceId, ArrayList<Station> items) {
                super(context, textViewResourceId,items);
                allStations = new ArrayList<Station>();
                allStations.addAll(items); 
                filteredStations = new ArrayList<Station>();
                this.activity = (Activity)context;
          
        }
        
        public synchronized void setStations(ArrayList<Station> stations)
        {
        	allStations.clear();
        	allStations.addAll(stations);
            for (int i = 0 ; i < stations.size(); i++)
            {
            	if (filteredStations.contains(stations.get(i)))
            	{
            		int index = filteredStations.indexOf(stations.get(i));
            		filteredStations.set(index,stations.get(i));	
            	}          	
            }	
            this.notifyDataSetChanged();
        }
        
        public synchronized ArrayList<Station> allStations()
        {
        	return this.allStations;
        }
        
        public void setFilteredStations(ArrayList<Station> list)
        {
        	this.filteredStations.clear();
        	this.filteredStations.addAll(list);        	
        	this.notifyDataSetChanged();
        }
        
        
        public int getCount() {
            return filteredStations.size();
        }


        public Station getItem(int position) {
            return filteredStations.get(position);
        }


        public int getPosition(Station item) {
            return filteredStations.indexOf(item);
        }


        public long getItemId(int position) {
            return position;
        }
        
        
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                View v = convertView;
                if (v == null) {
                    LayoutInflater vi = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    v = vi.inflate(R.layout.stationlistitem, null);
                }
                Station s = getItem(position);
                if (s != null) {
                        TextView tt = (TextView) v.findViewById(R.id.toptext);
                        TextView mt = (TextView) v.findViewById(R.id.middletext);
                        TextView bt = (TextView) v.findViewById(R.id.bikesText);
                        TextView st = (TextView) v.findViewById(R.id.standsText);
                        TextView dist = (TextView) v.findViewById(R.id.distance);
                        if (tt != null) {
                              tt.setText(s.name());
                        }
                        if (mt != null) {
                            mt.setText(s.address());
                      }
                        if(bt != null){
                              bt.setText("" + s.getBikes());
                        }
                        if(st != null){
                        	st.setText( "" + s.getStands());
                        }
                        if(dist != null){
                        	dist.setText(s.distance() + " m");
                        }
                        ImageButton viewBtn = (ImageButton)v.findViewById(R.id.viewbtn);
                        
                        if (viewBtn != null)
                        	viewBtn.setOnClickListener(new ViewMapListener((VforVelibApp)activity.getApplication(), s));
                        	
                        ImageButton favoriteBtn = (ImageButton)v.findViewById(R.id.favoritebtn);
                        
                        if (favoriteBtn != null)
                        {
	                        favoriteBtn.setOnClickListener(new FavoriteListener((VforVelibApp)activity.getApplication(),s));
	        				if (!s.getFavorite())
	        					favoriteBtn.setImageResource(R.drawable.favoriteicon_notselected);
	        				else
	        					favoriteBtn.setImageResource(R.drawable.favoriteicon_selected);
                        }
                        ImageButton updateBtn = (ImageButton)v.findViewById(R.id.updateBtn);

                        if (updateBtn != null)
                        	updateBtn.setOnClickListener(new UpdateListener((VforVelibApp)activity.getApplication(), s));
                }
                return v;
        }
        
        public void setFilter(android.widget.Filter filter)
        {
        	this.filter = filter;
        }

        public android.widget.Filter getFilter() {
            return filter;
        }
        
        private class UpdateListener implements Button.OnClickListener
        {
        	private VforVelibApp app;
        	private Station station;
        	
        	public UpdateListener(VforVelibApp app, Station s)
        	{
        		this.app = app;
        		this.station = s;
        	}

			public void onClick(View v) {
				ImageButton btn = (ImageButton)v;
				if (btn == null)
					return;
				
				Log.d("StationAdapter", "updateBtn clickd: ");
				ArrayList<Station> list = new ArrayList<Station>();
				Log.d("StationAdapter", station.number().toString());
				list.add(station);
				app.updateStationData(list);
				
			}
        	
        }

        private class FavoriteListener implements Button.OnClickListener
        {
        	private VforVelibApp app;
        	private Station station;
        	
        	public FavoriteListener(VforVelibApp app, Station s)
        	{
        		this.app = app;
        		this.station = s;
        	}

			public void onClick(View v) {
				ImageButton btn = (ImageButton)v;
				if (btn == null)
					return;
				if (!station.getFavorite())
					btn.setImageResource(R.drawable.favoriteicon_notselected);
				else
					btn.setImageResource(R.drawable.favoriteicon_selected);
				Log.d("StationAdapter", "favoriteBtn clickd: ");
				station.setFavorite(!station.getFavorite());
				ArrayList<Station> list = new ArrayList<Station>();
				Log.d("StationAdapter", station.number().toString());
				list.add(station);
				app.updateFavoriteData(list);
				
			}
        	
        }
        
        private class ViewMapListener implements Button.OnClickListener
        {
        	private VforVelibApp app;
        	private Station station;
        	
        	public ViewMapListener(VforVelibApp app, Station s)
        	{
        		this.app = app;
        		this.station = s;
        	}

			public void onClick(View v) {
				ImageButton btn = (ImageButton)v;
				if (btn == null)
					return;
				Log.d("StationAdapter", "viewMapBtn clickd:");
				app.setSelectedStation(station);
				app.getTabHost().setCurrentTab(2);
			}	
        }

}
	
