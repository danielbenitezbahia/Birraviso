package com.superlogico.birraviso.activity;

/**
 * Created by Daniel on 7/3/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
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

public class AddBeerActivity extends Activity {

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
    private Button btnAddBeer;
    private Button btnCancel;
    private Button btnLinkToRegister;
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_beer_form);

        inputNamebeer = (EditText) findViewById(R.id.namebeer);

        inputTrademark = (EditText) findViewById(R.id.trademark);
        inputStyle = (AutoCompleteTextView) findViewById(R.id.style);
        inputIbu = (EditText) findViewById(R.id.ibu);
        inputSrm = (EditText) findViewById(R.id.srm);
        inputAlcohol = (EditText) findViewById(R.id.alcohol);
        inputDescrpition = (EditText) findViewById(R.id.description);
        btnAddBeer = (Button) findViewById(R.id.btnAddUpdateBeer);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(AddBeerActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnAddBeer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = inputNamebeer.getText().toString().trim();
                String style = inputStyle.getText().toString().trim();
                String description = inputDescrpition.getText().toString().trim();
                String alcohol = inputAlcohol.getText().toString().trim();
                String srm = inputSrm.getText().toString().trim();
                String ibu = inputIbu.getText().toString().trim();
                String trademark = inputTrademark.getText().toString().trim();
                HashMap<String, String> myProfileDetails = db.getProfileDetails("1000000");


                // Check for empty data in the form
                if (!name.isEmpty() && !style.isEmpty()) {
                    // add beer
                    addBeerToUserList(name,trademark,style,ibu,alcohol,srm,description,myProfileDetails.get("hb_id"),"","","");
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!",Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(AddBeerActivity.this,
                        MainActivity.class);
                intent.putExtra("homebrewer","true");
                startActivity(intent);
                finish();

            }

        });

    }

    private void addBeerToUserList(String name,String trademark,String style,String ibu,
                                   String alcohol,String srm,String description,String others,
                                   String contact,String geo_x,String geo_y) {
        final String addName = name;
        final String addTrademark = trademark;
        final String addStyle = style;
        final String addIbu = ibu;
        final String addAlcohol = alcohol;
        final String addSrm = srm;
        final String addDescription = description;
        final String addOthers = others;
        final String addContact = contact;
        final String addGeo_x = geo_x;
        final String addGeo_y = geo_y;
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.ADD_BEER_URL,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Guardando nueva birra : " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    String id = jObj.getString("id");

                    if (!error) {
                        db.addBeer(id,addName,addTrademark,addStyle,addIbu,addAlcohol,addSrm,addDescription,addOthers,"","","","");
                        // Launch main activity
                        Intent intent = new Intent(AddBeerActivity.this,
                                MainActivity.class);
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
                params.put(BEER_NAME,addName);
                params.put(BEER_TRADEMARK,addTrademark);
                params.put(BEER_STYLE,addStyle);
                params.put(BEER_IBU,addIbu);
                params.put(BEER_ALCOHOL,addAlcohol);
                params.put(BEER_SRM,addSrm);
                params.put(BEER_DESCRIPTION,addDescription);
                params.put(BEER_OTHERS,addOthers);
                params.put(BEER_CONTACT_INFO,addContact);
                params.put(BEER_GEO_X,addGeo_x);
                params.put(BEER_GEO_Y,addGeo_y);
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
}
