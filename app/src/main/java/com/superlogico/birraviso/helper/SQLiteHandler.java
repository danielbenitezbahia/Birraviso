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
    // Login Table Columns names
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
    private static final String BEER_UID = "unique_id";


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
                + BEER_ID + " INTEGER PRIMARY KEY," + BEER_NAME + " TEXT,"
                + BEER_TRADEMARK + " TEXT," + BEER_STYLE + " TEXT, "
                + BEER_IBU + " TEXT, " + BEER_ALCOHOL + " TEXT, "+ BEER_SMR + " TEXT, "
                + BEER_DESCRIPTION + " TEXT,"+ BEER_OTHERS + " TEXT," + BEER_CONTACT_INFO
                + " TEXT, "+ BEER_GEO_X + " TEXT, " + BEER_GEO_Y + " TEXT" + " )";
        db.execSQL(CREATE_BEER_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEER);

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
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));
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
                        String contact, String geo_x, String geo_y) {

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
        values.put(BEER_OTHERS, others);
        values.put(BEER_CONTACT_INFO, contact);
        values.put(BEER_GEO_X, geo_x);
        values.put(BEER_GEO_Y, geo_y);
        values.put(BEER_UID, uid);

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
        // Move to first row
        cursor.moveToFirst();
        Beer beer;
        beer = new Beer(cursor.getString(1), cursor.getString(2) , cursor.getString(3), cursor.getString(4), cursor.getString(5),
                cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10),
                cursor.getString(11));
        beerList.add(beer);
        while (cursor.moveToNext()) {

            beer = new Beer(cursor.getString(1), cursor.getString(2) , cursor.getString(3), cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10),
                    cursor.getString(11));
            beerList.add(beer);
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
        String selectQuery = "SELECT * FROM " + TABLE_BEER + " WHERE id";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        Beer beer;
        beer = new Beer(cursor.getString(1), cursor.getString(2) , cursor.getString(3), cursor.getString(4), cursor.getString(5),
                cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10),
                cursor.getString(11));
        beerList.add(beer);
        while (cursor.moveToNext()) {

            beer = new Beer(cursor.getString(1), cursor.getString(2) , cursor.getString(3), cursor.getString(4), cursor.getString(5),
                    cursor.getString(6), cursor.getString(7), cursor.getString(8), cursor.getString(9), cursor.getString(10),
                    cursor.getString(11));
            beerList.add(beer);
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
       // db.delete(TABLE_BEER, null, null);
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

}
