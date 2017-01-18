package uk.ac.uea.testsigh;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.uea.framework.DataParser;

public class MapsActivitytwo extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double currentLong;
    private double currentLat;
    private float lat1;
    private float long1;
    private Button showCurrentLoc;
    private Button showPOI;
    TextView distance;


    LatLng you = new LatLng(currentLat, currentLong);
    LatLng uea = new LatLng(lat1, long1);
    ArrayList<LatLng> MarkerPoints;
    int timesClicked = 0;
    private float distanceInMeters;
    private float timeToWalk;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        /*
        *   Gathers the lat and long from previous activity
         */
        Intent intent = getIntent();

        lat1 = intent.getFloatExtra("lat", 0);
        long1 = intent.getFloatExtra("long", 0);
        currentLat = intent.getDoubleExtra("curlat", 0);
        currentLong = intent.getDoubleExtra("curlong", 0);
        MarkerPoints = new ArrayList<>();




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
            Toast.makeText(getApplicationContext(),"Something went wrong, default location set",Toast.LENGTH_LONG).show();

        }


        uea = new LatLng(lat1, long1);
        mMap.addMarker(new MarkerOptions().position(uea).title("Destination"));
        mMap.animateCamera(CameraUpdateFactory.newLatLng(uea));


        you = new LatLng(currentLat, currentLong);
      //  you = new LatLng(52.62154, 1.2395); //comment this out and remove // from above
      //  currentLat = 52.62154; //comment out
      //  currentLong = 1.2395; //comment out

        mMap.addMarker(new MarkerOptions().position(you).title("You").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));




        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(you));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(16));

        showCurrentLoc = (Button) findViewById(R.id.showCurrentLoc);
        showPOI = (Button) findViewById(R.id.showPOIloc);

        distance = (TextView) findViewById(R.id.distance);
        distance.setText(".  .  .");
        /*
        *   If the map is clicked, the an
        *
         */
        showCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(you));
                Toast.makeText(getApplicationContext(), "Navigated to:" + currentLat + " lat, " + currentLong + " long.", Toast.LENGTH_SHORT).show();


            }
        });


        showPOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.newLatLng(uea));
                Toast.makeText(getApplicationContext(), "Navigated to:" + currentLat + " lat, " + currentLong + " long.", Toast.LENGTH_SHORT).show();
            }
        });

        Toast.makeText(getApplicationContext(), "Tap the screen to begin your route!", Toast.LENGTH_LONG).show();

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            public void onMapClick(LatLng point) {

               timesClicked++;
                if (timesClicked > 1) {
                    Toast.makeText(getApplicationContext(), "You are already on route!", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Toast.makeText(getApplicationContext(), "You have started your route!", Toast.LENGTH_LONG).show();
                }

                MarkerPoints.add(0, you);
                MarkerPoints.add(1, uea);

                // Checks, whether start and end locations are captured

                    LatLng origin = MarkerPoints.get(0);
                    LatLng dest = MarkerPoints.get(1);

                System.out.println(MarkerPoints.get(0));
                System.out.println(MarkerPoints.get(1));

                    if (origin != you && dest != uea) {
                        origin = you;
                        dest = uea;
                    }

                    String url = getUrl(origin, dest);



                workOutTime();


                Log.d("onMapClick", url.toString());
                FetchUrl FetchUrl = new FetchUrl();

                // Start downloading json data from Google Directions API
                FetchUrl.execute(url);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(you));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(18));



        }
    });
}

    private void workOutTime() {
        //setup both locations

        Location locationYou = new Location("");
        locationYou.setLatitude(currentLat);
        locationYou.setLongitude(currentLong);

        Location locationUea = new Location("");
        locationUea.setLatitude(lat1);
        locationUea.setLongitude(long1);


        distanceInMeters = locationYou.distanceTo(locationUea);
        //541.9115m

        System.out.println(locationYou.distanceTo(locationUea));

        timeToWalk = Math.round(distanceInMeters*60) / (1000*4);

        int timeToWalkInt = (int)timeToWalk;



        if (timeToWalk < 1){
            distance.setText("The estimated walking time is under one minute");
    } else {
            distance.setText("The estimated walking time is: "+timeToWalkInt+" minutes.");
        }

    }
    private String getUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;


        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;


        return url;
    }

    /**
     * A method to download json data from url
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }



    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask",jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask","Executing routes");
                Log.d("ParserTask",routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask",e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);

                Log.d("onPostExecute","onPostExecute lineoptions decoded");

            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                mMap.addPolyline(lineOptions);
            }
            else {
                Log.d("onPostExecute","without Polylines drawn");
            }
        }
    }
}
