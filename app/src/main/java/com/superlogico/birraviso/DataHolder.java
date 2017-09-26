package com.superlogico.birraviso;

/**
 * Created by daniel.benitez on 9/26/2017.
 */

public class DataHolder {
    private static final DataHolder ourInstance = new DataHolder();
    private boolean favoritesMode;

    public static DataHolder getInstance() {
        return ourInstance;
    }

    private DataHolder() {
    }

    public boolean isFavoritesMode(){
        return favoritesMode;
    }

    public void setFavoriteMode(boolean favoriteMode){
        this.favoritesMode = favoriteMode;
    }
}
