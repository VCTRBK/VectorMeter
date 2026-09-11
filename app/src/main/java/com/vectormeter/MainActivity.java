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
import android.content.Intent;
import android.net.Uri;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;
import java.io.*;
import org.json.*;

public class MainActivity extends Activity {
    WebView web;
    LocationManager lm;
    boolean tracking=false;
    final Handler handler=new Handler();
    final SimpleDateFormat fmt=new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
    private ValueCallback<Uri[]> filePathCallback;
    private static final int FILE_CHOOSER=7001;

    @Override public void onCreate(Bundle b){
        super.onCreate(b);
        lm=(LocationManager)getSystemService(LOCATION_SERVICE);
        web=new WebView(this);
        web.setBackgroundColor(0xFF050505);
        WebSettings s=web.getSettings();
        s.setJavaScriptEnabled(true); s.setDomStorageEnabled(true); s.setAllowFileAccess(true); s.setAllowContentAccess(true);
        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient(){
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params){
                if(filePathCallback!=null) filePathCallback.onReceiveValue(null);
                filePathCallback=callback;
                Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                i.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"image/*","video/*"});
                try{startActivityForResult(i,FILE_CHOOSER);}catch(Exception e){filePathCallback=null;return false;}
                return true;
            }
        });
        web.addJavascriptInterface(new Bridge(),"Android");
        web.loadUrl("file:///android_asset/index.html");
        setContentView(web);
    }

    @Override protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==FILE_CHOOSER){
            if(filePathCallback!=null){Uri[] r=null;if(resultCode==RESULT_OK&&data!=null&&data.getData()!=null)r=new Uri[]{data.getData()};filePathCallback.onReceiveValue(r);filePathCallback=null;}
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    void askLocation(){
        if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},100);return;}
        try{lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,2,listener);lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,3000,5,listener);}catch(SecurityException ignored){}
    }
    void stopLocation(){try{lm.removeUpdates(listener);}catch(Exception ignored){}}
    final LocationListener listener=new LocationListener(){
        @Override public void onLocationChanged(Location l){web.evaluateJavascript("onGps("+l.getLatitude()+","+l.getLongitude()+")",null);}
        public void onProviderEnabled(String p){} public void onProviderDisabled(String p){} public void onStatusChanged(String p,int s,Bundle e){}
    };
    String now(){return fmt.format(new Date());}

    public class Bridge {
        @JavascriptInterface public void startGps(){runOnUiThread(()->{tracking=true;askLocation();web.evaluateJavascript("setStartTime('"+now().replace("'","")+"')",null);});}
        @JavascriptInterface public void stopGps(){runOnUiThread(()->{tracking=false;stopLocation();web.evaluateJavascript("setEndTime('"+now().replace("'","")+"')",null);});}
        @JavascriptInterface public void requestOneLocation(){askLocation();}
        @JavascriptInterface public void reverseGeocode(double lat,double lon,int requestId){
            new Thread(()->{
                String result="Lokasi ("+String.format(Locale.US,"%.5f, %.5f",lat,lon)+")";HttpURLConnection c=null;
                try{
                    String url="https://nominatim.openstreetmap.org/reverse?format=jsonv2&addressdetails=1&zoom=18&lat="+URLEncoder.encode(String.valueOf(lat),"UTF-8")+"&lon="+URLEncoder.encode(String.valueOf(lon),"UTF-8")+"&accept-language=id";
                    c=(HttpURLConnection)new URL(url).openConnection();c.setRequestMethod("GET");c.setConnectTimeout(10000);c.setReadTimeout(10000);c.setRequestProperty("User-Agent","VectorMeter/1.3.0 (Android; OpenStreetMap reverse geocoding)");c.setRequestProperty("Accept","application/json");
                    int code=c.getResponseCode();if(code>=200&&code<300){BufferedReader br=new BufferedReader(new InputStreamReader(c.getInputStream(),"UTF-8"));StringBuilder sb=new StringBuilder();String line;while((line=br.readLine())!=null)sb.append(line);br.close();JSONObject o=new JSONObject(sb.toString());JSONObject a=o.optJSONObject("address");String name=o.optString("name","").trim();String road=a!=null?a.optString("road","").trim():"";String neighbourhood=a!=null?a.optString("neighbourhood","").trim():"";if(neighbourhood.isEmpty()&&a!=null)neighbourhood=a.optString("suburb","").trim();String city=a!=null?a.optString("city","").trim():"";if(city.isEmpty()&&a!=null)city=a.optString("town","").trim();if(city.isEmpty()&&a!=null)city=a.optString("municipality","").trim();LinkedHashSet<String> parts=new LinkedHashSet<>();if(!name.isEmpty())parts.add(name);if(!road.isEmpty())parts.add(road);if(!neighbourhood.isEmpty())parts.add(neighbourhood);if(!city.isEmpty())parts.add(city);if(parts.isEmpty()){String display=o.optString("display_name","").trim();if(!display.isEmpty())result=display;}else result=String.join(", ",parts);}
                }catch(Exception ignored){}finally{if(c!=null)c.disconnect();}
                String safe=JSONObject.quote(result);String js="onReverseGeocode("+requestId+","+safe+")";runOnUiThread(()->web.evaluateJavascript(js,null));
            }).start();
        }
    }
    @Override protected void onDestroy(){stopLocation();super.onDestroy();}
}
