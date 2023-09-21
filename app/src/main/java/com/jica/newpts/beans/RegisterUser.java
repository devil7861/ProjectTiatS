package com.jica.newpts.beans;

import com.google.firebase.Timestamp;

public class RegisterUser {
    private int u_idx;
    private String u_id;
    private String u_name;
    private Timestamp u_date;
    private String u_photo;

    public int getU_idx() {
        return u_idx;
    }

    public void setU_idx(int u_idx) {
        this.u_idx = u_idx;
    }

    public String getU_id() {
        return u_id;
    }

    public void setU_id(String u_id) {
        this.u_id = u_id;
    }

    public String getU_name() {
        return u_name;
    }

    public void setU_name(String u_name) {
        this.u_name = u_name;
    }

    public Timestamp getU_date() {
        return u_date;
    }

    public void setU_date(Timestamp u_date) {
        this.u_date = u_date;
    }

    public String getU_photo() {
        return u_photo;
    }

    public void setU_photo(String u_photo) {
        this.u_photo = u_photo;
    }
}
