package com.bidachon.velib;

import java.util.ArrayList;

import android.util.Log;
import android.widget.Filter;

public class FavoriteFilter extends Filter
	{
		private StationAdapter adapter;
		
		public FavoriteFilter(StationAdapter a)
		{
			this.adapter = a;
		}

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
	        FilterResults result = new FilterResults();
	        

	        	Log.d("FavoriteFilter", "string search");
	        	ArrayList<Station> filteredItems = new ArrayList<Station>();
	        	ArrayList<Station> localItems = new ArrayList<Station>();
	        	localItems.addAll(adapter.allStations());
	            for(int i = 0; i < localItems.size(); i++)
	            {
	            	Station m = localItems.get(i);
	            	
	            	if (m.getFavorite())
	            	{
	            		Log.d("FavoriteFilter", "found station: " + m.toString());
	            		filteredItems.add(m);
	            	}
	            	

	            }
	            Log.d("FavoriteFilter", "found: " + filteredItems.size());
	        	result.count = filteredItems.size();
	            result.values = filteredItems;
	            

	        return result;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			adapter.setFilteredStations((ArrayList<Station>)results.values);
	        if (results.count > 0) { 	
	        	Log.d("FavoriteFilter", "publishing results");
	        	adapter.notifyDataSetChanged();
	        } else {
	        	adapter.notifyDataSetInvalidated();
	        }
			
		}
	}