package com.superlogico.birraviso.activity;

/**
 * Created by Daniel on 7/3/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
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

public class BeerDetailsActivity extends Activity {

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

    private Button btnAddUpdateBeer;
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
    private String[] beerStyles = {"American Light Lager","American Lager","Cream Ale","American Wheat Beer","International Pale Lager",
            "International Amber Lager","International Dark Lager","Czech Pale Lager","Czech Premium Pale Lager","Czech Amber Lager",
            "Czech Dark Lager","Munich Helles","Festbier","Helles Bock","German Leichtbier","Kölsch","German Helles Exportbier","German Pils",
            "Märzen","Rauchbier","Dunkles Bock","Vienna Lager","Altbier","Pale Kellerbier","Amber Kellerbier","Munich Dunkel","Schwarzbier",
            "Doppelbock","Eisbock","Baltic Porter","Weissbier","Dunkles Weissbier","Weizenbock","Ordinary Bitter","Best Bitter","Strong Bitter",
            "British Golden Ale","Australian Sparkling Ale","English IPA","Dark Mild","British Brown Ale","English Porter","Scottish Light",
            "Scottish Heavy","Scottish Export","Irish Red Ale","Irish Stout","Irish Extra Stout","Sweet Stout","Oatmeal Stout","Tropical Stout",
            "Foreign Extra Stout","British Strong Ale","Old Ale","Wee Heavy","English Barleywine","Blonde Ale","American Pale Ale","American Amber Ale",
            "California Common ","American Brown Ale","American Porter","American Stout","Imperial Stout","American IPA","Specialty IPA - Belgian IPA",
            "Specialty IPA - Black IPA","Specialty IPA - Brown IPA","Specialty IPA - Red IPA","Specialty IPA - Rye IPA","Specialty IPA - White IPA",
            "Double IPA","American Strong Ale","American Barleywine","Wheatwine","Berliner Weisse","Flanders Red Ale","Oud Bruin","Lambic","Gueuze ",
            "Fruit Lambic","Witbier","Belgian Pale Ale","Bière de Garde","Belgian Blond Ale","Saison","Belgian Golden Strong Ale","Trappist Single",
            "Belgian Dubbel","Belgian Tripel","Belgian Dark Strong Ale","Gose","Kentucky Common","Lichtenhainer","London Brown Ale","Piwo Grodziskie",
            "Pre-Prohibition Lager","Pre-Prohibition Porter","Roggenbier","Sahti","Brett Beer","Mixed-Fermentation Sour Beer","Wild Specialty Beer",
            "Fruit Beer","Fruit and Spice Beer","Specialty Fruit Beer","Spice"," Herb"," or Vegetable Beer","Autumn Seasonal Beer","Winter Seasonal Beer",
            "Alternative Grain Beer","Alternative Sugar Beer","Classic Style Smoked Beer","Specialty Smoked Beer",
            "Wood-Aged Beer","Specialty Wood-Aged Beer","Clone Beer","Mixed-Style Beer","Experimental Beer","Dorada Pampeana","IPA Argenta"};
    private TextInputLayout inputNamebeerLayout;
    private TextInputLayout inputTrademarkLayout;
    private TextInputLayout inputStyleLayout;
    private TextInputLayout inputIbuLayout;
    private TextInputLayout inputSrmLayout;
    private TextInputLayout inputAlcoholLayout;
    private TextInputLayout inputDescrpitionLayout;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_details);

        Intent myIntent = getIntent(); // gets the previously created intent
        final String beerId = myIntent.getStringExtra(KEY_UID);
        String beerName = myIntent.getStringExtra(BEER_NAME);
        String beerTrademark = myIntent.getStringExtra(BEER_TRADEMARK);
        String beerStyle = myIntent.getStringExtra(BEER_STYLE);
        String beerIbu = myIntent.getStringExtra(BEER_IBU);
        String beerSrm = myIntent.getStringExtra(BEER_SRM);
        String beerAlcohol = myIntent.getStringExtra(BEER_ALCOHOL);
        String beerDescription = myIntent.getStringExtra(BEER_DESCRIPTION);

        // layout edittext
        inputNamebeerLayout = (TextInputLayout) findViewById(R.id.namebeer_layout);
        inputTrademarkLayout = (TextInputLayout) findViewById(R.id.trademark_layout);
        inputStyleLayout = (TextInputLayout) findViewById(R.id.style_layout);
        inputIbuLayout = (TextInputLayout) findViewById(R.id.ibu_layout);
        inputSrmLayout = (TextInputLayout) findViewById(R.id.srm_layout);
        inputAlcoholLayout = (TextInputLayout) findViewById(R.id.alcohol_layout);
        inputDescrpitionLayout = (TextInputLayout) findViewById(R.id.description_layout);
        inputNamebeerLayout.setHint("Nombre");
        inputTrademarkLayout.setHint("Marca");
        inputStyleLayout.setHint("Estilo");
        inputIbuLayout.setHint("IBU");
        inputSrmLayout.setHint("SRM");
        inputAlcoholLayout.setHint("Alcohol");
        inputDescrpitionLayout.setHint("Descripcion breve");


        inputNamebeer = (EditText) findViewById(R.id.namebeer);

        inputTrademark = (EditText) findViewById(R.id.trademark);
        inputStyle = (AutoCompleteTextView) findViewById(R.id.style);
        inputIbu = (EditText) findViewById(R.id.ibu);
        inputSrm = (EditText) findViewById(R.id.srm);
        inputAlcohol = (EditText) findViewById(R.id.alcohol);
        inputDescrpition = (EditText) findViewById(R.id.description);
        btnAddUpdateBeer = (Button) findViewById(R.id.btnAddUpdateBeer);
        btnAddUpdateBeer.setText("Actualizar Birra");
        btnCancel = (Button) findViewById(R.id.btnCancel);


        inputNamebeer.setText(beerName);
        inputTrademark.setText(beerTrademark);
        inputStyle.setText(beerStyle);
        inputIbu.setText(beerIbu);
        inputSrm.setText(beerSrm);
        inputAlcohol.setText(beerAlcohol);
        inputDescrpition.setText(beerDescription);

        //Creating the instance of ArrayAdapter containing list of beer styles
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,beerStyles);

        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.style);
        textView.setAdapter(adapter);

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
            Intent intent = new Intent(BeerDetailsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnCancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent intent = new Intent(BeerDetailsActivity.this,
                        MainActivity.class);
                intent.putExtra("homebrewer","true");
                startActivity(intent);
                finish();
            }

        });

        btnAddUpdateBeer.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String name = inputNamebeer.getText().toString().trim();
                String style = inputStyle.getText().toString().trim();
                String description = inputDescrpition.getText().toString().trim();
                String alcohol = inputAlcohol.getText().toString().trim();
                String srm = inputSrm.getText().toString().trim();
                String ibu = inputIbu.getText().toString().trim();
                String trademark = inputTrademark.getText().toString().trim();
                // Check for empty data in the form
                if (!name.isEmpty() && !style.isEmpty()) {
                    // add beer
                    updateBeerToUserList(beerId,name,trademark,style,ibu,alcohol,srm,description,"","","","");
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!",Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

    }

    private void updateBeerToUserList(String beerId,String name,String trademark,String style,String ibu,
                                      String alcohol,String srm,String description,String others,
                                      String contact,String geo_x,String geo_y) {
        final String addBeerId = beerId;
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
                AppConfig.UPDATE_BEER_URL,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Actualizando birra : " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        //db.addBeer(id, addName, addTrademark, addStyle, addIbu, addAlcohol, addSrm, addDescription, addOthers, "", "", "");
                        // Launch main activity
                        Intent intent = new Intent(BeerDetailsActivity.this,
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
                params.put(KEY_UID,addBeerId);
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