package com.superlogico.birraviso.activity;

/**
 * Created by Daniel on 7/3/2017.
 */

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.superlogico.birraviso.DataHolder;
import com.superlogico.birraviso.MainActivity;
import com.superlogico.birraviso.Manifest;
import com.superlogico.birraviso.R;
import com.superlogico.birraviso.app.AppConfig;
import com.superlogico.birraviso.app.AppController;
import com.superlogico.birraviso.helper.SQLiteHandler;
import com.superlogico.birraviso.helper.SessionManager;
import com.superlogico.birraviso.helper.Util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeerDetailsActivity extends AppCompatActivity {

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
    private static final String BEER_HB_ID = "hb_id";
    private static final String BEER_ID = "id";

    private static final String KEY_UID = "uid";
    private static final int PERMISSION_REQUEST_CONTACT = 1;

    private static final String TAG = RegisterActivity.class.getSimpleName();
    private static final String WHATSAPP = "whatsapp";
    private static final String FACEBOOK = "facebook";
    private static final String CONTACT_HB = "contact";
    private static final String PUBLIC_EMAIL = "publicEmail";
    private static final String FAVORITE_MODE = "favorite_mode";

    private Button btnAddUpdateBeer;
    private Button btnCancel;
    private TextView tvBeerName;
    private TextView tvBeerTrademark;
    private TextView tvBeerStyle;
    private TextView tvBeerIbu;
    private TextView tvBeerSrm;
    private TextView tvBeerAlcohol;
    private TextView tvBeerDescrpition;
    private TextView tvWhatsapp;
    private TextView tvFacebook;
    private TextView tvEmail;
    private String firstName, secondName, phoneNumber, email, facebook;

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

    private  String[] srmColors = {"#FFE699","#FFD878","#FFCA5A","#FFBF42","#FBB123","#F8A600","#F39C00","#EA8F00","#E58500","#DE7C00","#D77200","#CF6900","#CB6200","#C35900","#BB5100","#B54C00","#B04500",
            "#A63E00","#A13700","#9B3200","#952D00","#8E2900","#882300","#821E00","#7B1A00","#771900","#701400","#6A0E00","#660D00","#5E0B00","#5A0A02","#600903","#520907","#4C0505",
            "#470606","#440607","#3F0708","#3B0607","#3A070B","#36080A"};
    private HashMap<String, String> profileDetails;
    private String beer_hb_id;
    private android.support.v7.app.ActionBar actionBar;
    private Drawable mDrawable;
    private Bitmap mFinalBitmap;
    private ImageView imgIcon;
    private String beer_id;
    private Boolean favoriteMode;
    private Utils util;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beer_details);

        util = Utils.getInstance();
        util.setAppContext(getApplicationContext());

     /*   mDrawable = ContextCompat.getDrawable(this, R.drawable.birra);
        mDrawable.setColorFilter(new PorterDuffColorFilter(0xfff000,PorterDuff.Mode.DARKEN));*/


        Intent myIntent = getIntent(); // gets the previously created intent
        final String beerId = myIntent.getStringExtra(KEY_UID);
        String beerName = myIntent.getStringExtra(BEER_NAME);
        //String beerTrademark = myIntent.getStringExtra(BEER_TRADEMARK);
        String beerStyle = myIntent.getStringExtra(BEER_STYLE);
        String beerIbu = myIntent.getStringExtra(BEER_IBU);
        String beerSrm = myIntent.getStringExtra(BEER_SRM);
        String beerAlcohol = myIntent.getStringExtra(BEER_ALCOHOL);
        beer_hb_id = myIntent.getStringExtra(BEER_HB_ID);
        beer_id = myIntent.getStringExtra(BEER_ID);
        DataHolder dataHolder = DataHolder.getInstance();
        favoriteMode = dataHolder.isFavoritesMode();
        dataHolder.setSyncAllBeerNeeded(true);

        Resources res = this.getResources();
        final ImageView image = (ImageView) findViewById(R.id.imageview_icon);
        //int colorResource = this.getResId("srmColor" + beerSrm, Color.class);
        int resourceId = this.getResources().getIdentifier("srmColor" + beerSrm, "color", this.getPackageName());
        final int newColor = res.getColor(resourceId);
        image.setColorFilter(newColor, PorterDuff.Mode.SRC_ATOP);



        tvBeerName = (TextView) findViewById(R.id.textview_title);
        // tvBeerTrademark = (TextView) findViewById(R.id.trademark);
        tvBeerStyle = (TextView) findViewById(R.id.textview_content);
        tvBeerIbu = (TextView) findViewById(R.id.ibuBeer);
        tvBeerSrm = (TextView) findViewById(R.id.srmBeer);
        tvBeerAlcohol = (TextView) findViewById(R.id.alcoholBeer);
        // tvBeerDescrpition = (TextView) findViewById(R.id.description);
       // tvWhatsapp = (TextView) findViewById(R.id.homebrewerWhatsapp);
        //tvFacebook = (TextView) findViewById(R.id.homebrewerFacebook);
       // tvEmail = (TextView) findViewById(R.id.homebrewerEmail);

        db = new SQLiteHandler(getApplicationContext());

        profileDetails = db.getProfileDetails(beer_hb_id);

        tvBeerName.setText(beerName);
        // tvBeerTrademark.setText(beerTrademark);
        tvBeerStyle.setText(beerStyle);
        tvBeerIbu.setText("IBU: " + beerIbu);
        tvBeerSrm.setText("SRM: " + beerSrm);
        tvBeerAlcohol.setText("ABV: " + beerAlcohol);
        phoneNumber = profileDetails.get(WHATSAPP);
        facebook = profileDetails.get(FACEBOOK);
        email = profileDetails.get(PUBLIC_EMAIL);
        firstName = profileDetails.get(CONTACT_HB);
        // tvBeerDescrpition.setText(beerDescription);


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

        FloatingActionButton myFab = (FloatingActionButton) findViewById(R.id.fabAddHombrewerFavorites);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addThisHBtoFavorites();
            }
        });
        ImageView whatsappButton = (ImageView) findViewById(R.id.TextView04);
        ImageView facebookButton = (ImageView) findViewById(R.id.TextView05);
        ImageView emailButton = (ImageView) findViewById(R.id.TextView06);
        whatsappButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                addContact();
            }

        });
        facebookButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = util.newFacebookIntent(getApplicationContext().getPackageManager(),facebook);
                startActivity(i);
            }
        });
        emailButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                sendEmail();

            }
        });


        // Set Image color regarding SRM


        //Get the image to be changed from the drawable, drawable-xhdpi, drawable-hdpi,etc folder.
     /*   Drawable sourceDrawable = getResources().getDrawable(R.drawable.birrafondo);

//Convert drawable in to bitmap
        Bitmap sourceBitmap = Util.convertDrawableToBitmap(sourceDrawable);

//Pass the bitmap and color code to change the icon color dynamically.

        mFinalBitmap = Util.changeImageColor(sourceBitmap, 200);

        imgIcon = (ImageView) findViewById(R.id.imageview_icon);

        imgIcon.setImageBitmap(mFinalBitmap);*/




    }

    private void sendEmail() {
        String mailto = "mailto:" + email +
                "&subject=" + Uri.encode("subject") +
                "&body=" + Uri.encode("bodyText");

        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse(mailto));

        try {
            startActivity(emailIntent);
        } catch (ActivityNotFoundException e) {
            //TODO: Handle case where no email app is available
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BeerDetailsActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void addThisHBtoFavorites() {
        if (db.addHBtoFavorites(beer_hb_id, beer_id)) {
            Toast.makeText(getApplicationContext(),
                    "El cervecero " + profileDetails.get(CONTACT_HB) + " fue agregado exitosamente a tu lista de favoritos!", Toast.LENGTH_LONG)
                    .show();
        } else {
            Utils.getInstance().toastError("El cervecero " + profileDetails.get(CONTACT_HB) + " ya existe en tu lista de favoritos y podes ver todas sus birras publicadas yendo a la seccion Favoritos!");

        }
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

    public void askForContactPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(),Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("please confirm Contacts access");//TODO put real question
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {Manifest.permission.READ_CONTACTS}
                                    , PERMISSION_REQUEST_CONTACT);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.

                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{"android.permission.WRITE_CONTACTS"},
                            PERMISSION_REQUEST_CONTACT);

                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }else{
                addContact();
            }
        }
        else{
            addContact();
        }
    }

    private void addContact() {
        util.setFirstName(firstName);
        util.setEmail(email);
        util.setSecondName(secondName);
        util.setPhone(phoneNumber);
        if(util.addContact()){
            Toast.makeText(getApplicationContext(),"Se agrego con éxito el contacto: " + firstName + " a tu Whatsapp", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(getApplicationContext(),"Ups, algo salió mal... no se pudo agregar este cervecero a tu Whatsapp", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSION_REQUEST_CONTACT: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    this.addContact();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Toast.makeText(getApplicationContext(),"No permission for contacts", Toast.LENGTH_SHORT);
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }


    public static int getResId(String resName, Class<?> c) {

        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
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