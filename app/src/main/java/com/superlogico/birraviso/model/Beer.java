package com.superlogico.birraviso.model;

/**
 * Created by daniel.benitez on 2/15/2017.
 */

public class Beer {
    private String id, user_id, name, style, trademark, ibu, drb, alcohol, description, contact, others;
    private boolean isSelected, isVisible;

    public Beer() {
    }

    public Beer(String id, String user_id, String name, String style, String trademark, String ibu, String drb, String alcohol, String description, String contact, String others) {
        this.id = id;
        this.user_id = user_id;
        this.name = name;
        this.style = style;
        this.trademark = trademark;
        this.ibu = ibu;
        this.drb = drb;
        this.alcohol = alcohol;
        this.description = description;
        this.contact = contact;
        this.others = others;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStyle() {
        return style;
    }

    public void setStyle(String style) {
        this.style = style;
    }

    public String getTrademark() {
        return trademark;
    }

    public void setTrademark(String trademark) {
        this.trademark = trademark;
    }

    public String getIbu() {
        return ibu;
    }

    public void setIbu(String ibu) {
        this.ibu = ibu;
    }

    public String getDrb() {
        return drb;
    }

    public void setDrb(String drb) {
        this.drb = drb;
    }

    public String getAlcohol() {
        return alcohol;
    }

    public void setAlcohol(String alcohol) {
        this.alcohol = alcohol;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getOthers() {
        return others;
    }

    public void setOthers(String others) {
        this.others = others;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSelected(){
        return this.isSelected;
    }
    public void setSelected(boolean selected){
        this.isSelected = selected;
    }

    public boolean isVisible(){
        return isVisible;
    }

    public void setVisible(boolean visible){
        this.isVisible = visible;
    }
}
