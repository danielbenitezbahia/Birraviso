package com.superlogico.birraviso.activity;

/**
 * Created by Daniel on 7/3/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.superlogico.birraviso.MainActivity;
import com.superlogico.birraviso.R;
import com.superlogico.birraviso.adapter.BeerAdapter;
import com.superlogico.birraviso.adapter.DividerItemDecoration;
import com.superlogico.birraviso.adapter.RecyclerTouchListener;
import com.superlogico.birraviso.app.AppConfig;
import com.superlogico.birraviso.app.AppController;
import com.superlogico.birraviso.helper.SQLiteHandler;
import com.superlogico.birraviso.helper.SessionManager;
import com.superlogico.birraviso.model.Beer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyBeerList extends Activity{

    private static final String BEER_NAME = "name";
    private static final String BEER_TRADEMARK = "trademark";
    private static final String BEER_STYLE = "style";
    private static final String BEER_IBU = "ibu";
    private static final String BEER_ALCOHOL = "alcohol";
    private static final String BEER_SMR = "srm";
    private static final String BEER_DESCRIPTION = "description";
    private static final String BEER_OTHERS = "others";
    private static final String BEER_CONTACT_INFO = "contact";
    private static final String BEER_GEO_X = "geo_x";
    private static final String BEER_GEO_Y = "geo_y";

    private static final String KEY_UID = "uid";

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
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

    private RecyclerView recyclerView;
    private BeerAdapter bAdapter;
    private List<Beer> beerList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_beers_list);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (!session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(MyBeerList.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        getMyBeerList();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view_my_beers_list);

        bAdapter = new BeerAdapter(beerList);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(bAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Beer beer = beerList.get(position);
                Toast.makeText(getApplicationContext(), beer.getName() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void getMyBeerList() {

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_BEER_LIST_BY_UID_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Trayendo mis birras : " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {
                        saveAllBeers(jObj);
                        prepareBeerData();

                    }


                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {



            @Override
            public Map<String, String> getHeaders()  {
                Map<String,String> params =  new HashMap<>();
                HashMap<String, String> userDetails = db.getUserDetails();
                String unique_id = userDetails.get(KEY_UID);
                params.put("Authorization", unique_id);
                //..add other headers
                return params;
            }

        };

        // Adding request to request queue
        String a = "0";
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void prepareBeerData()    {
        beerList = db.getAllBeers();
        bAdapter.setBeerList(beerList);
        bAdapter.notifyDataSetChanged();
    }

    private void saveAllBeers(JSONObject json) {
        HashMap<String, String> userDetails = db.getUserDetails();
        String unique_id = userDetails.get(KEY_UID);
        Iterator<String> iter = json.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject beer = json.getJSONObject(key);
                String name = beer.getString("marca");
                String trademark = beer.getString("marca");
                String style = beer.getString("estilo");
                String ibu = beer.getString("ibu");
                String alcohol = beer.getString("alcohol");
                String srm = beer.getString("srm");
                String description = beer.getString("descripcion");
                String others = beer.getString("otros");
                String contact = beer.getString("contacto");
                String geo_x = beer.getString("geo_x");
                String geo_y = beer.getString("geo_y");
                String uid = beer.getString(unique_id);

                if (db.existsBeer(key)){
                    db.deleteBeerById(key);
                }

                db.addMyBeer(key, name, trademark, style, ibu, alcohol, srm, description, others, contact, geo_x, geo_y, uid);

            } catch (JSONException e) {
                Log.e(TAG, "JSON ERROR! " + e.getMessage());
                Toast.makeText(getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }

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
