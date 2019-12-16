package com.example.findmyspot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.findmyspot.data.model.Post;
import com.example.findmyspot.data.remote.APIService;
import com.example.findmyspot.data.remote.ApiUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.ByteArrayOutputStream;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button btnCapture,btnToStatsAct,btnSendData;
    private ImageView imgCapture;
    private static final int Image_Capture_Code = 1;
    private FusedLocationProviderClient mFusedLocationClient;
    TextView latTextView, lonTextView, macTextView, timeTextView;
    Date currentTime;
    String latitudeJSON, longitudeJSON, MACAddressJSON, encodedImage;

    private APIService mAPIService;

    int PERMISSION_ID = 44;
    int PERMISSION_ID2 = 45;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAPIService = ApiUtils.getAPIService();

        /*
        StrictMode.ThreadPolicy policy = new
        StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        */

        latTextView = findViewById(R.id.latTextView);
        lonTextView = findViewById(R.id.lonTextView);
        macTextView = findViewById(R.id.macTextView);
        timeTextView = findViewById(R.id.timeTextView);
        btnToStatsAct = findViewById(R.id.btnToStatsAct);
        btnSendData = findViewById(R.id.btnSendData);
        btnCapture =(Button)findViewById(R.id.btnTakePicture);
        imgCapture = (ImageView) findViewById(R.id.capturedImage);
        btnCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cInt,Image_Capture_Code);
            }
        });
        btnToStatsAct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), StatsActivity.class);
                myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(myIntent);
            }
        });

        btnSendData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(encodedImage == null )
                    encodedImage = "IMAGE";

                String currentTimeJSON;
                if(currentTime == null )
                    currentTimeJSON = "YEAR ZERO";
                else
                    currentTimeJSON = currentTime.toString();



                ArrayList<String> arrayJSON = new ArrayList<>();
                arrayJSON.add(MACAddressJSON);
                arrayJSON.add(latitudeJSON);
                arrayJSON.add(longitudeJSON);
                if(encodedImage != null )
                    arrayJSON.add(encodedImage);
                else
                    arrayJSON.add("image");
                if(currentTime != null )
                    arrayJSON.add(currentTime.toString());
                else
                    arrayJSON.add("YEAR ZERO");

                //AsyncPostT asyncT = new AsyncPostT();
                //asyncT.execute(arrayJSON);


                sendPost(MACAddressJSON,latitudeJSON,longitudeJSON,encodedImage,currentTimeJSON);

                 /**/
                Toast.makeText(getApplicationContext(), "posting", Toast.LENGTH_SHORT).show();
            }
        });

        enableInternetPermission();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        MACAddressJSON =getMacAddr();
        macTextView.setText("MAC: "+MACAddressJSON);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap capturedImage = (Bitmap) data.getExtras().get("data");
                imgCapture.setImageBitmap(capturedImage);
                encodedImage = getStringFromBitmap(capturedImage);
                getLastLocation();
                currentTime = Calendar.getInstance().getTime();
                timeTextView.setText("TIME: "+currentTime.toString());
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }

    //@SuppressLint("MissingPermission")
    private void getLastLocation(){
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(
                        new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {
                                requestNewLocationData();
                                /*
                                Location location = task.getResult();
                                if (location == null) {
                                    requestNewLocationData();
                                } else {
                                    latTextView.setText("Lat: "+location.getLatitude()+"");
                                    lonTextView.setText("Lon: "+location.getLongitude()+"");
                                    latitudeJSON = location.getLatitude()+"";
                                    longitudeJSON = location.getLongitude()+"";
                                }

                                 */
                            }
                        }
                );
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    //@SuppressLint("MissingPermission")
    private void requestNewLocationData(){

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(0);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
        );
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            latTextView.setText("Lat: "+mLastLocation.getLatitude()+"");
            lonTextView.setText("Lon: "+mLastLocation.getLongitude()+"");
            latitudeJSON = mLastLocation.getLatitude()+"";
            longitudeJSON = mLastLocation.getLongitude()+"";

            //Toast.makeText(getApplicationContext(), "testToast", Toast.LENGTH_LONG).show();

            //TODO SEND LOCATION ALONG WITH TIME AND PHOTO
        }
    };

    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private boolean checkPermissionsInternet(){
        if(ActivityCompat.checkSelfPermission(this,Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED)
            return true;
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET},
                PERMISSION_ID
        );
    }
    private void requestPermissionInternet(){
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.INTERNET},
                PERMISSION_ID2
        );
    }

    private void enableInternetPermission(){
        if(!checkPermissionsInternet()){
            requestPermissionInternet();
        }
    }

    private boolean isLocationEnabled(){
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Granted. Start getting the location information
            }
        }
        if (requestCode == PERMISSION_ID2){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
        }
    }

    private String getStringFromBitmap(Bitmap bitmapPicture) {
        final int COMPRESSION_QUALITY = 100;
        String encodedImage;
        ByteArrayOutputStream byteArrayBitmapStream = new ByteArrayOutputStream();
        bitmapPicture.compress(Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
                byteArrayBitmapStream);
        byte[] b = byteArrayBitmapStream.toByteArray();
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encodedImage;
    }

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    String hex = Integer.toHexString(b & 0xFF);
                    if (hex.length() == 1)
                        hex = "0".concat(hex);
                    res1.append(hex.concat(":"));
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
        }
        return "";
    }

    public void sendPost(String MACAddress, String latitudeJSON, String longitudeJSON, String encodedImage, String currentTime  ) {
        mAPIService.savePost(MACAddress, latitudeJSON, longitudeJSON,encodedImage,currentTime).enqueue(new Callback<Post>() {
            @Override
            public void onResponse(Call<Post> call, Response<Post> response) {

                if(response.isSuccessful()) {
                    //showResponse(response.body().toString());
                    Log.i("TEEEST", "post submitted to API." + response.body().toString());
                    Log.i("TEEEST", "post submitted to API." + response.message());
                    Log.i("TEEEST", "post submitted to API." + response.code());
                }
            }

            @Override
            public void onFailure(Call<Post> call, Throwable t) {
                Log.e("TEEEST", "Unable to submit post to API.");
            }
        });
    }



    @Override
    public void onResume(){
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }

    }
}
