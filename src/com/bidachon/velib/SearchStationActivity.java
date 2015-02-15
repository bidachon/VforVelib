package com.bidachon.velib;

import java.util.Observable;
import java.util.Observer;

import android.app.ListActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class SearchStationActivity extends ListActivity implements Observer {

	private VforVelibApp app;
	private StationAdapter adapter;

	// private StationObservable nearByStationsObservable;
	// private StationObservable allStationsObservable;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("SearchStationActivity", "onCreate");
		app = (VforVelibApp) getApplication();
		setContentView(R.layout.stationlistview);
		ListView lv = getListView();

		EditText filterText = (EditText) this
				.findViewById(R.building_list.search_box);
		filterText.addTextChangedListener(filterTextWatcher);


		adapter = new StationAdapter(this, R.layout.stationlistitem,
				app.allStationsObservable.stations());
		adapter.setFilter(new StringFilter(adapter));
		adapter.getFilter().filter("");
		this.setListAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// When clicked, show a toast with the TextView text
				StationAdapter a = (StationAdapter) parent.getAdapter();
				if (a != null) {
					Station s = a.getItem(position);

					Toast.makeText(getApplicationContext(), s.fullAddress(),
							Toast.LENGTH_SHORT).show();
				}
			}
		});

		adapter.notifyDataSetChanged();

	}

	private TextWatcher filterTextWatcher = new TextWatcher() {

		public void afterTextChanged(Editable s) {
		}

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			adapter.getFilter().filter(s);
		}

	};

	public void update(Observable observable, Object data) {
		Log.d("SearchStationActivity", "got update from Observable");
		if (observable == app.allStationsObservable) {
			Log.d("SearchStationActivity",
					"got update from allStationsObservable");

			adapter.setStations(app.allStationsObservable.stations());
			adapter.notifyDataSetChanged();
			return;
		} else
			Log.e("SearchStationActivity", "observable is of unknown type");
	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("SearchStationActivity", "onStart");
		
	}

	@Override
	public void onStop() {
		
		Log.d("SearchStationActivity", "onStop");
		super.onStop();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("SearchStationActivity", "onResume");
		app.allStationsObservable.addObserver(this);
		
	}

	@Override
	public void onPause() {
		
		Log.d("SearchStationActivity", "onPause");
		app.allStationsObservable.deleteObserver(this);
		super.onPause();
	}

}
