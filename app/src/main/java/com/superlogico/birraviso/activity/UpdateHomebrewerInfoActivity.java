package com.superlogico.birraviso.activity;

/**
 * Created by Daniel on 7/3/2017.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.superlogico.birraviso.MainActivity;
import com.superlogico.birraviso.R;
import com.superlogico.birraviso.app.AppConfig;
import com.superlogico.birraviso.app.AppController;
import com.superlogico.birraviso.helper.SQLiteHandler;
import com.superlogico.birraviso.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UpdateHomebrewerInfoActivity extends FragmentActivity
        implements OnMapReadyCallback {


    private static final String BEER_NAME = "name";
    private static final String BEER_TRADEMARK = "trademark";
    private static final String BEER_STYLE = "style";
    private static final String BEER_IBU = "ibu";
    private static final String BEER_ALCOHOL = "alcohol";
    private static final String BEER_SRM = "srm";
    private static final String BEER_DESCRIPTION = "description";
    private static final String BEER_OTHERS = "others";
    private static final String BEER_CONTACT_INFO = "contact";
    private static final String BEER_GEO_X = "geo_x";
    private static final String BEER_GEO_Y = "geo_y";

    private static final String KEY_UID = "uid";

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String Y_COORDINATE = "y_coordinate";
    private static final String X_COORDINATE = "x_coordinate";
    private static final String PROFILE_CONTACT = "contacto";
    private static final String PROFILE_CONTACT_EMAIL = "email";
    private static final String PROFILE_CONTACT_WHATSAPP = "whatsapp";
    private static final String PROFILE_CONTACT_FACEBOOK = "facebook";
    private static final String LATITUD = "latitud";
    private static final String LONGITUD = "longitud";


    private Button btnAddUpdateProfile;
    private Button btnCancel;
    private EditText inputNamebeer;
    private EditText inputTrademark;
    private EditText inputStyle;
    private EditText inputIbu;
    private EditText inputSrm;
    private EditText inputAlcohol;
    private EditText inputDescrpition;

    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private static AppController appController;

    private TextInputLayout inputDescrpitionLayout;
    private TextInputLayout inputEmailLayout;
    private TextInputLayout inputFacebookLayout;
    private TextInputLayout inputWhatsappLayout;

    private GoogleMap mMap;
    private EditText inputContact;
    private EditText inputContactFacebook;
    private EditText inputContactWhatsapp;
    private EditText inputContactEmail;
    private String latitud = "0";
    private String longitud = "0";
    private String contact;
    private String whatsapp;
    private String email;
    private String facebook;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_form);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Intent myIntent = getIntent(); // gets the previously created intent
        contact = myIntent.getStringExtra(PROFILE_CONTACT);
        email = myIntent.getStringExtra(PROFILE_CONTACT_EMAIL);
        whatsapp = myIntent.getStringExtra(PROFILE_CONTACT_WHATSAPP);
        facebook = myIntent.getStringExtra(PROFILE_CONTACT_FACEBOOK);
        if( myIntent.getStringExtra(LATITUD) != null && !myIntent.getStringExtra(LATITUD).isEmpty() ){
            latitud = myIntent.getStringExtra(LATITUD);
        }else {
            latitud = "0";
        }

        if( myIntent.getStringExtra(LONGITUD) != null && !myIntent.getStringExtra(LONGITUD).isEmpty() ){
            longitud = myIntent.getStringExtra(LONGITUD);
        }else {
            longitud = "0";
        }


        inputDescrpitionLayout = (TextInputLayout) findViewById(R.id.contact_layout);
        inputDescrpitionLayout.setHint("Tu nombre o el nombre de tu marca de birras");

        inputWhatsappLayout = (TextInputLayout) findViewById(R.id.contact_whatsapp_layout);
        inputWhatsappLayout.setHint("Tu nro. de celular / Whatsapp");

        inputEmailLayout = (TextInputLayout) findViewById(R.id.contact_email_layout);
        inputEmailLayout.setHint("Tu email");

        inputFacebookLayout = (TextInputLayout) findViewById(R.id.contact_facebook_layout);
        inputFacebookLayout.setHint("Tu Facebook de homebrewer");

        inputDescrpition = (EditText) findViewById(R.id.description);
        inputContact = (EditText) findViewById(R.id.contact);
        inputContactFacebook = (EditText) findViewById(R.id.contact_facebook);
        inputContactEmail = (EditText) findViewById(R.id.contact_email);
        inputContactWhatsapp = (EditText) findViewById(R.id.contact_whatsapp);

        inputContact.setText(contact);
        inputContactFacebook.setText(facebook);
        inputContactEmail.setText(email);
        inputContactWhatsapp.setText(whatsapp);

        btnAddUpdateProfile = (Button) findViewById(R.id.btnAddUpdateBeer);
        btnAddUpdateProfile.setText("Actualizar mi perfil");
        btnCancel = (Button) findViewById(R.id.btnCancel);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setHint("Direccion de mi birreria");
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                latitud = String.valueOf(place.getLatLng().latitude);
                longitud = String.valueOf(place.getLatLng().longitude);
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("Ésta es tu birrería"));
                CameraUpdate center=
                        CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15.0f);
               // CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

                mMap.moveCamera(center);
               // mMap.animateCamera(zoom);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(UpdateHomebrewerInfoActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(UpdateHomebrewerInfoActivity.this,
                        MainActivity.class);
                intent.putExtra("homebrewer","true");
                startActivity(intent);
                finish();

            }

        });


        btnAddUpdateProfile.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String contact = inputContact.getText().toString().trim();
                String contactWhatsapp = inputContactWhatsapp.getText().toString().trim();
                String contactEmail = inputContactEmail.getText().toString().trim();
                String contactFacebook = inputContactFacebook.getText().toString().trim();

                // Check for empty data in the form
                if (!contact.isEmpty() && !contact.isEmpty()) {
                    // add beer
                    updateProfile(contact, contactEmail, contactFacebook, contactWhatsapp);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!",Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

    }

    private void updateProfile(String contact, String contactEmail, String contactFacebook,String contactWhatsapp
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
                        Intent intent = new Intent(UpdateHomebrewerInfoActivity.this,
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

                params.put(PROFILE_CONTACT, addContact);
                params.put(PROFILE_CONTACT_EMAIL,addContactEmail);
                params.put(PROFILE_CONTACT_FACEBOOK,addContactFacebook);
                params.put(PROFILE_CONTACT_WHATSAPP, addContactWhatsapp);
                params.put(BEER_GEO_X, latitud);
                params.put(BEER_GEO_Y, longitud);

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

        // Add a marker in hombrewer address and move the camera
        LatLng birreria = new LatLng(Double.parseDouble(latitud), Double.parseDouble(longitud));
        mMap.addMarker(new MarkerOptions().position(birreria).title("Ésta es tu birrería"));
        CameraUpdate center=
                CameraUpdateFactory.newLatLngZoom(birreria, 15.0f);
        // CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        mMap.moveCamera(center);

    }
}