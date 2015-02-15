package com.bidachon.velib;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class FavoriteActivity extends ListActivity implements Observer{
    
	private StationAdapter adapter;
	private VforVelibApp app;
	//private StationObservable allStationsObservable;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("FavoriteActivity",  "onCreate");
        setContentView(R.layout.favoritelistview);
	       ListView lv = getListView();
	    lv.setTextFilterEnabled(true);
	    app = (VforVelibApp)getApplication();
	    
	    Button updateBtn = (Button)this.findViewById(R.id.UpdateAllBtn);
        if (updateBtn != null)
        	updateBtn.setOnClickListener(new Button.OnClickListener()
            {

				public void onClick(View v) {
					Log.d("FavoriteActivity", "UpdateAll clicked");
					ArrayList<Station> list = new ArrayList<Station>();
					for (int i = 0 ; i < adapter.getCount(); i++)
					{
						list.add(adapter.getItem(i));
					}
					app.updateStationData(list);					
				}
            
            });
	    
	    adapter = new StationAdapter(this, R.layout.stationlistitem, new ArrayList<Station>());
		adapter.setFilter(new FavoriteFilter(adapter));
	    this.setListAdapter(adapter);
		
		loadFavoritesFromFile();
    }
	
	/*
	 * Load list of favorite station Ids from file
	 */
	private void loadFavoritesFromFile()
	{
		//read from favorites.dat file.
		ArrayList<Station> favs = new ArrayList<Station>();
		Integer key;
		FileInputStream fis;

		try {
			Log.d("FavoriteActivity", "opening favorites.dat file");
			fis = openFileInput(getString(R.string.FavDataFile));
			DataInputStream dis = new DataInputStream(fis);
			while(dis.available() > 0)
			{
				key = dis.readInt();
				if (app.allStationsObservable.stationMap().containsKey(key))
				{
				Log.d("FavoriteActivity", "found station id" + key + " in file");
				app.allStationsObservable.stationMap().get(key).setFavorite(true);
				favs.add(app.allStationsObservable.stationMap().get(key));
				
				}
				else
				{
					Log.e("FavoriteActivity", "item could not be found as key in stationMap");
				}
				
			}
			fis.close();
			app.updateFavoriteData(favs);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
	public void update(Observable observable, Object data) {
		if (observable == app.allStationsObservable)
		{
			Log.d("FavoriteActivity","got update from allStationsObservable " + app.allStationsObservable.stations().size());
			
			adapter.setStations(app.allStationsObservable.stations());
			adapter.notifyDataSetChanged();
			adapter.getFilter().filter("thestringisnotusedanyways");
			return;
		}
		else
			Log.e("FavoriteActivity", "observable is of unknown type");
	}
	
	@Override
	public void onStart()
	{
		super.onStart();
		Log.d("FavoriteActivity","onStart");
	}
	
	@Override
	public void onStop()
	{
		Log.d("FavoriteActivity","onStop");
		super.onStop();
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		Log.d("FavoriteActivity","onResume");
		app.allStationsObservable.addObserver(this);
	}
	
	@Override
	public void onPause()
	{
		Log.d("FavoriteActivity","onPause");
		app.allStationsObservable.deleteObserver(this);
		super.onPause();
	}
	
}
