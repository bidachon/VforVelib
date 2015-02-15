package com.bidachon.velib;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


interface AsyncTaskCompleteListener<T> {
	   public void onTaskComplete(T result);
	}

	 //To use the AsyncTask, it must be subclassed  
    class DownloadVelibData extends AsyncTask<Void, Integer, Void>  
    { 
    	
    	private Context context;
    	private ArrayList<Station> item;
    	private AsyncTaskCompleteListener<ArrayList<Station>> callback;
    	
    	DownloadVelibData(Context context,AsyncTaskCompleteListener<ArrayList<Station>> callback)
    	{
    		this.context = context;
    		item = new ArrayList<Station>();
    		this.callback = callback;
    	}
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected Void doInBackground(Void... params)  
        {
        	Log.d("DownloadVelibData", "doInBackground()");
			StationsSaxParser parser = new StationsSaxParser();
			item = parser.parseStations(parser.getInputStreamFromUrl(context.getString(R.string.VelibUrl)));
        	return null;  

        }  

        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(Void result)  
        {  
            callback.onTaskComplete(item);
      	  
        }  
    
	
}
    
    class UpdateStationData extends AsyncTask<ArrayList<Station>, Station, ArrayList<Station>>  
    { 
    	
    	
    	private Context context;
    	private AsyncTaskCompleteListener<ArrayList<Station>> callback;
    	
    	UpdateStationData(Context context,AsyncTaskCompleteListener<ArrayList<Station>> callback)
    	{
    		this.context = context;
    		this.callback = callback;
    	}
        //Before running code in separate thread  
        @Override  
        protected void onPreExecute()  
        {  
        }  
  
        //The code to be executed in a background thread.  
        @Override  
        protected ArrayList<Station> doInBackground(ArrayList<Station>... params)  
        {
        	Log.d("DownloadVelibData", "doInBackground()");
			StationsSaxParser parser = new StationsSaxParser();
			ArrayList<Station> result = new ArrayList<Station>();
			result.addAll(params[0]);
			
		
			Station tmp;
			for (int i = 0 ; i < params[0].size() ; i++)
			{
				tmp = parser.parseStationData(parser.getInputStreamFromUrl(context.getString(R.string.StationUrl) + result.get(i).number().toString()));
				result.get(i).setBikes(tmp.getBikes());
				result.get(i).setStands(tmp.getStands());
				result.get(i).setTotal(tmp.getTotal());
				result.get(i).setTicket(tmp.getTicket());
				publishProgress(tmp);
			}
			
			return result;
        }
        
        protected void onProgressUpdate(Station... progress) {
            Log.d("DownloadVelibData",progress[0].toString());
        }

        //after executing the code in the thread  
        @Override  
        protected void onPostExecute(ArrayList<Station> result)  
        {  
            callback.onTaskComplete(result);
      	  
        }
 
    }
    
 
    
