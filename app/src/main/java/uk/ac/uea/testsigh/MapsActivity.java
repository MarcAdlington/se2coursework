package uk.ac.uea.testsigh;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull; //are actually used#
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.common.GooglePlayServicesUtil;


public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient
        .OnConnectionFailedListener, LocationListener {


    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private TextView txtCoordinates, txtCorrectOrIncorrect;
    private Button btnGetCoordinates, btnLocationsUpdates, btnGoBack;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private double currentLong;
    private double currentLat;

    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISPLACEMENT = 10;


    LatLng latLng;
    GoogleMap mGoogleMap;
    SupportMapFragment mFragment;
    Marker currLocationMarker;


    public float lat1;
    public float long1;
    public String name;
    private GoogleMap mMap;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices())
                        buildGoogleApiClient();
                }
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buttonview);
       // setContentView(R.layout.buttonview);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
      //SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        //        .findFragmentById(R.id.map);
        //mapFragment.getMapAsync(this);

        Intent intent = getIntent();

        lat1 = intent.getFloatExtra("lat", 0);
        long1 = intent.getFloatExtra("long", 0);
        name = intent.getStringExtra("name");

        txtCoordinates = (TextView) findViewById(R.id.txtCoordinates);
        txtCorrectOrIncorrect= (TextView) findViewById(R.id.txtCorrectOrIncorrect);
       // btnGetCoordinates = (Button) findViewById(R.id.btnGetCoordinates);
        btnLocationsUpdates = (Button) findViewById(R.id.btnTrackLocation);
        btnGoBack = (Button) findViewById(R.id.btnGoBack);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Run-time request permission
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSION_REQUEST_CODE);
                    goBack(null);
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }



        btnLocationsUpdates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    displayLocation();
                    togglePeriodicLocationUpdates();
                    startMaps();

            }
        });
    }
    public void loadHome(View view) {
        boolean goneThrough = true;
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("goneThrough", goneThrough);
        startActivity(intent);
    }
    public void goBack(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
        if(mGoogleApiClient != null)
        mGoogleApiClient.disconnect();
        super.onStop();

    }


    private void togglePeriodicLocationUpdates() {
        if(!mRequestingLocationUpdates)
        {
            btnLocationsUpdates.setText("Yes");
                mRequestingLocationUpdates = true;
            startLocationUpdates();
            }
        else
        {
            btnLocationsUpdates.setText("Yes");
            mRequestingLocationUpdates = false;
            stopLocationUpdates();
        }


    }
    public void startMaps() {
        Intent intent = new Intent(this, MapsActivitytwo.class);
        intent.putExtra("lat", lat1);
        intent.putExtra("long", long1);
        intent.putExtra("curlat", currentLat);
        intent.putExtra("curlong", currentLong);
        intent.putExtra("name", name);
        startActivity(intent);
    }

    private void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }
    public void loadList(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        setContentView(R.layout.activity_main);
    }
    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();
            currentLat = latitude;
            currentLong = longitude;
            txtCoordinates.setText(name);
        } else
            txtCoordinates.setText(name);
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;
        }

        return true;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
displayLocation();
        if(mRequestingLocationUpdates)
            startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //default lat and long set if for some reason they are not set
        if (lat1 == 0) {
            lat1 = (float) 52.625162;
            long1 = (float) 1.240326;
        }

        LatLng uea = new LatLng(lat1, long1);
        mMap.addMarker(new MarkerOptions().position(uea).title("University of East Anglia"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(uea));
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }
}