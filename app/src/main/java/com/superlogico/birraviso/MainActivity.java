package com.superlogico.birraviso;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
import com.superlogico.birraviso.activity.BeerDetailsActivity;
import com.superlogico.birraviso.activity.LoginActivity;
import com.superlogico.birraviso.activity.RegisterActivity;
import com.superlogico.birraviso.activity.UpdateBeerActivity;
import com.superlogico.birraviso.activity.UpdateHomebrewerInfoActivity;
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
    private static final String ID_BEERS_TO_DELETE = "id_beers_to_delete";
    private static final String FAVORITE_MODE = "favorite_mode";
    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    private List<Beer> beerList = new ArrayList<>();
    private RecyclerView recyclerView;
    private BeerAdapter bAdapter;
    private boolean homebrewerMode;
    private boolean deleteMode;
    private MenuItem deleteIcon;
    private MenuItem favoritesIcon;
    private MenuItem autoRenewIcon;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private static final String KEY_UID = "uid";
    private static final String BEER_ID = "id";
    private static final String BEER_NAME = "name";
    private static final String BEER_TRADEMARK = "trademark";
    private static final String BEER_STYLE = "style";
    private static final String BEER_IBU = "ibu";
    private static final String BEER_ALCOHOL = "alcohol";
    private static final String BEER_SRM = "srm";
    private static final String BEER_DESCRIPTION = "description";
    private static final String BEER_OTHERS = "others";
    private static final String BEER_CONTACT_INFO = "contact";
    private static final String TRUE_HB = "true";
    private static final String BEER_HB_ID = "hb_id";

    private static final String CONTACT = "contacto";
    private static final String WHATSAPP = "whatsapp";
    private static final String FACEBOOK = "facebook";
    private static final String EMAIL = "email";
    private static final String LATITUD = "latitud";
    private static final String LONGITUD = "longitud";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    private View checkbox;
    private ActionBarDrawerToggle toggle;
    private FloatingActionButton addBeerFab;
    private boolean favoritesList;
    private boolean favoriteMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       /* viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);*/

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
               /* Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent intent = new Intent(MainActivity.this,AddBeerActivity.class);
                startActivity(intent);
                finish();

            }
        });

        addBeerFab = (FloatingActionButton) findViewById(R.id.fab);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
         toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if(homebrewerMode && deleteMode){
                        deleteModeOff();
                    }else {
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            drawer.closeDrawer(GravityCompat.START);
                        } else {
                            drawer.openDrawer(GravityCompat.START);
                        }
                    }
            }
        });
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Intent myIntent = getIntent(); // gets the previously created intent
        String isHomebrewer = myIntent.getStringExtra("homebrewer");
        DataHolder dataHolder = DataHolder.getInstance();
        favoriteMode = dataHolder.isFavoritesMode();
        if(TRUE_HB.equals(isHomebrewer)){
            favoriteMode = false;
            homebrewerMode = true;
            this.getMyBeerList();
        }else{
            if(!favoriteMode){
                this.getBeerList();
            }else{
                this.showMyFavoriteHomebrewerBeers();
            }
        }

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
                if(homebrewerMode && !deleteMode){
                    Intent intent = new Intent(MainActivity.this,UpdateBeerActivity.class);
                    intent.putExtra(KEY_UID, beer.getId());
                    intent.putExtra(BEER_NAME, beer.getName());
                    intent.putExtra(BEER_TRADEMARK, beer.getTrademark());
                    intent.putExtra(BEER_STYLE, beer.getStyle());
                    intent.putExtra(BEER_IBU, beer.getIbu());
                    intent.putExtra(BEER_ALCOHOL, beer.getAlcohol());
                    intent.putExtra(BEER_SRM, beer.getDrb());
                    intent.putExtra(BEER_DESCRIPTION, beer.getDescription());
                    intent.putExtra(BEER_NAME, beer.getName());

                    startActivity(intent);
                    finish();
                } else if(!homebrewerMode && !deleteMode){

                    Intent intent = new Intent(MainActivity.this,BeerDetailsActivity.class);
                    intent.putExtra(BEER_HB_ID, beer.getUser_id());
                    intent.putExtra(BEER_ID, beer.getId());
                    intent.putExtra(BEER_NAME, beer.getName());
                    intent.putExtra(BEER_TRADEMARK, beer.getTrademark());
                    intent.putExtra(BEER_STYLE, beer.getStyle());
                    intent.putExtra(BEER_IBU, beer.getIbu());
                    intent.putExtra(BEER_ALCOHOL, beer.getAlcohol());
                    intent.putExtra(BEER_SRM, beer.getDrb());
                    intent.putExtra(BEER_DESCRIPTION, beer.getDescription());

                    startActivity(intent);
                    finish();
                }
               // Toast.makeText(getApplicationContext(), beer.getName() + " is selected! id es: " + beer.getId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

                if(homebrewerMode) {
                    setDeleteMode();
                }
            }

        }));

        this.getAllProfiles();
        this.getMyProfileData();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

   /*private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new BeerList(), "ONE");
        adapter.addFragment(new FavoriteList(), "TWO");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }*/

    private void  setDeleteMode(){
        deleteMode = true;
       // checkbox = findViewById(R.id.chkSelected);
       // checkbox.setVisibility(View.VISIBLE);
        for (int i = 0; i <beerList.size() ; i++) {
            beerList.get(i).setVisible(true);
        }
        bAdapter.notifyDataSetChanged();
        setBackArrowState(true);
        invalidateOptionsMenu();
    }

    private void setBackArrowState(boolean state) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(state);
        getSupportActionBar().setDisplayShowHomeEnabled(state);
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

    private void deleteMySelectedBeers(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int choice) {
                switch (choice) {
                    case DialogInterface.BUTTON_POSITIVE:
                            //String rootDir = FileUtils.getImagesDir(getActivity());
                            //boolean fileWasDeleted = FileUtils.deleteFile(rootDir + "/" + imageFilename);
                            deleteSelectedBeers();
                               // Toast.makeText(getActivity(), "The file was deleted", Toast.LENGTH_SHORT).show();
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("¿Querés borrar estas birras: ?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();

    }

    @Override
    public void onBackPressed() {
       DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(homebrewerMode && deleteMode){
                deleteModeOff();
            } else {
                super.onBackPressed();
            }
        }

    }

    private void deleteSelectedBeers() {
        ArrayList<String> beersToDelete = new ArrayList<String>();
        for(int i = 0; i < beerList.size();i++){
            if(beerList.get(i).isSelected()){
                beersToDelete.add(beerList.get(i).getId());
            }
        }
        deleteMyBeerList(beersToDelete);
        deleteModeOff();
    }

    private void deleteModeOff(){
        deleteMode = false;
        checkbox = findViewById(R.id.chkSelected);
        checkbox.setVisibility(View.GONE);
        for (int i = 0; i <beerList.size() ; i++) {
            beerList.get(i).setVisible(false);
        }
        bAdapter.notifyDataSetChanged();
        setBackArrowState(false);
        toggle.syncState();
        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        favoritesIcon = menu.findItem(R.id.favorite_icon);
        deleteIcon = menu.findItem(R.id.delete_icon);
        autoRenewIcon = menu.findItem(R.id.autorenew_icon);
        if(deleteMode){
            deleteIcon.setVisible(true);
            favoritesIcon.setVisible(false);
        }else{
            deleteIcon.setVisible(false);
            favoritesIcon.setVisible(true);
        }
        autoRenewIcon.setVisible(true);
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

        switch (id) {
            case R.id.delete_icon:
                deleteMySelectedBeers();
                return true;
            case R.id.favorite_icon:
                this.favoritesList = true;
                this.getBeerList();
                return true;
            case R.id.autorenew_icon:
                favoriteMode = false;
                this.getBeerList();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showMyFavoriteHomebrewerBeers() {

        beerList = db.getMyFavoriteBeers();
        bAdapter = new BeerAdapter(beerList);
        bAdapter.notifyDataSetChanged();
        DataHolder.getInstance().setFavoriteMode(true);
        this.favoritesList = false;
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
            this.editMyProfile();
        } else if (id == R.id.nav_slideshow) {
           //Intent intent = new Intent(MainActivity.this,MyBeerList.class);
           // startActivity(intent);
           // finish();
            DataHolder.getInstance().setFavoriteMode(false);
            this.getMyBeerList();
            addBeerFab.setVisibility(View.VISIBLE);

        } else if (id == R.id.nav_manage) {
            DataHolder.getInstance().setFavoriteMode(false);
            this.homebrewerMode = false;
            this.getBeerList();

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_logout) {
            logoutUser();
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
                    if(!favoritesList){
                        prepareBeerData();
                    }else{
                        showMyFavoriteHomebrewerBeers();
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
                        homebrewerMode = true;
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
                String id = beer.getString("id");
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
                String hb_id = beer.getString("hb_id");

                db.addBeer(id, name, trademark, style, ibu, alcohol, srm, description, others, contact, geo_x, geo_y, hb_id);

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

              //  if (db.existsBeer(key)){
              //      db.deleteBeerById(key);
              //  }

                db.addMyBeer(key, name, trademark, style, ibu, alcohol, srm, description, others, contact, geo_x, geo_y, uid);

            } catch (JSONException e) {
                Log.e(TAG, "JSON ERROR! " + e.getMessage());
                Toast.makeText(getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }
    }

    private void deleteMyBeerList(ArrayList<String> beersToDelete) {
        final ArrayList<String> idBeersToDelete = beersToDelete;

        // Tag used to cancel the request
        String tag_string_req = "req_login";

        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.DELETE_BEER_LIST_URL,new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Borrando lista de birras : " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if (!error) {
                        //db.addBeer(id, addName, addTrademark, addStyle, addIbu, addAlcohol, addSrm, addDescription, addOthers, "", "", "");
                        // Launch main activity
                        showDialog();
                        getMyBeerList();
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
                Log.e(TAG,"Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(),Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String ids = TextUtils.join(",", idBeersToDelete);
                params.put(ID_BEERS_TO_DELETE, ids);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                HashMap<String, String> userDetails = db.getUserDetails();
                String unique_id = userDetails.get(KEY_UID);
                params.put("Authorization",unique_id);
                return params;
            }
        };

        // Adding request to request queue
        String a = "0";
        AppController.getInstance().addToRequestQueue(strReq,tag_string_req);
    }

    private void getMyProfileData() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        showDialog();
        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_PROFILE_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Trayendo mis datos de perfil : " + response.toString());
                hideDialog();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    // Check for error node in json
                    if (!error) {

                        JSONObject profile = jObj.getJSONObject("profile");
                        String id = (String) profile.get("id");
                        String contacto = (String) profile.get("contacto");
                        String whatsapp = (String) profile.get("whatsapp");
                        String email = (String) profile.get("email");
                        String facebook = (String) profile.get("facebook");
                        String latitud = (String) profile.get("geo_x");
                        String longitud = (String) profile.get("geo_y");
                        saveMyProfile(id, contacto, whatsapp, email, facebook, latitud, longitud);
                      //  editMyProfile(contacto, whatsapp, email, facebook, latitud, longitud);
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

    private void saveMyProfile(String id, String contacto,String whatsapp,String email,String facebook,String latitud,String longitud) {
        db.addProfile("1000000", contacto, latitud, longitud, whatsapp, facebook, email, email);
        db.addMyProfile(contacto, latitud, longitud, whatsapp, facebook, email, email);
    }

    private void editMyProfile() {

        HashMap<String, String> myProfileDetails = db.getMyProfileDetails();
        Intent intent = new Intent(MainActivity.this,UpdateHomebrewerInfoActivity.class);
        intent.putExtra(CONTACT, myProfileDetails.get("contact"));
        intent.putExtra(WHATSAPP, myProfileDetails.get("whatsapp"));
        intent.putExtra(EMAIL, myProfileDetails.get("email"));
        intent.putExtra(FACEBOOK, myProfileDetails.get("facebook"));
        intent.putExtra(LATITUD, myProfileDetails.get("geo_x"));
        intent.putExtra(LONGITUD, myProfileDetails.get("geo_y"));
        startActivity(intent);
        finish();
    }

    private void getAllProfiles() {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Trayendo lista de homebrewers...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.GET,
                AppConfig.GET_ALL_PROFILES_URL, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carga de homebrewer: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    String beerString = jObj.toString();
                    String corcheteAbre = "\\[";

                    jObj = new JSONObject(beerString.replaceAll(corcheteAbre,"{").replaceAll("\\]","}"));

                    saveAllProfiles(jObj);
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



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void saveAllProfiles(JSONObject json) {

        db.deleteProfiles();
        Iterator<String> iter = json.keys();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                JSONObject beer = json.getJSONObject(key);
                String id = beer.getString("id");
                String contacto = beer.getString("contacto");
                String email = beer.getString("email");
                String facebook = beer.getString("facebook");
                String whatsapp = beer.getString("whatsapp");
                String geo_x = beer.getString("geo_x");
                String geo_y = beer.getString("geo_y");

                db.addProfile(id, contacto, geo_x, geo_y, whatsapp, facebook, email, email);

            } catch (JSONException e) {
                Log.e(TAG, "JSON ERROR! " + e.getMessage());
                Toast.makeText(getApplicationContext(),
                        e.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }
    }

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