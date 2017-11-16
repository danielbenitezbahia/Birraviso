package com.superlogico.birraviso.model;

/**
 * Created by daniel.benitez on 2/15/2017.
 */

public class HomeBrewer {
    private String id, hb_id, contact, geo_x, geo_y, facebook, email, whatsapp;
    private boolean isSelected, isVisible;

    public HomeBrewer() {
    }

    public HomeBrewer(String id, String hb_id, String contact, String geo_x, String geo_y, String facebook, String email, String whatsapp) {
        this.id = id;
        this.hb_id = hb_id;
        this.contact = contact;
        this.geo_x = geo_x;
        this.geo_y = geo_y;
        this.facebook = facebook;
        this.email = email;
        this.whatsapp = whatsapp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHb_id() {
        return hb_id;
    }

    public void setHb_id(String hb_id) {
        this.hb_id = hb_id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGeo_x() {
        return geo_x;
    }

    public void setGeo_x(String geo_x) {
        this.geo_x = geo_x;
    }

    public String getGeo_y() {
        return geo_y;
    }

    public void setGeo_y(String geo_y) {
        this.geo_y = geo_y;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWhatsapp() {
        return whatsapp;
    }

    public void setWhatsapp(String whatsapp) {
        this.whatsapp = whatsapp;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
