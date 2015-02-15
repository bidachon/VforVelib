package com.bidachon.velib;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.widget.TabHost;



/**
 * @author de035891
 * VforVelibActivity is the main activity created on start-up.
 * It creates the TabHost which includes 3 tabs with the following activities:
 * 	- Favorite Tab (FavoriteActivity.class)
 *  - Search Tab (SearchStationActivity.class)
 *  - Map Tab (StationMapActivity.class)
 *  it then loads the list of Velib station from save xml file from previous connection
 *  and call for an update from network  asynchronously.
 */
public class VforVelibActivity extends TabActivity {
    /** Called when the activity is first created. */
    
    private VforVelibApp app;
 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("VforVelibActivity",  "onCreate ");
        
        app = (VforVelibApp)getApplication();
        
        Log.d("VforVelibActivity",  "loadCache");
        
        app.loadCache();
        

        setContentView(R.layout.main);
        
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Reusable TabSpec for each tab
        Intent intent;  // Reusable Intent for each tab

        // Create an Intent to launch an Activity for the tab (to be reused)
        intent = new Intent().setClass(this, FavoriteActivity.class);

        // Initialize a TabSpec for each tab and add it to the TabHost
        spec = tabHost.newTabSpec("favorite").setIndicator("",res.getDrawable(R.drawable.velib_tab_favorite)).setContent(intent);
        tabHost.addTab(spec);
        
        intent = new Intent().setClass(this, SearchStationActivity.class);
        spec = tabHost.newTabSpec("search").setIndicator("",res.getDrawable(R.drawable.velib_tab_search)).setContent(intent);
        tabHost.addTab(spec);
  
        // Do the same for the other tabs
        intent = new Intent().setClass(this, StationMapActivity.class);
        spec = tabHost.newTabSpec("map").setIndicator("",res.getDrawable(R.drawable.velib_tab_map)).setContent(intent);
        tabHost.addTab(spec);

        Log.d("VforVelibActivity",  "setCurrentTab");
        tabHost.setCurrentTab(0);
        app.setTabHost(tabHost);

        Log.d("VforVelibActivity",  "updateFromNetWork");
        app.updateFromNetwork();
        
    }
    
    
}