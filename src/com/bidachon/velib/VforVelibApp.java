package com.bidachon.velib;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;

public class VforVelibApp extends Application implements AsyncTaskCompleteListener<ArrayList<Station>> {

	private LocationManager locationManager;
	private LocationListener locationListener;
	private TabHost tabHost;
	public StationObservable allStationsObservable = new StationObservable();
	private StationsSaxParser stationParser = new StationsSaxParser();
	private Station _selectedStation = null;
	private HashSet<Integer> _favorites = new HashSet<Integer>();


	public void onCreate( ) {
		super.onCreate();
		Log.d("VforVelibApp",  "onCreate");

		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

		// Define a listener that responds to location updates
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				//makeUseOfNewLocation(location);

			}

			public void onStatusChanged(String provider, int status, Bundle extras) 
			{
				Log.d("SearchStationActivity","onStatusChanged");
			}

			public void onProviderEnabled(String provider) 
			{
				Log.d("SearchStationActivity","onProviderEnabled");
			}

			public void onProviderDisabled(String provider) 
			{
				Log.d("SearchStationActivity","onProviderDisabled");
			}
		};

		//addLocationListener(locationListener);

	}

	public void setTabHost(TabHost tabHost)
	{
		this.tabHost = tabHost;
	}

	public TabHost getTabHost()
	{
		return this.tabHost;
	}

	void setSelectedStation(Station s)
	{
		_selectedStation = s;
	}

	Station getSelectedStation()
	{
		return _selectedStation;
	}

	public void updateFromNetwork()
	{
		new DownloadVelibData(this,this).execute();
	}

	@SuppressWarnings("unchecked")
	public void updateStationData(ArrayList<Station> list)
	{
		Log.d("VforVelibApp", "updateStationData");
		new UpdateStationData(this, this).execute(list);
	}
	
	private void saveFavoritesToFile()
	{
		//save favorite station Ids to a file
		FileOutputStream fos;
		DataOutputStream dos;
		try {
			Log.d("VforVelibApp", "opening favorites.dat file");
			fos = openFileOutput(getString(R.string.FavDataFile), Context.MODE_PRIVATE);
			dos = new DataOutputStream(fos);
			Iterator<Integer> it = _favorites.iterator();
			Integer id;
			while(it.hasNext())
			{
				id = it.next();
				Log.d("VforVelibApp", "write id:" + id + " to favorites.dat file");
				dos.writeInt(id);
			}
			fos.close();			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updateFavoriteData(ArrayList<Station> list)
	{
			for (int i = 0 ; i < list.size(); i++)
			{
				if (allStationsObservable.stations().contains(list.get(i)))
				{
					int index = allStationsObservable.stations().indexOf(list.get(i));
					allStationsObservable.stations().set(index,list.get(i));
					if (list.get(i).getFavorite())
						_favorites.add(list.get(i).number());
					else
						_favorites.remove(list.get(i).number());
				}
				else
					Log.e("VforVelibApp", "could not find station id:" + list.get(i).number() + "in allStationsObservable stationlist");
			}
			
			/* we iterate through the list twice but
			 * if we cannot save the data to the file we can still manage the favorites.
			 */
			saveFavoritesToFile();

		allStationsObservable.notifyChange();
	}

	public void onTaskComplete(ArrayList<Station> result) {
		Log.d("VfoVelibApp","onTaskComplete");
		for (int i = 0 ; i < result.size(); i++)
		{
			if (allStationsObservable.stations().contains(result.get(i)))
			{
				int index = allStationsObservable.stations().indexOf(result.get(i));
				allStationsObservable.stations().set(index,result.get(i));

			}

		}
		allStationsObservable.notifyChange();	
	}

	public void addLocationListener(LocationListener listener)
	{
		Log.d("VforVelibApp", "addLocationListener");
		// Register the listener with the Location Manager to receive location updates

		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 20, listener);
	}

	public void removeLocationListener(LocationListener listener)
	{
		Log.d("VforVelibApp", "removeLocationListener");
		// Register the listener with the Location Manager to receive location updates
		locationManager.removeUpdates(listener);
	}

	public void loadCache()
	{
		Log.d("VforVelibApp","loadCache()");
		allStationsObservable.setStations(stationParser.parseStations(getResources().openRawResource(R.raw.carto)));

	}


	public class StationObservable extends Observable {

		private ArrayList<Station> _stations = new ArrayList<Station>();
		private Hashtable<Integer, Station> _stationMap = new Hashtable<Integer, Station>();

		public StationObservable()
		{

		}

		@Override
		public void addObserver(Observer obs)
		{
			super.addObserver(obs);
			obs.update(this, new Object());

		}

		public synchronized void setStations(ArrayList<Station> stations)
		{
			_stations.clear();
			_stations.addAll(stations);
			Iterator<Station> it = stations.iterator();
			while (it.hasNext())
			{
				Station s = it.next();
				_stationMap.put(s.number(), s);
			}
			setChanged();
			notifyObservers();
		}

		public void notifyChange()
		{
			setChanged();
			notifyObservers();			
		}



		public synchronized ArrayList<Station> stations()
		{
			return _stations;
		}

		public synchronized Hashtable<Integer, Station> stationMap()
		{
			return _stationMap;
		}

	}








}
