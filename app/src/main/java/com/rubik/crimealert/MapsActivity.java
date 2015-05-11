package com.rubik.crimealert;

import android.graphics.Color;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

public class MapsActivity extends FragmentActivity  implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, OnMapReadyCallback {

    private Marker myLocationMarker;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    Polygon[][] polygons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        setUpMapIfNeeded();
        buildGoogleApiClient();

  //      mGoogleApiClient.connect();
        createLocationRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

        if (mGoogleApiClient.isConnected()) {
            startLocationUpdates();
        } else {

            Log.d("MAP", "client not connected");
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }


    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.


            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }

            SupportMapFragment mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map));
            mapFragment.getMapAsync(this);
        }
    }


    protected void startLocationUpdates() {

        Log.d("MAP", "LocationUpdates started");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private void setUpMap() {
        myLocationMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.my_location)));
    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Log.d("MAP", "LocationRequest created");
    }


    @Override
    public void onConnected(Bundle bundle) {

        //   if (mRequestingLocationUpdates) {
        startLocationUpdates();
        //   }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {

        myLocationMarker.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));
        Log.d("MAP", "Location changed " + location.toString());

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap.setMyLocationEnabled(true);

        polygons = new Polygon[32][32];



        double latStep = (G.dhakaEndLatLng.latitude - G.dhakaStartLatLng.latitude)/32.0;
        double lngStep = (G.dhakaEndLatLng.longitude - G.dhakaStartLatLng.longitude)/32.0;

        for(int i=0; i<32; ++i){
            for(int j=0; j<32; ++j){

                polygons[i][j] =  mMap.addPolygon(new PolygonOptions()
                                .add(
                                        new LatLng(G.dhakaStartLatLng.latitude + i*latStep, G.dhakaStartLatLng.longitude + j*lngStep),
                                        new LatLng(G.dhakaStartLatLng.latitude + (i+1)*latStep, G.dhakaStartLatLng.longitude + j*lngStep),
                                        new LatLng(G.dhakaStartLatLng.latitude + (i+1)*latStep, G.dhakaStartLatLng.longitude + (j+1)*lngStep),
                                        new LatLng(G.dhakaStartLatLng.latitude + i*latStep, G.dhakaStartLatLng.longitude + (j+1)*lngStep)

                                )
                                .fillColor(Color.argb(200, (int)(Math.random()*255), 0,0))
                                .strokeColor(Color.TRANSPARENT)


                );
            }

        }

    }
}
