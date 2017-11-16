package com.superlogico.birraviso;

/**
 * Created by daniel.benitez on 9/26/2017.
 */

public class DataHolder {
    private static final DataHolder ourInstance = new DataHolder();
    private boolean favoritesMode;
    private boolean editProfile;
    private boolean syncAllBeerNeeded;
    private boolean homebrewerMapTurnedOn;

    public static DataHolder getInstance() {
        return ourInstance;
    }

    private DataHolder() {
    }

    public boolean isFavoritesMode(){
        return favoritesMode;
    }

    public boolean isSyncAllBeerNeeded() {
        return syncAllBeerNeeded;
    }

    public void setSyncAllBeerNeeded(boolean syncAllBeerNeeded) {
        this.syncAllBeerNeeded = syncAllBeerNeeded;
    }

    public void setFavoriteMode(boolean favoriteMode){
        this.favoritesMode = favoriteMode;
    }

    public void setEditProfile(boolean editProfile) {
        this.editProfile = editProfile;
    }

    public boolean isEditProfile(){
        return editProfile;
    }

    public boolean isHomebrewerMapTurnedOn() {
        return homebrewerMapTurnedOn;
    }

    public void setHomebrewerMapTurnedOn(boolean homebrewerMapTurnedOn) {
        this.homebrewerMapTurnedOn = homebrewerMapTurnedOn;
    }
}
