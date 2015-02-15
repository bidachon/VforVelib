package com.bidachon.velib;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;

public class StationMapActivity extends MapActivity implements Observer {
	/** Called when the activity is first created. */

	private VforVelibApp app;
	private MapView mapView;
	private MapController myMapController;
	private List<Overlay> mapOverlays;
	private MyLocationOverlay myLocationOverlay;
	private boolean displayMyLocation = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("StationMapActivity", "onCreate");
		setContentView(R.layout.stationmapview);
		app = (VforVelibApp) getApplication();
		mapView = (MapView) findViewById(R.id.mapview);

		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapOverlays = mapView.getOverlays();

		mapView.setBuiltInZoomControls(true);
		mapView.setSatellite(false); // Set satellite view
		myMapController = mapView.getController();
		myMapController.setZoom(18); // Fixed Zoom Level

		ImageButton updateBtn = (ImageButton) findViewById(R.id.button1);

		if (updateBtn != null)
			updateBtn.setOnClickListener(new UpdateBtnListener());

		ImageButton localizeBtn = (ImageButton) findViewById(R.id.button2);

		if (localizeBtn != null)
			localizeBtn.setOnClickListener(new LocalizeBtnListener());
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public void onStop() {

		Log.d("StationMapActivity", "onStop");
		if (displayMyLocation)
			myLocationOverlay.disableMyLocation();

		super.onStop();
	}

	public void onStart() {
		super.onStart();
		Log.d("StationMapActivity", "onStart");

	}

	public void onPause() {
		Log.d("StationMapActivity", "onPause");
		app.allStationsObservable.deleteObserver(this);
		super.onPause();
	}

	public void onResume() {
		super.onResume();
		Log.d("StationMapActivity", "onResume");
		
		app.allStationsObservable.addObserver(this);
		
		if (app.getSelectedStation() != null) {
			myMapController.setCenter(new GeoPoint((int) (app.getSelectedStation()
				.location().latitude() * 1000000), (int) (app
				.getSelectedStation().location().longitude() * 1000000)));
			app.setSelectedStation(null);
		}
		Log.d("StationMapActivity", "after adding myself as observer");
		ArrayList<Station> stations = findNearByStations(mapView.getMapCenter(), mapView.getLatitudeSpan(), mapView.getLongitudeSpan());

		addOverlays(stations);
		
		app.updateStationData(stations);
		

		// app.addLocationListener(locationListener);
		if (displayMyLocation)
			myLocationOverlay.enableMyLocation();
		

	}

	private void addOverlays(ArrayList<Station> list) {
		Log.d("StationMapActivity", "addOverlays");
		Drawable drawable = this.getResources().getDrawable(
				R.drawable.ic_map_marker_unviewed);
		mapOverlays.clear();
		FrameLayout markerLayout = (FrameLayout) getLayoutInflater().inflate(
				R.layout.stationoverlay, null);
		StationItemizedOverlay itemizedoverlay = new StationItemizedOverlay(
				drawable, this);
		for (int i = 0; i < list.size(); i++) {
			int lat = (int) (list.get(i).location().latitude() * 1E6);
			int lng = (int) (list.get(i).location().longitude() * 1E6);
			GeoPoint point = new GeoPoint(lat, lng);
			// String label = list.get(i).getBikes().toString();
			StationItem overlayitem = new StationItem(point, list.get(i)
					.getBikes(), list.get(i).getStands(), list.get(i).fullAddress(),
					markerLayout, this);
			itemizedoverlay.addOverlay(overlayitem);
		}
		mapOverlays.add(itemizedoverlay);
		if (displayMyLocation)
			mapOverlays.add(myLocationOverlay);
		else
			mapOverlays.remove(myLocationOverlay);
		mapView.invalidate();
		

	}

	private ArrayList<Station> findNearByStations(GeoPoint center, int latitudeSpan, int longitudeSpan) {
		ArrayList<Station> result = new ArrayList<Station>();
		Log.d("StationMapActivity","entering findNearByStations");

		ArrayList<Station> allStations = new ArrayList<Station>();
		allStations.addAll(app.allStationsObservable.stations());
		//float[] distance = new float[3];
		Double latLower = new Double(center.getLatitudeE6()-(latitudeSpan/2));
		Double latUpper = new Double(center.getLatitudeE6()+(latitudeSpan/2));
		Double lngLower = new Double(center.getLongitudeE6()-(longitudeSpan/2));
		Double lngUpper = new Double(center.getLongitudeE6()+(longitudeSpan/2));

		for (int i = 0; i < allStations.size(); i++) {
			
			Station s  = allStations.get(i);
			if (! ((s.location().latitude() > latLower/1000000) && (s.location().latitude() < latUpper/1000000)))
				continue;
			if (! ((s.location().longitude() > lngLower/1000000) && (s.location().longitude() < lngUpper/1000000)))
				continue;
			result.add(allStations.get(i));	
			Log.d("StationMapActivity", "Found station close enough");
			/*
			Location.distanceBetween(lat/1000000, lng/1000000, allStations.get(i).location().latitude(), allStations
							.get(i).location().longitude(), distance);

			if (distance[0] < 300) {
				Log.d("StationMapActivity", "Found station close enough");
				result.add(allStations.get(i));
			}*/
			//Log.d("test",new DecimalFormat("#.##").format(distance[0]));
		}
		
		return result;
	}

	private class UpdateBtnListener implements Button.OnClickListener {

		public UpdateBtnListener() {
		}

		public void onClick(View v) {
			ImageButton btn = (ImageButton) v;
			if (btn == null)
				return;
			Log.d("StationMapActivity", "updateBtn clickd: ");
			ArrayList<Station> stations = findNearByStations(mapView.getMapCenter(), mapView.getLatitudeSpan(), mapView.getLongitudeSpan());
			addOverlays(stations);
			app.updateStationData(stations);
			

		}
	}

	private void toggleCurrentLocation() {
		if (!displayMyLocation) {
			displayMyLocation = true;
			mapOverlays.add(myLocationOverlay);
			myLocationOverlay.enableMyLocation();
			myLocationOverlay.runOnFirstFix(new MoveToCurrentLocation());
		} else {
			displayMyLocation = false;
			mapOverlays.remove(myLocationOverlay);
			myLocationOverlay.disableMyLocation();
		}

	}

	private class LocalizeBtnListener implements Button.OnClickListener {

		public LocalizeBtnListener() {
		}

		public void onClick(View v) {
			ImageButton btn = (ImageButton) v;
			if (btn == null)
				return;
			Log.d("StationMapActivity", "localizeBtn clickd: ");
			toggleCurrentLocation();
			mapView.invalidate();

		}
	}

	private class MoveToCurrentLocation implements Runnable {

		public void run() {
			myMapController.animateTo(myLocationOverlay.getMyLocation());
		}

	}

	public void update(Observable observable, Object data) {
		// TODO Auto-generated method stub
		Log.d("StationMapActivity", "got stations update");
		addOverlays(findNearByStations(mapView.getMapCenter(), mapView.getLatitudeSpan(), mapView.getLongitudeSpan()));
	}
}
