package com.superlogico.birraviso.activity;

/**
 * Created by Daniel on 7/3/2017.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.superlogico.birraviso.DataHolder;
import com.superlogico.birraviso.MainActivity;
import com.superlogico.birraviso.Manifest;
import com.superlogico.birraviso.R;
import com.superlogico.birraviso.app.AppConfig;
import com.superlogico.birraviso.app.AppController;
import com.superlogico.birraviso.helper.SQLiteHandler;
import com.superlogico.birraviso.helper.SessionManager;
import com.superlogico.birraviso.helper.Util.PermissionUtils;
import com.superlogico.birraviso.model.HomeBrewer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdjustmentActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final String BEER_GEO_X = "geo_x";
    private static final String BEER_GEO_Y = "geo_y";

    private static final String KEY_UID = "uid";

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String PROFILE_CONTACT = "contacto";
    private static final String PROFILE_CONTACT_EMAIL = "email";
    private static final String PROFILE_CONTACT_WHATSAPP = "whatsapp";
    private static final String PROFILE_CONTACT_FACEBOOK = "facebook";
    private static final float DEFAULT_ZOOM = 15.0f;

    private Button btnAddUpdateProfile;
    private Button btnCancel;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    private GoogleMap mMap;
    private EditText inputContact;
    private EditText inputContactFacebook;
    private EditText inputContactWhatsapp;
    private EditText inputContactEmail;
    private String latitud = "0";
    private String longitud = "0";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mLocationPermissionGranted;
    private PlaceDetectionClient mPlaceDetectionClient;
    private GeoDataClient mGeoDataClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Location mLastKnownLocation;
    private LatLng mDefaultLocation;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private MarkerOptions marker;
    private boolean firsTime = true;
    private HashMap markerHb;
    private String selected_hb;
    private CircleOptions circleOptions;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjustment_form);

        enableMyLocation();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        } else
            Toast.makeText(this, "Not Connected!", Toast.LENGTH_SHORT).show();

        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this,null);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final SeekBar sk = (SeekBar) findViewById(R.id.seekBar);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar,int progress,boolean fromUser) {
                // TODO Auto-generated method stub

                addCircleToMap(progress);
               // addHBMarkersToMap();

            }
        });
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Ubicarme en este lugar: ");

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG,"Place: " + place.getName());
                latitud = String.valueOf(place.getLatLng().latitude);
                longitud = String.valueOf(place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Ésta es tu birrería"));
                CameraUpdate center =
                        CameraUpdateFactory.newLatLngZoom(place.getLatLng(),15.0f);
                // CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                mMap.moveCamera(center);
                // mMap.animateCamera(zoom);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG,"An error occurred: " + status);
            }
        });


        btnCancel = (Button) findViewById(R.id.btnCancel);
       // btnAddUpdateProfile = (Button) findViewById(R.id.btnAddUpdateBeer);
//        btnAddUpdateProfile.setText("Actualizar mi perfil");

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(AdjustmentActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(AdjustmentActivity.this,
                        MainActivity.class);
                //intent.putExtra("homebrewer","true");
                startActivity(intent);

                finish();
            }
        });

    }

    private void updateProfile(String contact,String contactEmail,String contactFacebook,String contactWhatsapp
    ) {
        final String addContact = contact;
        final String addContactEmail = contactEmail;
        final String addContactFacebook = contactFacebook;
        final String addContactWhatsapp = contactWhatsapp;

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.UPDATE_PROFILE_URL,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Actualizando Perfil : " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        //db.addBeer(id, addName, addTrademark, addStyle, addIbu, addAlcohol, addSrm, addDescription, addOthers, "", "", "");
                        // Launch main activity
                        Intent intent = new Intent(AdjustmentActivity.this,
                                MainActivity.class);
                        intent.putExtra("homebrewer","true");
                        startActivity(intent);
                        finish();
                    }

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Json error: " + e.getMessage(),Toast.LENGTH_LONG).show();
                }

            }
        },new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG,"Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(),Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put(PROFILE_CONTACT,addContact);
                params.put(PROFILE_CONTACT_EMAIL,addContactEmail);
                params.put(PROFILE_CONTACT_FACEBOOK,addContactFacebook);
                params.put(PROFILE_CONTACT_WHATSAPP,addContactWhatsapp);
                params.put(BEER_GEO_X,latitud);
                params.put(BEER_GEO_Y,longitud);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                HashMap<String, String> userDetails = db.getUserDetails();
                String unique_id = userDetails.get(KEY_UID);
                params.put("Authorization",unique_id);
                //..add other headers
                return params;
            }
        };

        // Adding request to request queue
        String a = "0";
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(this,LOCATION_PERMISSION_REQUEST_CODE,
                    "android.permission.ACCESS_FINE_LOCATION",true);

        } else if (mMap != null) {
            // Access to the location has been granted to the app.
           // mMap.setMyLocationEnabled(true);
        }
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
        updateLocationUI();
       // addCircleToMap(0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                   // addCircleToMap(0);
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
               // mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                //  enableMyLocation();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s",e.getMessage());
        }
    }

    private void getDeviceLocation() {
    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this,new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                        } else {
                            Log.d(TAG,"Current location is null. Using defaults.");
                            Log.e(TAG,"Exception: %s",task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation,DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s",e.getMessage());
        }
    }

    public void addCircleToMap(int rad) {
        //getDeviceLocation();

        if(null != mMap) {
            mMap.clear();
            int radius = 0;
            switch (rad) {
                case 0:
                    radius = 1000;
                    break;
                case 1:
                    radius = 2500;
                    break;
                case 2:
                    radius = 10000;
                    break;
                case 3:
                    radius = 50000;
                    break;
                case 4:
                    radius = 100000;
                    break;
                default:
                    radius = 100000;
            }
            circleOptions = new CircleOptions()
                    .center(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()))
                    .radius(radius).strokeColor(Color.RED)
                    .fillColor(0x220000FF); // In meters
            mMap.addCircle(circleOptions);
            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
            double scale = radius / 500;
            int zoomLevel = (int) (16 - Math.log(scale) / Math.log(2));
            CameraUpdate center =
                    CameraUpdateFactory.newLatLngZoom(latLng,zoomLevel - 1);
            mMap.moveCamera(center);
            addHBMarkersToMap();
        }
    }

    @Override
    public void onLocationChanged(Location location)
    {
       // if(firsTime) {
            mMap.clear();
            mLastKnownLocation = location;


            LatLng currentPosition = new LatLng(location.getLatitude(),
                    location.getLongitude());
            /*marker = new MarkerOptions()
                    .position(currentPosition)
                    .snippet(
                            "Lat:" + location.getLatitude() + "Lng:"
                                    + location.getLongitude())
                    .icon(BitmapDescriptorFactory
                            .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .title("position");
            mMap.addMarker(marker);
            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
            CameraUpdate center =
                    CameraUpdateFactory.newLatLngZoom(latLng,15.0f);
            // CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);


            mMap.moveCamera(center);
       //     firsTime = false;
       // }*/
            addCircleToMap(0);

    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.
                        getLocation();
                        addCircleToMap(0);
                        //addHBMarkersToMap();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(AdjustmentActivity.this, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way
                        // to fix the settings so we won't show the dialog.
                        break;
                }
            }

        });
    }

    private void addHBMarkersToMap() {
        Marker marker;
        markerHb = new HashMap();

        // Add a marker in hombrewer address and move the camera

        String latitud = "40", longitud = "-74";
        LatLng birreria = null;
        int clickCounter = 0;

        ArrayList<HomeBrewer> hbList = db.getAllHomebrewers();
        for(HomeBrewer hb : hbList){

            birreria = new LatLng(Double.parseDouble(hb.getGeo_x()),Double.parseDouble(hb.getGeo_y()));

            if(null != hb.getGeo_x() && !hb.getGeo_x().isEmpty() && isInsideTheRadious(new MarkerOptions().position(birreria))) {

                marker = mMap.addMarker(new MarkerOptions().position(birreria).title(hb.getContact()).snippet(getString(R.string.hbBeerList)));
                marker.setTag(hb.getHb_id());
                markerHb.put(marker.getId(), hb.getHb_id());

            }
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Toast.makeText(getApplicationContext(),"Debería esta mostrandose el listado de cervezas de este birrero...", Toast.LENGTH_SHORT);
                /*if (marker.equals(userMarker)) {  //if clicked marker equals marker created by user
                    Intent intent = new Intent(MapsActivity.this, ObjectViewActivity.class);
                    startActivity(intent);

                }   */    //Otherwise just show the info window
                return false;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

            @Override
            public void onInfoWindowClick(Marker marker) {
                DataHolder.getInstance().setShowHBBeers(true);
                selected_hb = (String) markerHb.get(marker.getId());
                Intent intent = new Intent(AdjustmentActivity.this,
                        MainActivity.class);
                intent.putExtra("map","true");
                intent.putExtra("hb", selected_hb);
                startActivity(intent);
                finish();
                //showHomebrewerBeers(selected_hb);

            }
        });
    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        } else {
            /*Getting the location after aquiring location service*/
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastKnownLocation != null) {
                //_progressBar.setVisibility(View.INVISIBLE);
                //_latitude.setText("Latitude: " + String.valueOf(mLastLocation.getLatitude()));
                //_longitude.setText("Longitude: " + String.valueOf(mLastLocation.getLongitude()));
                String holis = "holis";
                addCircleToMap(0);
                String a = "";
            } else {
                /*if there is no last known location. Which means the device has no data for the loction currently.
                * So we will get the current location.
                * For this we'll implement Location Listener and override onLocationChanged*/
                Log.i("Current Location", "No data for location found");

                if (!mGoogleApiClient.isConnected())
                    mGoogleApiClient.connect();

                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, AdjustmentActivity.this);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        final LocationSettingsStates states = LocationSettingsStates.fromIntent(data);
        switch (requestCode) {
            case 1000:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        // All required changes were successfully made
                        getLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Toast.makeText(this, "Location Service not Enabled", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                break;
        }
    }

    public boolean isInsideTheRadious(MarkerOptions marker){
        float[] distance = new float[2];

        Location.distanceBetween( marker.getPosition().latitude, marker.getPosition().longitude,
                circleOptions.getCenter().latitude, circleOptions.getCenter().longitude, distance);

        if( distance[0] > circleOptions.getRadius()  ){
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {}
}