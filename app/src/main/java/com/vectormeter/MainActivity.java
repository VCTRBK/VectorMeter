package com.vectormeter;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.content.pm.PackageManager;
import android.location.*;
import android.webkit.*;
import android.view.View;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity {
    WebView web;
    LocationManager lm;
    boolean tracking=false;
    final Handler handler=new Handler();
    final SimpleDateFormat fmt=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        lm=(LocationManager)getSystemService(LOCATION_SERVICE);
        web=new WebView(this);
        web.setBackgroundColor(0xFF050505);
        WebSettings s=web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        web.setWebViewClient(new WebViewClient());
        web.addJavascriptInterface(new Bridge(),"Android");
        web.loadUrl("file:///android_asset/index.html");
        setContentView(web);
    }

    void askLocation(){
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);
            return;
        }
        try{
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,2,listener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,5,listener);
        }catch(SecurityException ignored){}
    }

    void stopLocation(){
        try{lm.removeUpdates(listener);}catch(Exception ignored){}
    }

    final LocationListener listener=new LocationListener(){
        @Override public void onLocationChanged(Location l){
            double lat=l.getLatitude(), lon=l.getLongitude();
            web.evaluateJavascript("onGps("+lat+","+lon+")",null);
        }
        public void onProviderEnabled(String p){}
        public void onProviderDisabled(String p){}
        public void onStatusChanged(String p,int s,Bundle e){}
    };

    String now(){return fmt.format(new Date());}

    public class Bridge {
        @JavascriptInterface public void startGps(){
            runOnUiThread(()->{
                tracking=true;
                askLocation();
                web.evaluateJavascript("setStartTime('"+now().replace("'","")+"')",null);
            });
        }
        @JavascriptInterface public void stopGps(){
            runOnUiThread(()->{
                tracking=false; stopLocation();
                web.evaluateJavascript("setEndTime('"+now().replace("'","")+"')",null);
            });
        }
        @JavascriptInterface public void requestOneLocation(){
            askLocation();
        }
        @JavascriptInterface public void savePlan(String json){
            getPreferences(MODE_PRIVATE).edit().putString("last_plan",json).apply();
            runOnUiThread(()->Toast.makeText(MainActivity.this,"VectorPlan tersimpan",Toast.LENGTH_SHORT).show());
        }
    }

    @Override protected void onDestroy(){
        stopLocation();
        super.onDestroy();
    }
}
