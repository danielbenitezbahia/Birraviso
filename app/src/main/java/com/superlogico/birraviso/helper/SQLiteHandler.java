package com.superlogico.birraviso.helper;

/**
 * Created by daniel.benitez on 2/1/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.superlogico.birraviso.model.Beer;

import java.util.ArrayList;
import java.util.HashMap;

public class SQLiteHandler extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandler.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Beers table name
    private static final String TABLE_BEER = "beer";
    // Beers Table Columns names
    private static final String BEER_ID = "id";
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
    private static final String BEER_HB_ID = "hb_id";

    // Favorites table name
    private static final String TABLE_FAVORITE = "favorite";
    // Favorites Table Columns names
    private static final String FAVORITE_ID = "id";
    private static final String FAVORITE_ID_HB = "hb_id";

    // Profile table name
    private static final String TABLE_PROFILE = "profile";
    // Profile Table Columns names
    private static final String PROFILE_ID = "id";
    private static final String PROFILE_ID_HB = "hb_id";
    private static final String PROFILE_CONTACT_INFO = "contact";
    private static final String PROFILE_GEO_X = "geo_x";
    private static final String PROFILE_GEO_Y = "geo_y";
    private static final String PROFILE_WHATSAPP = "whatsapp";
    private static final String PROFILE_FACEBOOK = "facebook";
    private static final String PROFILE_EMAIL = "email";
    private static final String PROFILE_PUBLIC_EMAIL = "publicEmail";

    private static final String TABLE_USER = "user";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    SQLiteDatabase db;

    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE," + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT" + ")";
        db.execSQL(CREATE_LOGIN_TABLE);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEER);
        String CREATE_BEER_TABLE = "CREATE TABLE " + TABLE_BEER + "("
                + BEER_ID + " INTEGER," + BEER_NAME + " TEXT,"
                + BEER_TRADEMARK + " TEXT," + BEER_STYLE + " TEXT, "
                + BEER_IBU + " TEXT, " + BEER_ALCOHOL + " TEXT, "+ BEER_SMR + " TEXT, "
                + BEER_DESCRIPTION + " TEXT,"+ BEER_OTHERS + " TEXT," + BEER_CONTACT_INFO
                + " TEXT, "+ BEER_GEO_X + " TEXT, " + BEER_GEO_Y + " TEXT," + BEER_HB_ID + " TEXT" + " )";
        db.execSQL(CREATE_BEER_TABLE);

        String CREATE_FAVORITE_TABLE = "CREATE TABLE " + TABLE_FAVORITE + "("
                + FAVORITE_ID + " INTEGER PRIMARY KEY," + FAVORITE_ID_HB + " TEXT"
                + ")";
        db.execSQL(CREATE_FAVORITE_TABLE);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        String CREATE_PROFILE_TABLE = "CREATE TABLE " + TABLE_PROFILE + "("
                + PROFILE_ID + " INTEGER," + PROFILE_ID_HB + " TEXT,"
                + PROFILE_CONTACT_INFO + " TEXT," + PROFILE_GEO_X + " TEXT, "
                + PROFILE_GEO_Y + " TEXT, " + PROFILE_WHATSAPP + " TEXT, "+ PROFILE_FACEBOOK + " TEXT, " + PROFILE_PUBLIC_EMAIL + " TEXT, "
                + PROFILE_EMAIL + " TEXT" + " )";
        db.execSQL(CREATE_PROFILE_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);

        // Create tables again
        onCreate(db);
    }

    public void createTables() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEER);
        String CREATE_BEER_TABLE = "CREATE TABLE " + TABLE_BEER + "("
                + BEER_ID + " INTEGER PRIMARY KEY," + BEER_NAME + " TEXT,"
                + BEER_TRADEMARK + " TEXT," + BEER_STYLE + " TEXT, "
                + BEER_IBU + " TEXT, " + BEER_ALCOHOL + " TEXT, "+ BEER_SMR + " TEXT, "
                + BEER_DESCRIPTION + " TEXT,"+ BEER_OTHERS + " TEXT," + BEER_CONTACT_INFO
                + " TEXT, "+ BEER_GEO_X + " TEXT, " + BEER_GEO_Y + " TEXT" + " )";
        db.execSQL(CREATE_BEER_TABLE);

        Log.d(TAG, "Database tables created");
    }

    /**
     * Storing user details in database
     * */
    public void addUser(String name, String email, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_UID, uid); // Email
        values.put(KEY_CREATED_AT, created_at); // Created At

        // Inserting Row
        long id = db.insert(TABLE_USER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New user inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() != 0) {
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                user.put("name", cursor.getString(1));
                user.put("email", cursor.getString(2));
                user.put("uid", cursor.getString(3));
                user.put("created_at", cursor.getString(4));
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Re craete database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_USER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    /**
     * Storing beer details in database
     * */
    public void addBeer(String idbackend, String name, String trademark, String style, String ibu,
                        String alcohol, String srm, String description, String others,
                        String contact, String geo_x, String geo_y, String hb_id) {

        int idb = Integer.valueOf(idbackend);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BEER_ID, idb);
        values.put(BEER_NAME, name);
        values.put(BEER_TRADEMARK, trademark);
        values.put(BEER_STYLE, style);
        values.put(BEER_IBU, ibu);
        values.put(BEER_ALCOHOL, alcohol);
        values.put(BEER_SMR, srm);
        values.put(BEER_DESCRIPTION, description);
        values.put(BEER_OTHERS, others);
        values.put(BEER_CONTACT_INFO, contact);
        values.put(BEER_GEO_X, geo_x);
        values.put(BEER_GEO_Y, geo_y);
        values.put(BEER_HB_ID, hb_id);

        // Inserting Row
        long id = db.insert(TABLE_BEER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New beer inserted into sqlite: " + id);
    }

    /**
     * Storing beer details in database
     * */
    public void addMyBeer(String idbackend, String name, String trademark, String style, String ibu,
                          String alcohol, String srm, String description, String others,
                          String contact, String geo_x, String geo_y, String uid) {

        int idb = Integer.valueOf(idbackend);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BEER_ID, idb);
        values.put(BEER_NAME, name);
        values.put(BEER_TRADEMARK, trademark);
        values.put(BEER_STYLE, style);
        values.put(BEER_IBU, ibu);
        values.put(BEER_ALCOHOL, alcohol);
        values.put(BEER_SMR, srm);
        values.put(BEER_DESCRIPTION, description);
        values.put(BEER_OTHERS, uid);
        values.put(BEER_CONTACT_INFO, contact);
        values.put(BEER_GEO_X, geo_x);
        values.put(BEER_GEO_Y, geo_y);

        // Inserting Row
        long id = db.insert(TABLE_BEER, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "New my beer inserted into sqlite: " + id);
    }

    /**
     * Getting beers data from database
     * */
    public ArrayList<Beer> getAllBeers() {

        ArrayList<Beer> beerList = new ArrayList<Beer>();
        String selectQuery = "SELECT * FROM " + TABLE_BEER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() != 0) {
            // Move to first row
            cursor.moveToFirst();
            // Cursor parameter Order
            // id, name, trademark, style, ibu, alcohol, srm, description, others, contact , x, y
            //
            // Beer parameter order
            // String beer_id, String user_id, String name, String style, String trademark, String ibu,
            // String drb, String alcohol, String description, String contact, String others
            Beer beer;
            HashMap<String, String> userDetails = getUserDetails();
            //String unique_id = userDetails.get(KEY_UID);
            beer = new Beer(cursor.getString(0), cursor.getString(12), cursor.getString(1), cursor.getString(3), cursor.getString(2),
                    cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(9),
                    cursor.getString(8));
            beerList.add(beer);
            while (cursor.moveToNext()) {

                beer = new Beer(cursor.getString(0), cursor.getString(12), cursor.getString(1), cursor.getString(3), cursor.getString(2),
                        cursor.getString(4), cursor.getString(5), cursor.getString(6), cursor.getString(7), cursor.getString(9),
                        cursor.getString(8));
                beerList.add(beer);
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching beer from Sqlite: " + beerList.toString());

        return beerList;
    }

    /**
     * Getting beers data from database
     * */
    public ArrayList<Beer> getAllMyBeers(String uid) {

        ArrayList<Beer> beerList = new ArrayList<Beer>();
        String selectQuery = "SELECT * FROM " + TABLE_BEER + " WHERE others = '" + uid + "'";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() != 0) {
            // Move to first row
            cursor.moveToFirst();
            Beer beer;
            beer = new Beer(cursor.getString(0),uid,cursor.getString(1),cursor.getString(3),cursor.getString(2),cursor.getString(4),
                    cursor.getString(6),cursor.getString(5),cursor.getString(7),cursor.getString(9),cursor.getString(8));
            beerList.add(beer);
            while (cursor.moveToNext()) {

                beer = new Beer(cursor.getString(0),uid,cursor.getString(1),cursor.getString(3),cursor.getString(2),cursor.getString(4),
                        cursor.getString(6),cursor.getString(5),cursor.getString(7),cursor.getString(9),cursor.getString(8));
                beerList.add(beer);
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching beer from Sqlite: " + beerList.toString());

        return beerList;
    }

    /**
     * Getting beers data from database
     * */
    public ArrayList<Beer> getMyFavoriteBeers() {

        ArrayList<Beer> beerList = new ArrayList<Beer>();
        String selectQuery = "SELECT * FROM " + TABLE_BEER + " INNER JOIN " + TABLE_FAVORITE + " ON beer.hb_id = favorite.hb_id";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() != 0) {
            // Move to first row
            cursor.moveToFirst();
            // Cursor parameter Order
            // id, name, trademark, style, ibu, alcohol, srm, description, others, contact , x, y
            //
            // Beer parameter order
            // String beer_id, String user_id, String name, String style, String trademark, String ibu,
            // String drb, String alcohol, String description, String contact, String others
            Beer beer;
            HashMap<String, String> userDetails = getUserDetails();
            String unique_id = userDetails.get(KEY_UID);
            beer = new Beer(cursor.getString(0),unique_id,cursor.getString(1),cursor.getString(3),cursor.getString(2),
                    cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(9),
                    cursor.getString(8));
            beerList.add(beer);
            while (cursor.moveToNext()) {

                beer = new Beer(cursor.getString(0),unique_id,cursor.getString(1),cursor.getString(3),cursor.getString(2),
                        cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7),cursor.getString(9),
                        cursor.getString(8));
                beerList.add(beer);
            }
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching beer from Sqlite: " + beerList.toString());

        return beerList;
    }

    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteBeers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_BEER, null, null);
        db.close();

        Log.d(TAG, "Deleted all user info from sqlite");
    }

    public void deleteBeerById(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        //db.delete
        db.delete(TABLE_BEER, "id=?", new String[]{id});
        db.close();
    }

    public boolean existsBeer(String id) {

        ArrayList<Beer> beerList = new ArrayList<Beer>();
        String selectQuery = "SELECT * FROM " + TABLE_BEER + " WHERE id =" + id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        return cursor.getCount() > 0;
    }

    public void addProfile(String hb_id, String contact, String geo_x, String geo_y, String whatsapp,
                           String facebook, String publicEmail, String email) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILE_ID_HB, hb_id);
        values.put(PROFILE_CONTACT_INFO, contact);
        values.put(PROFILE_GEO_X, geo_x);
        values.put(PROFILE_GEO_Y, geo_y);
        values.put(PROFILE_WHATSAPP, whatsapp);
        values.put(PROFILE_FACEBOOK, facebook);
        values.put(PROFILE_PUBLIC_EMAIL, publicEmail);
        values.put(PROFILE_EMAIL, email);


        // Inserting Row
        long id = db.insert(TABLE_PROFILE, null, values);
        db.close(); // Closing database connection

        Log.d(TAG, "Homebrewer profile inserted into sqlite: " + id);
    }

    /**
     * Getting user data from database
     * */
    public HashMap<String, String> getProfileDetails(String hb_id) {
        HashMap<String, String> profile = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_PROFILE + " WHERE hb_id = "+ hb_id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() != 0) {
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                profile.put("hb_id", cursor.getString(1));
                profile.put("contact", cursor.getString(2));
                profile.put("geo_x", cursor.getString(3));
                profile.put("geo_y", cursor.getString(4));
                profile.put("whatsapp", cursor.getString(5));
                profile.put("facebook", cursor.getString(6));
                profile.put("publicEmail", cursor.getString(7));
            }
        }
        cursor.close();
        db.close();
        // return profile
        Log.d(TAG, "Fetching user from Sqlite: " + profile.toString());

        return profile;
    }

    public void deleteProfiles() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_PROFILE, null, null);
        db.close();

        Log.d(TAG, "Deleted all profile info from sqlite");
    }

    public boolean addHBtoFavorites(String hb_id) {

        if(!existsFavoriteHB(hb_id)) {

            SQLiteDatabase db = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(FAVORITE_ID_HB,hb_id);


            // Inserting Row
            long id = db.insert(TABLE_FAVORITE,null,values);
            db.close(); // Closing database connection

            Log.d(TAG,"Homebrewer favorite inserted into sqlite: " + id);
            return true;
        }
        return false;
    }

    private boolean existsFavoriteHB(String hb_id) {

        ArrayList<Beer> beerList = new ArrayList<Beer>();
        String selectQuery = "SELECT * FROM " + TABLE_FAVORITE + " WHERE id =" + hb_id;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        return cursor.getCount() > 0;
    }

    public HashMap<String,String> getMyProfileDetails() {

        String myEmail = "";
        HashMap<String, String> user  = new HashMap<String, String>();
        String selectQuery = "SELECT email FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() != 0) {
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                myEmail = cursor.getString(0);
            }
        }
        cursor.close();

        // Get profile filtering by email

        HashMap<String, String> profile = new HashMap<String, String>();
        selectQuery = "SELECT * FROM " + TABLE_PROFILE + " WHERE email = '" +myEmail + "'";


         cursor = db.rawQuery(selectQuery, null);
        if(cursor.getCount() != 0) {
            // Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                profile.put("hb_id", cursor.getString(1));
                profile.put("contact", cursor.getString(2));
                profile.put("geo_x", cursor.getString(3));
                profile.put("geo_y", cursor.getString(4));
                profile.put("whatsapp", cursor.getString(5));
                profile.put("facebook", cursor.getString(6));
                profile.put("publicEmail", cursor.getString(7));
            }
        }
        cursor.close();
        db.close();
        // return profile
        Log.d(TAG, "Fetching user from Sqlite: " + profile.toString());

        return profile;

    }


    public void addMyProfile(String contacto, String latitud, String longitud, String whatsapp, String facebook, String email, String email1) {


    }
}