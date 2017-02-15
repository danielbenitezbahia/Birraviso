package com.superlogico.birraviso.model;

/**
 * Created by daniel.benitez on 2/15/2017.
 */

public class beer {
    private String name, style, trademark, ibu, drb, alcohol;

    public beer() {
    }

    public beer(String name, String style, String trademark, String ibu, String drb, String alcohol) {
        this.name = name;
        this.style = style;
        this.trademark = trademark;
        this.ibu = ibu;
        this.drb = drb;
        this.alcohol = alcohol;
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
}
