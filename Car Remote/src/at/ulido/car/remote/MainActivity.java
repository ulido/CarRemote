package at.ulido.car.remote;

import android.util.Log;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends Activity {	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
    
    public void settingsClicked(MenuItem menuitem) {
    	startActivity(new Intent(this, PreferencesActivity.class));
    }
    
    private class DirRequest extends AsyncTask<String, Void, Void> {
    	private String urlBase;
    	
    	DirRequest(Context context) {
            SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
            Log.i("CarRemote", settings.getAll().toString());
            String hostName = settings.getString("hostName", "192.168.1.123");
            this.urlBase = "http://" + hostName + "/";
    	}
    	
    	@Override
    	protected Void doInBackground(String...dirs) {
    		for (String dir : dirs) {
    			URL url;
    			try {
    				url = new URL(this.urlBase + dir);
    			} catch (MalformedURLException e) {
    				e.printStackTrace();
    				Log.w("CarRemote", Log.getStackTraceString(e));
    				return null;
    			}
    			Log.i("CarRemote", "Contacting " + url.toString());
    			HttpURLConnection urlConnection;
    			try {
        			urlConnection = (HttpURLConnection) url.openConnection();
    				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
    				Log.i("CarRemote", in.toString());
    			} catch (IOException e) {
    				Log.w("CarRemote", Log.getStackTraceString(e));
    				return null;
    			}
    			urlConnection.disconnect();
    		}
    		return null;
    	}
    }

    private void dirRequest(String dir)
    {
    	new DirRequest(this).execute(dir);
    }
    
    public void forwardHandler(View view) {
    	dirRequest("F");
    }

    public void backwardHandler(View view) {
    	dirRequest("B");
    }

    public void stopHandler(View view) {
    	dirRequest("SG");
    }

    public void leftHandler(View view) {
    	dirRequest("LF");
    }

    public void rightHandler(View view) {
    	dirRequest("RF");
    }
}
