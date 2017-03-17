package com.superlogico.birraviso;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.superlogico.birraviso.activity.AddBeerActivity;
import com.superlogico.birraviso.activity.LoginActivity;
import com.superlogico.birraviso.activity.RegisterActivity;
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

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final String TAG = RegisterActivity.class.getSimpleName();
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    private List<Beer> beerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BeerAdapter bAdapter;
    private boolean homebrewerMode;

    private static final String KEY_UID = "uid";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());


        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        this.getBeerList();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void prepareBeerData(){
        beerList = db.getAllBeers();
        bAdapter.setBeerList(beerList);
        bAdapter.notifyDataSetChanged();
    }

    private void prepareMyBeersData(){
        HashMap<String, String> userDetails = db.getUserDetails();
        String unique_id = userDetails.get(KEY_UID);
        beerList = db.getAllMyBeers(unique_id);
        bAdapter.setBeerList(beerList);
        bAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            // Launching the login activity
            Intent intent = new Intent(MainActivity.this,AddBeerActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_gallery) {
            logoutUser();

        } else if (id == R.id.nav_slideshow) {
           //Intent intent = new Intent(MainActivity.this,MyBeerList.class);
           // startActivity(intent);
           // finish();
            this.getMyBeerList();

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getBeerList() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Trayendo lista de birras...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_BEERS_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carga de birras: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);

                    saveAllBeers(jObj);
                    prepareBeerData();


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


        };

        // Adding request to request queue
        String a = "0";
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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


                        JSONObject beers = jObj.getJSONObject("beers");
                        String beerString = beers.toString();
                        String corcheteAbre = "\\[";

                        beers = new JSONObject(beerString.replaceAll(corcheteAbre,"{").replaceAll("\\]","}"));
                        saveMyBeers(beers);
                        prepareMyBeersData();

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

    private void saveAllBeers(JSONObject json) {
        db.deleteBeers();
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

                db.addBeer(key, name, trademark, style, ibu, alcohol, srm, description, others, contact, geo_x, geo_y);

            } catch (JSONException e) {
                Log.e(TAG, "JSON ERROR! " + e.getMessage());
                Toast.makeText(getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }

    }

    private void saveMyBeers(JSONObject json) {
        db.deleteBeers();
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
                String geo_x = "";
                String geo_y = "";
                String uid = unique_id;

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

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
