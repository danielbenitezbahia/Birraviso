package com.superlogico.birraviso.adapter;

/**
 * Created by Daniel on 16/2/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.superlogico.birraviso.model.Beer;

import java.util.ArrayList;
import java.util.List;

public class DBAdapter extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "beerDetails";
    // Beer table name
    private static final String TABLE_BEERS = "beers";
    // Beer Table Columns names
    private static final String KEY_ID = "id";
    private static final String USER_ID = "user_id";
    private static final String NOMBRE = "nombre";
    private static final String MARCA = "marca";
    private static final String ESTILO = "estilo";
    private static final String IBU = "ibu";
    private static final String ALCOHOL = "alcohol";
    private static final String DRB = "drm";
    private static final String CONTACTO = "contacto";
    private static final String DESCRIPCION = "descripcion";
    private static final String OTROS = "otros";


    public DBAdapter(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_BEERS + "("
        + KEY_ID + " INTEGER PRIMARY KEY," + USER_ID + " TEXT," + NOMBRE + " TEXT"
        + MARCA + " TEXT" + ESTILO + " TEXT" + IBU + " TEXT"+ ALCOHOL + " TEXT"+ DRB + " TEXT"+ CONTACTO +
                " TEXT"+ DESCRIPCION + " TEXT"+ OTROS + " TEXT)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
// Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BEERS);
// Creating tables again
        onCreate(db);
    }
    // Adding new beer
    public void addBeer(Beer beer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ID, beer.getUser_id());
        values.put(NOMBRE, beer.getName());
        values.put(MARCA, beer.getTrademark());
        values.put(ESTILO, beer.getStyle());
        values.put(IBU, beer.getIbu());
        values.put(ALCOHOL, beer.getAlcohol());
        values.put(DRB, beer.getDrb());
        values.put(CONTACTO, beer.getContact());
        values.put(DESCRIPCION, beer.getDescription());
        values.put(OTROS, beer.getOthers());


// Inserting Row
        db.insert(TABLE_BEERS, null, values);
        db.close(); // Closing database connection
    }
    // Getting one beer
    public Beer getBeer(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BEERS, new String[]{KEY_ID,
                        USER_ID, MARCA, ESTILO, ALCOHOL, DRB, DESCRIPCION, IBU, CONTACTO, OTROS}, KEY_ID + "=?",
        new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        Beer contact = new Beer(cursor.getString(0),
                cursor.getString(1), cursor.getString(2),
                cursor.getString(3), cursor.getString(4),
                cursor.getString(5), cursor.getString(6),
                cursor.getString(7), cursor.getString(8),
                cursor.getString(9),
                cursor.getString(10));
// return beer
        return contact;
    }
    // Getting All Beers
    public List<Beer> getAllBeers() {
        List<Beer> beerList = new ArrayList<Beer>();
// Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_BEERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Beer beer = new Beer();
                beer.setId(cursor.getString(0));
                beer.setUser_id(cursor.getString(1));
                beer.setName(cursor.getString(2));
                beer.setTrademark(cursor.getString(3));
                beer.setStyle(cursor.getString(4));
                beer.setIbu(cursor.getString(5));
                beer.setAlcohol(cursor.getString(6));
                beer.setDrb(cursor.getString(7));
                beer.setContact(cursor.getString(8));
                beer.setDescription(cursor.getString(9));
                beer.setOthers(cursor.getString(10));

                beerList.add(beer);
            } while (cursor.moveToNext());
        }

        return beerList;
    }
    // Getting shops Count
    public int getBeersCount() {
        String countQuery = "SELECT * FROM " + TABLE_BEERS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        cursor.close();

// return count
        return cursor.getCount();
    }
    // Updating a beer
    public int updateBeer(Beer beer) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_ID, beer.getUser_id());
        values.put(NOMBRE, beer.getName());
        values.put(MARCA, beer.getTrademark());
        values.put(ESTILO, beer.getStyle());
        values.put(IBU, beer.getIbu());
        values.put(ALCOHOL, beer.getAlcohol());
        values.put(DRB, beer.getDrb());
        values.put(CONTACTO, beer.getContact());
        values.put(DESCRIPCION, beer.getDescription());
        values.put(OTROS, beer.getOthers());

// updating row
        return db.update(TABLE_BEERS, values, KEY_ID + " = ?",
        new String[]{String.valueOf(beer.getId())});
    }

    // Deleting a beer
    public void deleteBeer(Beer beer) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BEERS, KEY_ID + " = ?",
        new String[] { String.valueOf(beer.getId()) });
        db.close();
    }
}
