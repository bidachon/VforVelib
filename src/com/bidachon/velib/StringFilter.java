package com.bidachon.velib;

import java.util.ArrayList;

import android.location.Location;
import android.util.Log;
import android.widget.Filter;


	public class StringFilter extends Filter
    {

    	private StationAdapter adapter;
    	private Location location = null;
    	private double range = 0;
    	
    	public StringFilter(StationAdapter a)
    	{
    		adapter = a;
    	}
	
	
	public void setLocation(Location l)
	{
		if (location != null)
			return;
		this.location = new Location(l);
	}
	
	public void setRange(double range)
	{
		this.range = range;
	}
	
	protected FilterResults performFiltering(CharSequence constraint) {
        String s = constraint.toString();
        FilterResults result = new FilterResults();
        if (s.equals("LOCATION"))
        {
        	Log.d("StringFilter","LOCATION");
        	if (location == null)
        	{
        		Log.e("StringFilter","location is null");
        		return result;
        	}
        	else
        	{
        		Log.d("StringFilter","size: " + adapter.allStations().size());
        	}	
        	ArrayList<Station> filteredItems = new ArrayList<Station>();
            for(int i = 0; i < adapter.allStations().size(); i++)
            {
                Station m = adapter.allStations().get(i);
                float[] distance = new float[3];
                try {
                	if (location == null)
                		Log.e("er", "location is null");
                //Log.d("LocationFilter","i:" + i + " " + m.location().latitude() + " " + m.location().longitude());		
                Location.distanceBetween(location.getLatitude(), location.getLongitude(), m.location().latitude(), m.location().longitude(), distance);
                }
                catch (IllegalArgumentException e)
                {
                	Log.e("StringFilter", "error:" + e.toString());
                }
                catch(Exception e)
                {
                	Log.e("", "unknown error:" + e.toString());
                }
            
                if (distance[0] < 500)
                {
                	Log.d("StringFilter","distance < " + range);
                    m.setDistanceFromCurrent((int) distance[0]);
                	filteredItems.add(m);
                }
            }
            result.count = filteredItems.size();
            result.values = filteredItems;
        }
        else if (!s.isEmpty())
        {
        	Log.d("StringFilter", "string search");
        	ArrayList<Station> filteredItems = new ArrayList<Station>();
            for(int i = 0; i < adapter.allStations().size(); i++)
            {
            	Station m = adapter.allStations().get(i);
            	s = s.toLowerCase();
            	if (m.name().toLowerCase().contains(s) || m.fullAddress().toLowerCase().contains(s))
            	{
            		Log.d("StringFilter", "found station: " + m.toString());
            		filteredItems.add(m);
            	}
            	

            }
            Log.d("StringFilter", "found: " + filteredItems.size());
        	result.count = filteredItems.size();
            result.values = filteredItems;
            
        }
            else
            {
                synchronized(this)
                {
                    result.values = adapter.allStations();
                    result.count = adapter.allStations().size();
                }
            }

        return result;
        
	}

	@Override
	protected void publishResults(CharSequence constraint, FilterResults results) {
        if (results.count > 0) {

        	adapter.setFilteredStations((ArrayList<Station>)results.values);
        	
        	Log.d("StringFilter", "publishing results");
        	adapter.notifyDataSetChanged();
        } else {
        	adapter.notifyDataSetInvalidated();
        }

	}

}
